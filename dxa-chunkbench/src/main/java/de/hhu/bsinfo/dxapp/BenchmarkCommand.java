package de.hhu.bsinfo.dxapp;

import picocli.CommandLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.hhu.bsinfo.dxmem.benchmark.Benchmark;
import de.hhu.bsinfo.dxmem.benchmark.BenchmarkPhase;
import de.hhu.bsinfo.dxmem.benchmark.BenchmarkRunner;
import de.hhu.bsinfo.dxmem.benchmark.workload.FacebookA;
import de.hhu.bsinfo.dxmem.benchmark.workload.FacebookB;
import de.hhu.bsinfo.dxmem.benchmark.workload.FacebookC;
import de.hhu.bsinfo.dxmem.benchmark.workload.FacebookD;
import de.hhu.bsinfo.dxmem.benchmark.workload.FacebookE;
import de.hhu.bsinfo.dxmem.benchmark.workload.FacebookF;
import de.hhu.bsinfo.dxmem.benchmark.workload.MemVar;
import de.hhu.bsinfo.dxmem.benchmark.workload.YcsbA;
import de.hhu.bsinfo.dxmem.benchmark.workload.YcsbB;
import de.hhu.bsinfo.dxmem.benchmark.workload.YcsbC;
import de.hhu.bsinfo.dxmem.data.ChunkID;
import de.hhu.bsinfo.dxmem.data.ChunkIDRanges;
import de.hhu.bsinfo.dxram.lookup.overlay.storage.BarrierID;
import de.hhu.bsinfo.dxram.lookup.overlay.storage.BarrierStatus;
import de.hhu.bsinfo.dxram.util.NodeRole;
import de.hhu.bsinfo.dxutils.NodeID;

@CommandLine.Command(
        name = "ChunkBenchmark",
        customSynopsis = "@|bold ChunkBenchmark|@ @|yellow <nodeIdx> <otherNodes> " +
                "WORKLOAD|@ [...]",
        description = "Run a benchmark to evaluate the ChunkService with different workloads",
        subcommands = {
                FacebookA.class,
                FacebookB.class,
                FacebookC.class,
                FacebookD.class,
                FacebookE.class,
                FacebookF.class,
                MemVar.class,
                YcsbA.class,
                YcsbB.class,
                YcsbC.class,
        }
)
public class BenchmarkCommand implements Runnable, BenchmarkRunner {
    private static final Logger LOGGER = LogManager.getFormatterLogger(BenchmarkCommand.class.getSimpleName());

    private static final String NAMESERVICE_BARRIER_NAME = "CHKBM";

    @CommandLine.Parameters(
            index = "0",
            paramLabel = "<nodeIdx>",
            description = "Always set this to 0 when running the application.")
    private int m_nodeIdx;

    @CommandLine.Parameters(
            index = "1",
            paramLabel = "<otherNodes>",
            description = "Either a single number X (e.g. 2) to run the benchmark on any additional X peers or a " +
                    "comma separated list of hex NIDs (e.g. F3FA,B1D1) not including the current node")
    private String m_otherNodes;

    private final ChunkBenchmarkContext m_context;

    private List<Short> m_nodeOtherList;
    private int m_barrierId = BarrierID.INVALID_ID;

    public BenchmarkCommand(final ChunkBenchmarkContext p_context) {
        m_context = p_context;
    }

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    @Override
    public void runBenchmark(final Benchmark p_benchmark) {
        if (!bootstrapRootWithArgs()) {
            return;
        }

        syncBarrier();
        executeBenchmark(p_benchmark);
        syncBarrier();
        deleteBarrier();
        printStatistics();

        LOGGER.info("Finished chunk benchmark");
    }

    private boolean bootstrapRootWithArgs() {
        // only first peer has to bootstrap
        if (m_nodeIdx != 0) {
            LOGGER.debug("Not bootstrap peer (%d, skip bootstrapping", m_nodeIdx);
            return true;
        }

        LOGGER.debug("I am bootstrap peer");

        // reflect: total node count or list of NIDs
        int totalNodeCount;
        m_nodeOtherList = new ArrayList<>();

        try {
            totalNodeCount = Integer.parseInt(m_otherNodes);
        } catch (NumberFormatException e) {
            // try node list instead
            String[] tokens = m_otherNodes.split(",");

            for (String tok : tokens) {
                m_nodeOtherList.add(NodeID.parse(tok));
            }

            totalNodeCount = m_nodeOtherList.size();
        }

        List<Short> nodesAvail = m_context.getBootService().getOnlinePeerNodeIDs();

        if (m_nodeOtherList.isEmpty()) {
            if (totalNodeCount <= 0) {
                LOGGER.error("Invalid total node count specified: %d", totalNodeCount);
                return false;
            }

            // if no NIDs specified, pick available nodes instead (not counting current node)
            if (totalNodeCount > nodesAvail.size() - 1) {
                LOGGER.error("Not enough peers available (%d) to run benchmark with %d nodes", nodesAvail.size(),
                        totalNodeCount);
                return false;
            }

            int idx = 0;

            while (totalNodeCount > 0) {
                if (nodesAvail.get(idx) != m_context.getBootService().getNodeID()) {
                    m_nodeOtherList.add(nodesAvail.get(idx));
                    totalNodeCount--;
                }

                idx++;
            }
        } else {
            // check if all nodes are available
            for (short nid : m_nodeOtherList) {
                if (!m_context.getBootService().isNodeOnline(nid)) {
                    LOGGER.error("Node %s is not online", NodeID.toHexString(nid));
                    return false;
                }

                if (m_context.getBootService().getNodeRole(nid) != NodeRole.PEER) {
                    LOGGER.error("Node %s is not a peer", NodeID.toHexString(nid));
                    return false;
                }
            }
        }

        LOGGER.info("Bootstrapper: running chunk benchmark with nodes: %s", NodeID.nodeIDArrayToString(
                m_nodeOtherList));
        // bootstrapper deploys further applications on remote peers

        int nodeIdx = 1;

        for (short nid : m_nodeOtherList) {
            LOGGER.debug("Deploying benchmark to remote: %s", NodeID.toHexString(nid));

            String[] args = Arrays.copyOf(m_context.getAppArgs(), m_context.getAppArgs().length);
            args[0] = Integer.toString(nodeIdx);
            m_context.getApplicationService().startApplication(nid, ChunkBenchmark.class.getName(), args);
        }

        return true;
    }

    private BarrierStatus syncBarrier() {
        LOGGER.debug("syncBarrier enter");

        if (m_nodeIdx == 0) {
            if (m_barrierId == BarrierID.INVALID_ID) {
                // other peers + bootstrap peer
                m_barrierId = m_context.getSyncService().barrierAllocate(m_nodeOtherList.size() + 1);
                m_context.getNameserviceService().register(m_barrierId, NAMESERVICE_BARRIER_NAME);
            }
        } else {
            while (m_barrierId == BarrierID.INVALID_ID) {
                m_barrierId = (int) m_context.getNameserviceService().getChunkID(NAMESERVICE_BARRIER_NAME, -1);

                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) {
                }
            }
        }

        LOGGER.debug("syncBarrier");

        return m_context.getSyncService().barrierSignOn(m_barrierId, 0);
    }

    private ChunkIDRanges collectAvailableChunkIDs() {
        LOGGER.debug("Collecting chunk ranges from remote nodes");

        ChunkIDRanges ranges = new ChunkIDRanges();

        // collect from ALL nodes to also cover storage only instances which don't run this benchmark
        for (short nodeId : m_context.getBootService().getOnlinePeerNodeIDs()) {
            LOGGER.debug("Collect from %s", NodeID.toHexString(nodeId));
            ranges.add(m_context.getChunkService().cidStatus().getAllLocalChunkIDRanges(nodeId));
            ranges.add(m_context.getChunkService().cidStatus().getAllMigratedChunkIDRanges(nodeId));

            // remove index chunk
            ranges.remove(ChunkID.getChunkID(nodeId, 0));
        }

        LOGGER.debug("Collected chunk ranges for benchmark: %s", ranges);

        return ranges;
    }

    private void executeBenchmark(final Benchmark p_benchmark) {
        LOGGER.info("Executing benchmark '%s'", p_benchmark.getName());

        for (BenchmarkPhase phase : p_benchmark.getPhases()) {
            // collect chunk ranges before every phase to ensure that all chunks are available (including remote ones)
            // e.g. before load phase: existing chunks (of previous benchmark runs), load phase -> local on each node,
            // collect chunks from load phase to make them available in benchmark phase, benchmark phase with all
            // chunks
            ChunkIDRanges ranges = collectAvailableChunkIDs();

            LOGGER.info("Executing benchmark phase '%s'...", phase.getName());
            phase.execute(m_context, ranges);
            LOGGER.info("Results of benchmark phase '%s'...", phase.getName());
            phase.printResults(m_context);
        }

        LOGGER.info("Finished executing benchmark '%s'", p_benchmark.getName());
    }

    private void deleteBarrier() {
        if (m_nodeIdx == 0) {
            m_context.getNameserviceService().register(BarrierID.INVALID_ID, NAMESERVICE_BARRIER_NAME);
            m_context.getSyncService().barrierFree(m_barrierId);
        }
    }

    private void printStatistics() {
        m_context.getStatisticsService().getManager().printStatistics(System.out);
    }
}

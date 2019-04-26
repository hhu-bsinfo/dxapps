package de.hhu.bsinfo.dxapp;

import de.hhu.bsinfo.dxmem.benchmark.workload.*;
import picocli.CommandLine;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.hhu.bsinfo.dxmem.benchmark.Benchmark;
import de.hhu.bsinfo.dxmem.benchmark.BenchmarkPhase;
import de.hhu.bsinfo.dxmem.benchmark.BenchmarkRunner;
import de.hhu.bsinfo.dxmem.data.ChunkID;
import de.hhu.bsinfo.dxmem.data.ChunkIDRanges;
import de.hhu.bsinfo.dxram.lookup.overlay.storage.BarrierID;
import de.hhu.bsinfo.dxram.lookup.overlay.storage.BarrierStatus;
import de.hhu.bsinfo.dxutils.NodeID;

@CommandLine.Command(
        name = "ChunkBenchmark",
        customSynopsis = "@|bold ChunkBenchmark|@ @|yellow <isFirst> <useMultiOps> <isNodeList> <activeNodesPerPhase> " +
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
                YcsbCustom.class
        }
)
public class BenchmarkCommand implements Runnable, BenchmarkRunner {
    private static final Logger LOGGER = LogManager.getFormatterLogger(BenchmarkCommand.class.getSimpleName());

    private static final String NAMESERVICE_BARRIER_NAME = "CHKBM";

    @CommandLine.Parameters(
            index = "0",
            paramLabel = "<isFirst>",
            description = "Always set this to true when running the application.")
    private boolean m_isFirst;

    @CommandLine.Parameters(
            index = "1",
            paramLabel = "<useMultiOps>",
            description = "Set this to true, if you want the benchmark to use MULTI-GET/MULTI-PUT operations.")
    private boolean m_useMultiOps;

    @CommandLine.Parameters(
            index = "2",
            paramLabel = "<isNodeList>",
            description = "Set this to true if you are using the node list in the next argument. False if using " +
                    "node counts.")
    private boolean m_isNodeList;

    @CommandLine.Parameters(
            index = "3",
            paramLabel = "<activeNodesPerPhase>",
            description = "A list of node counts determining the number of nodes to run on each phase, e.g. 2:3 " +
                    "means run with 2 nodes on first phase and three nodes on second phase. Or, a list of phases with" +
                    " a list of nids, e.g. C0C3,FA3A:C0C3 means run first phase on nodes C0C3 and FA3A and second" +
                    " phase on node C0C3")
    private String m_activeNodesPerPhaseStr;

    private final ChunkBenchmarkContext m_context;

    private List<List<Short>> m_activeNodesPerPhase;
    private List<Short> m_participatingNodes;

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
        if (!argumentBootstrap(p_benchmark)) {
            return;
        }

        if (m_isFirst) {
            deployRemotes();
        }

        executeBenchmark(p_benchmark);
        printStatistics();

        LOGGER.info("Finished chunk benchmark");
    }

    private boolean argumentBootstrap(final Benchmark p_benchmark) {
        BootstrappingNodesUtil bootstrap = new BootstrappingNodesUtil(m_context.getBootService());

        m_context.setUseMultiOps(m_useMultiOps);

        if (m_isNodeList) {
            m_activeNodesPerPhase = bootstrap.bootstrapNodesListParameters(m_activeNodesPerPhaseStr);
        } else {
            m_activeNodesPerPhase = bootstrap.bootstrapNodesCountParameters(m_activeNodesPerPhaseStr);
        }

        if (m_activeNodesPerPhase == null) {
            LOGGER.error("Bootstrapping benchmark failed, abort");
            return false;
        }

        m_participatingNodes = BootstrappingNodesUtil.getParticipatingNodes(m_activeNodesPerPhase);

        if (m_activeNodesPerPhase.size() != p_benchmark.getPhases().size()) {
            LOGGER.error("Phase count of benchmark (%d) does not match the specified active nodes per face argument " +
                    "list (%d)", p_benchmark.getPhases().size(), m_activeNodesPerPhase.size());
            return false;
        }

        return true;
    }

    private void deployRemotes() {
        // deploy to remotes
        for (short nid : m_participatingNodes) {
            if (nid != m_context.getBootService().getNodeID()) {
                LOGGER.debug("Deploying benchmark to remote: %s", NodeID.toHexString(nid));

                String[] args = Arrays.copyOf(m_context.getAppArgs(), m_context.getAppArgs().length);
                args[0] = "false";
                m_context.getApplicationService().startApplication(nid, ChunkBenchmark.class.getName(), args);
            }
        }
    }

    private void executeBenchmark(final Benchmark p_benchmark) {
        LOGGER.info("Executing benchmark '%s'", p_benchmark.getName());

        List<BenchmarkPhase> phases = p_benchmark.getPhases();

        for (int i = 0; i < phases.size(); i++) {
            BenchmarkPhase phase = phases.get(i);
            List<Short> nodesToExecuteOn = m_activeNodesPerPhase.get(i);

            syncBarrier();

            // execute on current node if in list, only
            if (nodesToExecuteOn.contains(m_context.getBootService().getNodeID())) {
                // collect chunk ranges before every phase to ensure that all chunks are available
                // (including remote ones) e.g. before load phase: existing chunks (of previous benchmark runs),
                // load phase -> local on each node, collect chunks from load phase to make them available in benchmark
                // phase, benchmark phase with all chunks
                ChunkIDRanges ranges = collectAvailableChunkIDs();

                LOGGER.info("Executing benchmark phase '%s'...", phase.getName());
                phase.execute(m_context, ranges);
                LOGGER.info("Results of benchmark phase '%s'...", phase.getName());
                phase.printResults(m_context);
            } else {
                LOGGER.info("Skipping benchmark phase '%s'", phase.getName());
            }
        }

        syncBarrier();
        deleteBarrier();

        LOGGER.info("Finished executing benchmark '%s'", p_benchmark.getName());
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

    private BarrierStatus syncBarrier() {
        LOGGER.debug("syncBarrier enter");

        if (m_isFirst) {
            if (m_barrierId == BarrierID.INVALID_ID) {
                // other peers + bootstrap peer
                m_barrierId = m_context.getSyncService().barrierAllocate(m_participatingNodes.size());
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

    private void deleteBarrier() {
        if (m_isFirst) {
            m_context.getNameserviceService().register(BarrierID.INVALID_ID, NAMESERVICE_BARRIER_NAME);
            m_context.getSyncService().barrierFree(m_barrierId);
        }
    }

    private void printStatistics() {
        m_context.getStatisticsService().getManager().printStatistics(System.out);
    }
}

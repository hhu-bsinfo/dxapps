package de.hhu.bsinfo.dxapp;

import picocli.CommandLine;

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
import de.hhu.bsinfo.dxutils.NodeID;

@CommandLine.Command(
        name = "ChunkBenchmark",
        customSynopsis = "@|bold ChunkBenchmark|@ @|yellow <benchmark node idx> <benchmark total nodes> " +
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
            paramLabel = "<benchmark node idx>",
            description = "Node idx for benchmark (0, 1, 2, ...) to identify benchmark nodes")
    private int m_benchmarkNodeId;

    @CommandLine.Parameters(
            index = "1",
            paramLabel = "<benchmark total nodes>",
            description = "Total nodes involved in benchmark")
    private int m_totalBenchmarkNodes;

    private final ChunkBenchmarkContext m_context;

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
        LOGGER.info("Running chunk benchmark, my node idx %d of %d nodes", m_benchmarkNodeId, m_totalBenchmarkNodes);

        if (m_benchmarkNodeId >= m_totalBenchmarkNodes) {
            LOGGER.error("Invalid parameter(s) specified, node idx >= total nodes");
            return;
        }

        syncBarrier();
        ChunkIDRanges ranges = collectAvailableChunkIDs();
        syncBarrier();
        executeBenchmark(p_benchmark, ranges);
        syncBarrier();

        LOGGER.info("Finished chunk benchmark");
    }

    private BarrierStatus syncBarrier() {
        LOGGER.debug("syncBarrier enter");

        if (m_benchmarkNodeId == 0) {
            if (m_barrierId == BarrierID.INVALID_ID) {
                m_barrierId = m_context.getSyncService().barrierAllocate(m_totalBenchmarkNodes);
                m_context.getNameserviceService().register(m_barrierId, NAMESERVICE_BARRIER_NAME);
            }
        } else {
            if (m_barrierId == BarrierID.INVALID_ID) {
                m_barrierId = (int) m_context.getNameserviceService().getChunkID(NAMESERVICE_BARRIER_NAME, -1);
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

        return ranges;
    }

    private void executeBenchmark(final Benchmark p_benchmark, final ChunkIDRanges p_ranges) {
        LOGGER.info("Executing benchmark '%s'", p_benchmark.getName());

        for (BenchmarkPhase phase : p_benchmark.getPhases()) {
            LOGGER.info("Executing benchmark phase '%s'...", phase.getName());
            phase.execute(m_context, p_ranges);
            LOGGER.info("Results of benchmark phase '%s'...", phase.getName());
            phase.printResults(m_context);
        }

        LOGGER.info("Finished executing benchmark '%s'", p_benchmark.getName());
    }
}

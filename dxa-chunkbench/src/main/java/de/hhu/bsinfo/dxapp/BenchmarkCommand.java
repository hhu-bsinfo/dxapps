package de.hhu.bsinfo.dxapp;

import picocli.CommandLine;

import de.hhu.bsinfo.dxmem.benchmark.BenchmarkBootstrapper;
import de.hhu.bsinfo.dxmem.benchmark.BenchmarkContext;
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

@CommandLine.Command(
        name = "benchmark",
        customSynopsis = "@|bold dxmem benchmark|@ @|yellow heapSize WORKLOAD|@ [...]",
        description = "Run a benchmark to evaluate DXMem with different workloads",
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
public class BenchmarkCommand implements Runnable, BenchmarkBootstrapper {
    private final ChunkBenchmarkContext m_context;

    public BenchmarkCommand(final ChunkBenchmarkContext p_context) {
        m_context = p_context;
    }

    @Override
    public void init() {

    }

    @Override
    public BenchmarkContext getContext() {
        return m_context;
    }

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }
}

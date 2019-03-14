package de.hhu.bsinfo.dxapp;

import de.hhu.bsinfo.dxmem.data.ChunkByteBuffer;
import de.hhu.bsinfo.dxram.app.Application;
import de.hhu.bsinfo.dxram.boot.BootService;
import de.hhu.bsinfo.dxram.chunk.ChunkLocalService;
import de.hhu.bsinfo.dxram.chunk.ChunkService;
import de.hhu.bsinfo.dxram.engine.DXRAMVersion;
import de.hhu.bsinfo.dxram.generated.BuildConfig;
import de.hhu.bsinfo.dxram.migration.LongRange;
import de.hhu.bsinfo.dxutils.NodeID;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GetBenchmark extends Application {

    private final Logger log = LogManager.getFormatterLogger(GetBenchmark.class);

    private static final String DEFAULT_TOTAL_SIZE = String.valueOf(1024 * 1024 * 32);

    private static final String DEFAULT_CHUNK_SIZE = String.valueOf(64);

    private static final String DEFAULT_DURATION_VALUE = String.valueOf(60);

    private static final String DEFAULT_INTERVAL_VALUE = String.valueOf(10);

    private static final String ARG_TOTAL = "total";

    private static final String ARG_CHUNK = "chunk";

    private static final String ARG_TARGET = "target";

    private static final String ARG_DURATION = "duration";

    private static final String ARG_INTERVAL = "interval";

    private static final String EMPTY_STRING = "";

    @Override
    public DXRAMVersion getBuiltAgainstVersion() {
        return BuildConfig.DXRAM_VERSION;
    }

    @Override
    public String getApplicationName() {
        return "GetBenchmark";
    }

    @Override
    public void main(final String[] p_args) {
        Options options = new Options();
        getOptions().forEach(options::addOption);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, p_args);
        } catch (ParseException e) {
            log.error("Application options could not be parsed", e);
            return;
        }

        ChunkService chunkService = getService(ChunkService.class);
        ChunkLocalService chunkLocalService = getService(ChunkLocalService.class);
        BootService bootService = getService(BootService.class);

        String totalSizeValue = cmd.getOptionValue(ARG_TOTAL, DEFAULT_TOTAL_SIZE);
        String chunkSizeValue = cmd.getOptionValue(ARG_CHUNK, DEFAULT_CHUNK_SIZE);
        String durationValue = cmd.getOptionValue(ARG_DURATION, DEFAULT_DURATION_VALUE);
        String intervalValue = cmd.getOptionValue(ARG_INTERVAL, DEFAULT_INTERVAL_VALUE);
        String targetValue = cmd.getOptionValue(ARG_TARGET, EMPTY_STRING);

        int totalSize = Integer.parseInt(totalSizeValue);
        int chunkSize = Integer.parseInt(chunkSizeValue);
        int numChunks = totalSize / chunkSize;
        long duration = Long.parseLong(durationValue) * 1000;
        int interval = Integer.parseInt(intervalValue) * 1000 * 1000;

        short target = targetValue.isEmpty() ? NetworkHelper.findStorageNode(bootService) : NodeID.parse(targetValue);

        log.info("Starting GET benchmark with target %X", target);

        LongRange range = ChunkHelper.createChunks(chunkService, chunkLocalService, chunkSize, numChunks, target);

        ThreadLocalRandom random = ThreadLocalRandom.current();

        String user = System.getProperty("user.name");
        File logDir = new File(
                String.format("/home/%s/dxlogs/migration/getbench-%d.csv", user, System.currentTimeMillis()));
        //noinspection ResultOfMethodCallIgnored
        logDir.getParentFile().mkdirs();

        try {
            FileWriter writer = new FileWriter(logDir);
            writer.append("timestamp,operation_time_nano\n");
            ChunkByteBuffer chunkBuffer;
            long chunkId;
            long benchmarkStartTime = System.currentTimeMillis();

            long opCount = 0;
            long sampleStart = System.nanoTime();
            long currentTime;
            while (System.currentTimeMillis() - benchmarkStartTime < duration) {
                // Create random chunk id from range
                chunkId = random.nextLong(range.getFrom(), range.getTo());
                chunkBuffer = new ChunkByteBuffer(chunkId, chunkSize);

                // Retrieve chunk and increment operation count
                chunkService.get().get(chunkBuffer);
                opCount++;

//                if (!chunkBuffer.isStateOk()) {
//                    log.warn("Chunk's %X state was not OK", chunkId);
//                }

                if (System.nanoTime() - sampleStart >= interval) {
                    writer.append(String.format("%d,%d\n", System.currentTimeMillis(), interval / opCount));
                    opCount = 0;
                    sampleStart = System.nanoTime();
                }
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            log.error("Couldn't write results to file");
        }
    }

    @Override
    public void signalShutdown() {

    }

    private static List<Option> getOptions() {
        return Arrays.asList(
                Option.builder(ARG_TOTAL).argName(ARG_TOTAL)
                        .hasArg()
                        .desc("The total amount of data to send in bytes")
                        .required(false)
                        .type(Integer.class)
                        .build(),
                Option.builder(ARG_CHUNK).argName(ARG_CHUNK)
                        .hasArg()
                        .desc("The size of a single chunk in bytes")
                        .required(false)
                        .type(Integer.class)
                        .build(),
                Option.builder(ARG_DURATION).argName(ARG_DURATION)
                        .hasArg()
                        .desc("The benchmark's duration in seconds")
                        .required(false)
                        .type(Integer.class)
                        .build(),
                Option.builder(ARG_TARGET).argName(ARG_TARGET)
                        .hasArg()
                        .desc("The target node's id")
                        .required(false)
                        .type(Short.class)
                        .build(),
                Option.builder(ARG_INTERVAL).argName(ARG_INTERVAL)
                        .hasArg()
                        .desc("The operation interval in milliseconds")
                        .required(false)
                        .type(Short.class)
                        .build()
        );
    }
}

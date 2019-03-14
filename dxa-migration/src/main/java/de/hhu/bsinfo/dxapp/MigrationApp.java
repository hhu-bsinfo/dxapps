package de.hhu.bsinfo.dxapp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import de.hhu.bsinfo.dxram.migration.LongRange;
import de.hhu.bsinfo.dxutils.NodeID;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.hhu.bsinfo.dxram.app.Application;
import de.hhu.bsinfo.dxram.boot.BootService;
import de.hhu.bsinfo.dxram.chunk.ChunkLocalService;
import de.hhu.bsinfo.dxram.chunk.ChunkService;
import de.hhu.bsinfo.dxram.engine.DXRAMVersion;
import de.hhu.bsinfo.dxram.generated.BuildConfig;
import de.hhu.bsinfo.dxram.migration.MigrationService;
import de.hhu.bsinfo.dxram.migration.MigrationStatus;
import de.hhu.bsinfo.dxram.migration.MigrationTicket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MigrationApp extends Application {

    private final Logger log = LogManager.getFormatterLogger(MigrationApp.class);

    private static final String DEFAULT_TOTAL_SIZE = String.valueOf(1024 * 1024 * 32);

    private static final String DEFAULT_CHUNK_SIZE = String.valueOf(64);

    private static final String DEFAULT_ITERATION_COUNT = String.valueOf(1);

    private static final String ARG_TOTAL = "total";

    private static final String ARG_CHUNK = "chunk";

    private static final String ARG_ITERATIONS = "iterations";

    private static final String ARG_TARGET = "target";

    private static final String EMPTY_STRING = "";

    @Override
    public DXRAMVersion getBuiltAgainstVersion() {
        return BuildConfig.DXRAM_VERSION;
    }

    @Override
    public String getApplicationName() {
        return "MigrationApp";
    }

    @Override
    public void main(final String[] p_args) {
        Options options = new Options();
        getOptions().forEach(options::addOption);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, p_args);
        } catch (ParseException e) {
            log.error("Application options could not be parsed", e);
            return;
        }

        ChunkService chunkService = getService(ChunkService.class);
        ChunkLocalService chunkLocalService = getService(ChunkLocalService.class);
        MigrationService migrationService = getService(MigrationService.class);
        BootService bootService = getService(BootService.class);

        String totalSizeValue = cmd.getOptionValue(ARG_TOTAL, DEFAULT_TOTAL_SIZE);
        String chunkSizeValue = cmd.getOptionValue(ARG_CHUNK, DEFAULT_CHUNK_SIZE);
        String iterationsValue = cmd.getOptionValue(ARG_ITERATIONS, DEFAULT_ITERATION_COUNT);
        String targetValue = cmd.getOptionValue(ARG_TARGET, EMPTY_STRING);

        int iterations = Integer.parseInt(iterationsValue);
        int totalSize = Integer.parseInt(totalSizeValue);
        int chunkSize = Integer.parseInt(chunkSizeValue);
        int numChunks = totalSize / chunkSize;

        long[] creationTimes = new long[iterations];
        long[] migrationTimes = new long[iterations];

        short target = targetValue.isEmpty() ? NetworkHelper.findStorageNode(bootService) : NodeID.parse(targetValue);

        log.info("Starting migration to %X", target);

        long then;
        for (int i = 0; i < iterations; i++) {
            then = System.currentTimeMillis();
            LongRange range = ChunkHelper.createChunks(chunkService, chunkLocalService, chunkSize, numChunks);
            creationTimes[i] = System.currentTimeMillis() - then;

            log.info("Migrating chunk range [%X , %X]", range.getFrom(), range.getTo());

            MigrationStatus status;
            MigrationTicket ticket;

            then = System.currentTimeMillis();
            ticket = migrationService.migrateRange(range.getFrom(), range.getTo(), target);
            status = migrationService.await(ticket);
            migrationTimes[i] = System.currentTimeMillis() - then;

            if (status == MigrationStatus.ERROR) {
                log.warn("Iteration %d was not successful", i);
            }
        }

        int workerCount = migrationService.getWorkerCount();

        String user = System.getProperty("user.name");

        File logDir = new File(
                String.format("/home/%s/dxlogs/migration/migration_%d_%d_%d-%d.csv", user, workerCount, iterations,
                        numChunks, System.currentTimeMillis()));
        logDir.getParentFile().mkdirs();

        try {
            FileWriter writer = new FileWriter(logDir);

            writer.append("iteration,creation_time,migration_time,chunk_count\n");
            for (int i = 0; i < iterations; i++) {
                writer.append(String.format("%d,%d,%d,%d\n", i, creationTimes[i], migrationTimes[i], numChunks));
            }

            writer.flush();
            writer.close();

            log.info("Results written to %s", logDir.getAbsolutePath());
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
                Option.builder(ARG_ITERATIONS).argName(ARG_ITERATIONS)
                        .hasArg()
                        .desc("The iteration count")
                        .required(false)
                        .type(Integer.class)
                        .build(),
                Option.builder(ARG_TARGET).argName(ARG_TARGET)
                        .hasArg()
                        .desc("The target node")
                        .required(false)
                        .type(Short.class)
                        .build()
        );
    }
}

package de.escapefromtarkov.eftwikicrawler.adapter.jvm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.NumberFormat;

public class JvmUtil {

    private static Logger logger = LogManager.getLogger(JvmUtil.class);

    public static void logMemoryOutput() {
        Runtime runtime = Runtime.getRuntime();
        final NumberFormat format = NumberFormat.getInstance();
        final long maxMemory = runtime.maxMemory();
        final long allocatedMemory = runtime.totalMemory();
        final long freeMemory = runtime.freeMemory();
        final long mb = 1024 * 1024;
        final String mega = " MB";
        logger.info("========================== Memory Info ==========================");
        logger.info("Free memory: " + format.format(freeMemory / mb) + mega);
        logger.info("Allocated memory: " + format.format(allocatedMemory / mb) + mega);
        logger.info("Max memory: " + format.format(maxMemory / mb) + mega);
        logger.info("Total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / mb) + mega);
        logger.info("=================================================================\n");
    }

}
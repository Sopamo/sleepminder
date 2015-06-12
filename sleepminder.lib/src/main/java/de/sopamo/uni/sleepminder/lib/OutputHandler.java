package de.sopamo.uni.sleepminder.lib;

/**
 * Create your own implementation for storing the monitored data
 */
public interface OutputHandler {

    /**
     * This method gets called when new monitoring data is available and should be persisted
     * Note that the data might not be complete, but arrives in chunks during monitoring to prevent data loss
     * if the monitoring gets interrupted by the system.
     * So always append new data, never override existing data.
     *
     * @param data The monitoring data
     * @param identifier A unique string which tells you for which recording this data is
     */
    void saveData(String data, String identifier);
}

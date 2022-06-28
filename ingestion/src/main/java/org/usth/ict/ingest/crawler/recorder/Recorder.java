package org.usth.ict.ingest.crawler.recorder;

import org.usth.ict.ingest.crawler.storage.Storage;

import java.util.HashMap;

public interface Recorder {
    void setup(HashMap config);
    void setup(Storage store);
    void record(Object info, HashMap meta);
    void info(Object carrier, HashMap meta);
}

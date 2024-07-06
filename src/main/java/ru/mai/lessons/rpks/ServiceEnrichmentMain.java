package ru.mai.lessons.rpks;

import lombok.extern.slf4j.Slf4j;
import ru.mai.lessons.rpks.impl.ConfigReaderImpl;
import ru.mai.lessons.rpks.impl.ServiceEnrichment;

@Slf4j
public class ServiceEnrichmentMain {
    public static void main(String[] args) {
        log.info("Start service Enrichment");
        ConfigReader configReader = new ConfigReaderImpl();
        Service service = new ServiceEnrichment();
        service.start(configReader.loadConfig());
        log.info("Terminate service Enrichment");
    }
}
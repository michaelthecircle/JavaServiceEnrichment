package ru.mai.lessons.rpks;

import org.bson.Document;

public interface MongoDBClientEnricher {
    public Document getEnrichmentDocument(Document document);
}

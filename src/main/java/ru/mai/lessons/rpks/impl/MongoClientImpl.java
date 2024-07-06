package ru.mai.lessons.rpks.impl;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import ru.mai.lessons.rpks.MongoDBClientEnricher;

@Slf4j
public class MongoClientImpl implements MongoDBClientEnricher {
    MongoCollection<Document> collection;
    MongoClient mongoClient;

    public MongoClientImpl(Config config) {
        mongoClient = MongoClients.create(config.getString("mongo.connectionString"));
        MongoDatabase database = mongoClient.getDatabase(config.getString("mongo.database"));
        collection = database.getCollection(config.getString("mongo.collection"));
        log.info("Connected to MongoDB database: {} and collection: {}", database, collection);
    }

    @Override
    public Document getEnrichmentDocument(Document document) {
        Document result = collection.find(document).sort(Sorts.descending("_id")).first();
        if (result != null) {
            log.debug("Found document: {}", result.toJson());
        } else {
            log.debug("No document found matching: {}", document.toJson());
        }

        return result;
    }
}

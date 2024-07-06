package ru.mai.lessons.rpks.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import ru.mai.lessons.rpks.MongoDBClientEnricher;
import ru.mai.lessons.rpks.RuleProcessor;
import ru.mai.lessons.rpks.model.Message;
import ru.mai.lessons.rpks.model.Rule;

import java.util.Optional;


@Slf4j
public class RuleProcessorImpl implements RuleProcessor {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MongoDBClientEnricher mongoClient;

    public RuleProcessorImpl(Config config) {
        this.mongoClient = new MongoClientImpl(config);
    }

    @Override
    public Message processing(Message message, Rule[] rules) {
        if (rules == null || rules.length == 0) {
            log.debug("No rules provided, returning original message");
            return message;
        }
        try {
            JsonNode node = objectMapper.readTree(message.getValue());
            for (Rule rule : rules) {
                String fieldName = rule.getFieldName();
                String fieldValue = rule.getFieldValue();
                String fieldNameEnrichment = rule.getFieldNameEnrichment();

                Optional<Document> foundEnrichment = Optional.ofNullable(mongoClient
                        .getEnrichmentDocument(new Document(fieldNameEnrichment, fieldValue)));
                if (foundEnrichment.isPresent()){
                    log.debug("Enrichment document found for field: {}, updating value", fieldName);
                    ((ObjectNode) node).set(fieldName, objectMapper.readTree(foundEnrichment.get().toJson()));
                } else {
                    String defaultValue = rule.getFieldValueDefault();
                    ((ObjectNode) node).put(fieldName, defaultValue);
                }
            }
            message.setValue(node.toString());
            log.info("Message processed successfully");
        }catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return message;
    }
}

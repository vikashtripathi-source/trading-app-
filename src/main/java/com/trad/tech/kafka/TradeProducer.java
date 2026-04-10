package com.trad.tech.kafka;

import com.trad.tech.model.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TradeProducer {
    
    private static final Logger logger = LoggerFactory.getLogger(TradeProducer.class);
    
    private final KafkaTemplate<String, Trade> kafkaTemplate;
    
    @Value("${kafka.topics.trade-events}")
    private String tradeEventsTopic;
    
    @Value("${kafka.topics.trade-validation}")
    private String tradeValidationTopic;
    
    public TradeProducer(KafkaTemplate<String, Trade> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    
    public void sendTrade(Trade trade) {
        try {
            kafkaTemplate.send(tradeEventsTopic, trade);
            logger.info("Trade event sent: {}", trade);
        } catch (Exception e) {
            logger.error("Error sending trade event: {}", e.getMessage(), e);
        }
    }
    
    public void sendTradeValidation(Trade trade) {
        try {
            kafkaTemplate.send(tradeValidationTopic, trade);
            logger.info("Trade validation event sent: {}", trade);
        } catch (Exception e) {
            logger.error("Error sending trade validation event: {}", e.getMessage(), e);
        }
    }
}

package com.ostapchuk.email.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsumerService {

    private final SenderService senderService;

    @KafkaListener(topics = "${spring.kafka.template.email-topic}")
    public void receive(final ConsumerRecord<?, ?> consumerRecord) {
        senderService.send("kera.ostapchuk@mail.ru",
                consumerRecord.topic() + ":" + consumerRecord.value(),
                consumerRecord.topic());
        log.info("received [topic:{}, partition:{}, offset:{}] value:{}", consumerRecord.topic(),
                consumerRecord.partition(), consumerRecord.offset(), consumerRecord.value());
    }
}

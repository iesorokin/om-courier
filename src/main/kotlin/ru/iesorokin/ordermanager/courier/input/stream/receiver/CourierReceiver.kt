package ru.iesorokin.ordermanager.courier.input.stream.receiver

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service
import ru.iesorokin.ordermanager.courier.config.START_DELIVERY
import ru.iesorokin.ordermanager.courier.core.service.delivery.DeliveryService
import ru.iesorokin.ordermanager.courier.input.dto.DeliveryTaskMessage

private val log = KotlinLogging.logger {}

@Service
class CourierReceiver(
        private val deliveryService: DeliveryService,
        @Value("\${banker.consumer.default.maxRetry}")
        private val maxRetry: Long
) {

    @StreamListener(START_DELIVERY)
    fun delivery(
            @Payload message: DeliveryTaskMessage,
            @Header(name = X_DEATH_HEADER, required = false) death: Map<Any, Any?>?
    ) {
        log.inputMessage(START_DELIVERY, message)
        if (death.isDeadLetterCountOverflown(maxRetry)) {
            log.deadLetterCountOverflownError(maxRetry, START_DELIVERY, message)
            return
        }

        deliveryService.delivery(message)
    }
}

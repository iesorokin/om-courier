package ru.iesorokin.ordermanager.courier.output.stream.sender

import mu.KotlinLogging
import org.springframework.messaging.Message
import org.springframework.stereotype.Service
import ru.iesorokin.ordermanager.courier.config.MessageQueueSource
import ru.iesorokin.ordermanager.courier.error.SendMessageException
import ru.iesorokin.ordermanager.courier.input.dto.DeliveryTaskMessage

private val log = KotlinLogging.logger {}

@Service
class CourierStatusSender(
        private val messageQueueSource: MessageQueueSource
){

    /**
     * @throws SendMessageException describing the problem while sending unified message
     */
    fun sendDeliverySuccess(task: DeliveryTaskMessage?) {
        val message = org.springframework.messaging.support.MessageBuilder
                .withPayload(task)
                .build()
        sendDeliverySuccess(message)
    }

    private fun sendDeliverySuccess(message: Message<*>) {
        messageQueueSource.deluverySuccess().sendOrThrow(message) { e ->
            SendMessageException("Message: $message. Exception: $e")
        }
        log.info { "Message was sent to deluverySuccess exchange. $message" }
    }

}
package ru.iesorokin.ordermanager.courier.core.service.delivery

import mu.KotlinLogging
import org.springframework.stereotype.Service
import ru.iesorokin.ordermanager.courier.core.repository.DeliveryTaskFailedRepository
import ru.iesorokin.ordermanager.courier.input.dto.DeliveryTaskMessage
import ru.iesorokin.ordermanager.courier.output.stream.sender.CourierStatusSender

private val log = KotlinLogging.logger {}

@Service
class DeliveryService(
        private val deliveryTaskFailedRepository: DeliveryTaskFailedRepository,
        private val courierStatusSender: CourierStatusSender
) {

    fun delivery(message: DeliveryTaskMessage) {
        deliveryTaskFailedRepository.save(message)
    }

    fun process(task: DeliveryTaskMessage?) {
        courierStatusSender.sendDeliverySuccess(task)
    }
}

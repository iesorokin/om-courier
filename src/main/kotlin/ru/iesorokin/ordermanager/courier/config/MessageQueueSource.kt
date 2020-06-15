package ru.iesorokin.ordermanager.courier.config

import org.springframework.cloud.stream.annotation.Input
import org.springframework.cloud.stream.annotation.Output
import org.springframework.messaging.SubscribableChannel

internal const val START_DELIVERY = "startDelivery"

internal const val DELIVERY_SUCCESS = "deliverySuccess"

interface MessageQueueSource {
    // Input
    @Input(START_DELIVERY)
    fun startDelivery(): SubscribableChannel

    // Output
    @Output(DELIVERY_SUCCESS)
    fun deluverySuccess(): SubscribableChannel

}


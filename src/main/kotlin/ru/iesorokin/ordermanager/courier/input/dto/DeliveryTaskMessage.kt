package ru.iesorokin.ordermanager.courier.input.dto

import java.time.LocalDateTime

data class DeliveryTaskMessage(
        val correlationId: String,
        val creationDate: LocalDateTime? = null,
        val context: Map<Any, Any>
)
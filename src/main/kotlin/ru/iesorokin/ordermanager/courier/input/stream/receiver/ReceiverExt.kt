package ru.iesorokin.ordermanager.courier.input.stream.receiver

import mu.KLogger

internal const val X_DEATH_HEADER = "x-death"
internal const val ROUTE_KEY_HEADER = "routeTo"

internal fun Map<Any, Any?>?.getCount() = this?.get("count") as Long?

internal fun Map<Any, Any?>?.isDeadLetterCountOverflown(maxRetry: Long): Boolean {
    val count = this.getCount()
    if (count != null && count >= maxRetry) {
        return true
    }
    return false
}

internal fun KLogger.deadLetterCountOverflownError(maxRetry: Long, inputQueueName: String, message: Any) {
    this.error {
        """Message consuming failed after $maxRetry attempts. For queue $inputQueueName and message: 
            |$message
            |""".trimMargin()
    }
}

internal fun KLogger.inputMessage(inputQueueName: String, message: Any) {
    this.info {
        """Received in queue: $inputQueueName message: 
            |$message
            |""".trimMargin()
    }
}
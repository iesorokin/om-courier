package ru.iesorokin.ordermanager.courier.output.stream.sender

import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel

inline fun <T> MessageChannel.sendOrThrow(message: Message<T>, exceptionProducer: (Exception?) -> Exception) {
    var exception: Exception? = null
    var sent = false

    try {
        sent = send(message)
    } catch (e: Exception) {
        exception = e
    }

    if (!sent) throw exceptionProducer(exception)
}

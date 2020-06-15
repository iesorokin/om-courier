package ru.iesorokin.ordermanager.courier.error

class DeliveryResponseException(override val message: String) : RuntimeException()

class SendMessageException(override val message: String) : RuntimeException()

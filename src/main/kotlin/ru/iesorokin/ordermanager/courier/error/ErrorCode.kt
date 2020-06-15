package ru.iesorokin.ordermanager.courier.error

enum class ErrorCode(val code: Int, val errorMessage: String) {
    UNEXPECTED(201, "unexpected.error"),
    INVALID_ATTRIBUTE(103, "invalid.parameter"),
    COURIER_NOT_AVAILABLE(400, "COURIER_NOT_AVAILABLE"),
    DELIVERY_ERROR(500, "DELIVERY_ERROR")
}

package ru.iesorokin.ordermanager.courier.output.client

import org.apache.http.client.utils.URIBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class DeliverySystemClient(
        private val restTemplateDelivery: RestTemplate,
        @Value("\${delivery.task.urlToImplement}")
        private val urlToImplement: String
) {

    fun delivery(
            extOrderId: String
    ) {
        //use urlToImplement to rest to bank system
    }

}
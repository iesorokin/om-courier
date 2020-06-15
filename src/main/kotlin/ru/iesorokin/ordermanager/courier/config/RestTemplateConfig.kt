package ru.iesorokin.ordermanager.courier.config

import org.apache.http.client.HttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RequestCallback
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.ResponseErrorHandler
import org.springframework.web.client.ResponseExtractor
import org.springframework.web.client.RestTemplate
import org.zalando.logbook.Logbook
import org.zalando.logbook.httpclient.LogbookHttpRequestInterceptor
import org.zalando.logbook.httpclient.LogbookHttpResponseInterceptor
import ru.iesorokin.ordermanager.courier.error.ErrorCode
import ru.iesorokin.ordermanager.courier.error.ErrorCode.DELIVERY_ERROR
import ru.iesorokin.ordermanager.courier.error.ErrorCode.COURIER_NOT_AVAILABLE
import ru.iesorokin.ordermanager.courier.output.handler.ClientErrorHandler
import ru.iesorokin.ordermanager.courier.output.handler.ClientResponseErrorException
import java.net.URI

@Configuration
class RestTemplateConfig(
        private val logbook: Logbook,
        @Value("\${internalSystem.default.maxConnPerRoute:25}")
        private val maxConnPerRoute: Int,
        @Value("\${internalSystem.default.maxConnTotal:50}")
        private val maxConnTotal: Int
) {

    @Bean
    @LoadBalanced
    fun restTemplateDelivery(
            restTemplateBuilder: RestTemplateBuilder,
            @Value("\${bank.readTimeout:\${internalSystem.default.readTimeout}}")
            readTimeout: Int,
            @Value("\${bank.connectTimeout:\${internalSystem.default.connectTimeout}}")
            connectTimeout: Int
    ): RestTemplate =
            restTemplateBuilder
                    .requestFactory { createRequestFactory(readTimeout, connectTimeout) }
                    .errorHandler(responseErrorHandlerBank())
                    .build(CommonRestTemplate::class.java).apply {
                        this.errorCode = ErrorCode.COURIER_NOT_AVAILABLE
                    }

    @Bean
    fun responseErrorHandlerBank(): ResponseErrorHandler =
            ClientErrorHandler(
                    DELIVERY_ERROR, COURIER_NOT_AVAILABLE, COURIER_NOT_AVAILABLE
            )

    internal fun createRequestFactory(readTimeout: Int, connectTimeout: Int): ClientHttpRequestFactory =
            HttpComponentsClientHttpRequestFactory(createHttpClient()).also {
                it.setConnectTimeout(connectTimeout)
                it.setReadTimeout(readTimeout)
            }

    private fun createHttpClient(): HttpClient =
            HttpClientBuilder.create()
                    .addInterceptorFirst(LogbookHttpRequestInterceptor(logbook))
                    .addInterceptorLast(LogbookHttpResponseInterceptor())
                    .setMaxConnPerRoute(maxConnPerRoute)
                    .setMaxConnTotal(maxConnTotal)
                    .build()
}

class CommonRestTemplate : RestTemplate() {
    var errorCode: ErrorCode? = null

    override fun <T> doExecute(url: URI, method: HttpMethod?, requestCallback: RequestCallback?, responseExtractor: ResponseExtractor<T>?): T? {
        try {
            return super.doExecute(url, method, requestCallback, responseExtractor)
        } catch (e: ResourceAccessException) {
            throw ClientResponseErrorException(errorCode = errorCode!!)
        }

    }
}
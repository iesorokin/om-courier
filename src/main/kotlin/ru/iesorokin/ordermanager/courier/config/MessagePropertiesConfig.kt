package ru.iesorokin.ordermanager.courier.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource

private const val ERROR_MESSAGES_DIRECTORY = "messages/messages"
private const val DEFAULT_FILE_ENCODING = "UTF-8"
private const val IS_USING_CODE_MESSAGE_AS_DEFAULT = true

@Configuration
class MessagePropertiesConfig {
    @Bean
    fun messageSource(): ResourceBundleMessageSource =
            ResourceBundleMessageSource().also {
                it.setBasename(ERROR_MESSAGES_DIRECTORY)
                it.setUseCodeAsDefaultMessage(IS_USING_CODE_MESSAGE_AS_DEFAULT)
                it.setDefaultEncoding(DEFAULT_FILE_ENCODING)
            }
}
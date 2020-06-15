package ru.iesorokin.ordermanager.courier.config

import com.mongodb.MongoClient
import net.javacrumbs.shedlock.core.LockProvider
import net.javacrumbs.shedlock.provider.mongo.MongoLockProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SchedulerLockConfig {

    @Value("\${mongo.db-name}")
    private lateinit var databaseName: String

    @Bean
    fun lockProvider(mongo: MongoClient): LockProvider {
        return MongoLockProvider(mongo, databaseName)
    }

}

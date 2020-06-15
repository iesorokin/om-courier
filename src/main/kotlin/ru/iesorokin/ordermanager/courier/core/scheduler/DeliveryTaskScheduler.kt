package ru.iesorokin.ordermanager.courier.core.scheduler

import mu.KotlinLogging
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import ru.iesorokin.ordermanager.courier.core.repository.DeliveryTaskFailedRepository
import ru.iesorokin.ordermanager.courier.core.service.delivery.DeliveryService
import ru.iesorokin.ordermanager.courier.input.dto.DeliveryTaskMessage
import java.time.LocalDateTime

private val log = KotlinLogging.logger {}

@Service
@RefreshScope
class DeliveryTaskScheduler(
        private val deliveryTaskFailedRepository: DeliveryTaskFailedRepository,
        private val delivery: DeliveryService,
        @param:Value("\${schedule.courier.minutesForSelfProcessing}")
        private val minutesForSelfProcessing: Long
) {
    @SchedulerLock(
            name = "courier",
            lockAtLeastFor = "\${schedule.courier.lockAtLeast}",
            lockAtMostFor = "\${schedule.courier.lockAtMost}"
    )
    @Scheduled(cron = "\${schedule.courier.cron}", zone = "UTC")
    fun schedule() {
        val failedMessages = deliveryTaskFailedRepository.findAll()

        if (failedMessages.isNotEmpty()) {
            notify(failedMessages)

            failedMessages.forEach {
                delivery.process(it)
            }
        }
    }

    private fun notify(failedMessages: List<DeliveryTaskMessage>) {
        log.info { "Start scheduling for delivery tasks: $failedMessages" }

        val oldMessages = failedMessages.filter {
            it.creationDate!!.isBefore(LocalDateTime.now().minusMinutes(minutesForSelfProcessing))
        }
        if (oldMessages.isNotEmpty()) {
            log.error { "Some tasks can't be processed too long. Messages: $oldMessages" }
        }
    }
}
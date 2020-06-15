package ru.iesorokin.ordermanager.courier.core.repository

import org.springframework.data.mongodb.repository.MongoRepository
import ru.iesorokin.ordermanager.courier.input.dto.DeliveryTaskMessage

interface DeliveryTaskFailedRepository : MongoRepository<DeliveryTaskMessage, String>
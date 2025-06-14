package com.nv.myrecords.database.repository

import com.nv.myrecords.database.model.Record
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface RecordRepository : MongoRepository<Record, ObjectId> {

    fun findByOwnerId(ownerId: ObjectId): List<Record>

}
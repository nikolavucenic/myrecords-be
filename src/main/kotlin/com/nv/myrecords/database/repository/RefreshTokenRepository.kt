package com.nv.myrecords.database.repository

import com.nv.myrecords.database.model.RefreshToken
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface RefreshTokenRepository: MongoRepository<RefreshToken, ObjectId> {

    fun findByUserIdAndHashedToken(userId: ObjectId, hashedToken: String): RefreshToken?

    fun deleteByUserIdAndHashedToken(userId: ObjectId, hashedToken: String)

}
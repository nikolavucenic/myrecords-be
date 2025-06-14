package com.nv.myrecords.database.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("records")
data class Record(
    @Id val id: ObjectId = ObjectId.get(),
    val ownerId: ObjectId,
    val totalTracks: Int,
    val url: String,
    val images: List<String>,
    val name: String,
    val releaseDate: String,
    val artistsId: List<String>,
    val artists: List<String>,
    val recordState: Int,
    val recordRating: Int,
    val dateAdded: Instant,
    val boughtFor: Int,
    val soldFor: Int?,
    val dateSold: Instant?,
)

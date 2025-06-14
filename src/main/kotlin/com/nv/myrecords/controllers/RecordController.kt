package com.nv.myrecords.controllers

import com.nv.myrecords.database.model.Record
import com.nv.myrecords.database.repository.RecordRepository
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.bson.types.ObjectId
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/records")
class RecordController(
    private val repository: RecordRepository
) {

    data class RecordRequest(
        val id: String?,
        val totalTracks: Int,
        val url: String,
        val images: List<String>,
        @field:NotBlank(message = "Record name can't be empty.")
        val name: String,
        val releaseDate: String,
        val artistsId: List<String>,
        val artists: List<String>,
        val recordState: Int,
        val recordRating: Int,
        val boughtFor: Int,
        val soldFor: Int?,
        val dateSold: Instant?,
    )

    data class RecordResponse(
        val id: String,
        val totalTracks: Int,
        val url: String,
        val images: List<String>,
        val name: String,
        val releaseDate: String,
        val artistsId: List<String>,
        val artists: List<String>,
        val recordState: Int,
        val recordRating: Int,
        val dateAdded: Instant = Instant.now(),
        val boughtFor: Int,
        val soldFor: Int?,
        val dateSold: Instant?,
    )

    @PostMapping
    fun save(
        @Valid @RequestBody body: RecordRequest
    ): RecordResponse =
        repository.runCatching {
            save(
                Record(
                    totalTracks = body.totalTracks,
                    url = body.url,
                    images = body.images,
                    name = body.name,
                    releaseDate = body.releaseDate,
                    artistsId = body.artistsId,
                    artists = body.artists,
                    recordState = body.recordState,
                    recordRating = body.recordRating,
                    boughtFor = body.boughtFor,
                    soldFor = body.soldFor,
                    dateAdded = Instant.now(),
                    dateSold = body.dateSold,
                    id = body.id?.let { ObjectId(it) } ?: ObjectId.get(),
                    ownerId = ObjectId(SecurityContextHolder.getContext().authentication.principal as String),
                )
            )
        }.mapCatching { record ->
            record.toResponse()
        }.getOrThrow()

    @GetMapping
    fun findByOwnerId(): List<RecordResponse> =
        repository.findByOwnerId(
            ObjectId(SecurityContextHolder.getContext().authentication.principal as String)
        ).map { record ->
            record.toResponse()
        }

    @DeleteMapping(path = ["/{id"])
    fun deleteById(@PathVariable id: String) {
        val record = repository.findById(ObjectId(id)).orElseThrow {
            IllegalArgumentException("Record not found")
        }
        val ownerId = SecurityContextHolder.getContext().authentication.principal as String
        if (record.ownerId.toHexString() == ownerId) {
            repository.deleteById(ObjectId(id ))
        }
    }

    private fun Record.toResponse(): RecordResponse =
        RecordResponse(
            id = id.toHexString(),
            totalTracks = totalTracks,
            url = url,
            images = images,
            name = name,
            releaseDate = releaseDate,
            artistsId = artistsId,
            artists = artists,
            recordState = recordState,
            recordRating = recordRating,
            dateAdded = dateAdded,
            boughtFor = boughtFor,
            soldFor = soldFor,
            dateSold = dateSold,
        )

}
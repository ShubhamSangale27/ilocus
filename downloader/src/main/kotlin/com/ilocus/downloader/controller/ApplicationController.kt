package com.ilocus.downloader.controller


import com.ilocus.downloader.service.DriveService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.io.IOException
import java.nio.file.Files
import java.security.GeneralSecurityException

@RestController
class ApplicationController{

    val driveService : DriveService = DriveService()


    @get:Throws(IOException::class, GeneralSecurityException::class)
    @get:GetMapping("ilocus/getFiles")
    val files: MutableList<com.google.api.services.drive.model.File>?
        get() {
            return driveService.getAllFilesFromDrive()
        }

    @GetMapping("/ilocus/download/{fileId}")
    @Throws(GeneralSecurityException::class, IOException::class)
    fun downloadFile(@PathVariable fileId: String): ResponseEntity<*> {
        val file = driveService.downloadAndSaveFile(fileId)
        val connection = file.toURL().openConnection()
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf(connection.contentType)).body(Files.readAllBytes(file.toPath()))
    }
}
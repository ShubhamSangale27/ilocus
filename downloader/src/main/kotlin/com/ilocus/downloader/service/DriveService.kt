package com.ilocus.downloader.service

import com.google.api.client.http.HttpResponse
import com.google.api.services.drive.Drive
import com.ilocus.downloader.utils.DriverUtils
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class DriveService {

    val driverUtils : DriverUtils = DriverUtils()

    fun downloadAndSaveFile(fileId : String) : File {
        var fileName = "C:\\Users\\Shree\\Desktop\\Server_Downloads\\"
        for (file in driverUtils.getResult().getFiles()) {
            if (file.getId().equals(fileId,ignoreCase = true)) {
                val fname = file.name
                val ex = fname.substring(fname.lastIndexOf(".") + 1)

                try {
                    val f: Drive.Files = driverUtils.getService().files()
                    var httpResponse: HttpResponse? = null
                    if (ex.equals("xlsx", ignoreCase = true)) {
                        httpResponse = f
                                .export(file.getId(),
                                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                                .executeMedia()
                    } else if (ex.equals("docx", ignoreCase = true)) {
                        httpResponse = f
                                .export(file.getId(),
                                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                                .executeMedia()
                    } else if (ex.equals("pptx", ignoreCase = true)) {
                        httpResponse = f
                                .export(file.getId(),
                                        "application/vnd.openxmlformats-officedocument.presentationml.presentation")
                                .executeMedia()
                    } else if (ex.equals("pdf", ignoreCase = true)
                            || ex.equals("jpg", ignoreCase = true)
                            || ex.equals("png", ignoreCase = true)
                            || ex.equals("zip", ignoreCase = true)) {
                        val get: Drive.Files.Get = f.get(file.getId())
                        httpResponse = get.executeMedia()
                    }
                    if (null != httpResponse) {
                        val instream: InputStream = httpResponse.getContent()
                        fileName = fileName + file.name
                        val output = FileOutputStream(
                                "C:\\Users\\Shree\\Desktop\\Server_Downloads\\" + file.name)
                        try {
                            var l: Int
                            val tmp = ByteArray(2048)
                            while ((instream.read(tmp).also { l = it }) != -1) {
                                output.write(tmp, 0, l)
                            }
                        } finally {
                            output.close()
                            instream.close()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        val fileT = File(fileName)
        return fileT
    }

    fun getAllFilesFromDrive() : MutableList<com.google.api.services.drive.model.File>? {
        // Build a new authorized API client service.

        val files: MutableList<com.google.api.services.drive.model.File>? = driverUtils.getResult().getFiles()
        if (files == null || files.isEmpty()) {
            println("No files found.")
        } else {
            println("Files:")
            for (file in files) {
                System.out.printf("%s (%s)\n", file.name, file.getId())
            }
        }
        return files
    }

}
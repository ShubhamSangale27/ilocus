package com.ilocus.downloader.utils

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.FileList
import org.springframework.util.ResourceUtils
import java.io.*
import java.security.GeneralSecurityException

class DriverUtils {
    private val APPLICATION_NAME = "Google Drive Downloader"

    /**
     * Global instance of the JSON factory.
     */
    private val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance()

    /**
     * Directory to store authorization tokens for this application.
     */
    private val TOKENS_DIRECTORY_PATH = "tokens"

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private val SCOPES = listOf(DriveScopes.DRIVE)
    private val CREDENTIALS_FILE_PATH = "/credentials.json"


    @Throws(IOException::class)
    fun getCredentials(HTTP_TRANSPORT: NetHttpTransport?): Credential {
        // Load client secrets.
        val file = ResourceUtils.getFile("classpath:keys/gd_updated.json")
        val `in`: InputStream = FileInputStream(file)
        if (`in`.available() == 0) {
            throw FileNotFoundException("Resource not found: $CREDENTIALS_FILE_PATH")
        }
        val clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(`in`))

        // Build flow and trigger user authorization request.
        val flow = GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build()
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        val credential = AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
        //returns an authorized Credential object.
        return credential
    }

    @Throws(GeneralSecurityException::class, IOException::class)
    fun getResult(): FileList {
        val service = getService()

        // Print the names and IDs for up to 10 files.
        val result = service.files().list()
                .setPageSize(10)
                .setFields("nextPageToken, files(id, name, mimeType, thumbnailLink)")
                .execute()

        return result
    }

    @Throws(GeneralSecurityException::class, IOException::class)
    fun getService(): Drive {
        val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
        return Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build()
    }
}
package com.phoenix

import io.ktor.application.call
import io.ktor.http.ContentDisposition
import io.ktor.http.HttpHeaders
import io.ktor.locations.get
import io.ktor.response.header
import io.ktor.response.respondFile
import io.ktor.routing.Route
import java.io.File


fun Route.download() {

    /** 下载文件 **/
    get<DownLoad> {
        println("download action")
        call.response.header(
            /** 指定下载后的文件名 **/
            HttpHeaders.ContentDisposition, ContentDisposition.Attachment.withParameter(
                ContentDisposition.Parameters.FileName, "tv.dmg"
            ).toString()
        )
        /** 下载指定的文件 **/
        call.respondFile(File("/Users/phoenix/Downloads/TeamViewer.dmg"))
    }
}
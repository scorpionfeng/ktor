package com.phoenix

import io.ktor.application.call
import io.ktor.html.*
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.locations.url
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.Route
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import kotlinx.html.*
import java.io.File
import java.io.InputStream
import java.io.OutputStream


fun Route.upload( uploadDir: File){

    /** 上传页面 路由为:uploadpage **/
    get<Upload> {
        val data = listOf(1, 2, 3)

        call.respondHtml {

            body {
                form(
                    call.url(Upload()),
                    classes = "pure-form-stacked",
                    encType = FormEncType.multipartFormData,
                    method = FormMethod.post
                ) {
                    acceptCharset = "utf-8"

                    label {
                        htmlFor = "title"; +"Title:"
                        textInput { name = "title"; id = "title" }
                    }

                    br()
                    fileInput { name = "file" }
                    br()

                    submitInput(classes = "pure-button pure-button-primary") { value = "Upload" }
                }
            }

        }
    }

    /** 上传接口**/
    post<Upload> {
            val multipart = call.receiveMultipart()
            var title = ""
            var videoFile: File? = null

            multipart.forEachPart { part ->
                if (part is PartData.FormItem) {
                    if (part.name == "title") {
                        title = part.value
                    }
                } else if (part is PartData.FileItem) {
                    val ext = File(part.originalFileName).extension
                    val file = File(
                        uploadDir,
                        "upload-${System.currentTimeMillis()}-${title.hashCode()}.$ext"
                    )

                    part.streamProvider().use { its -> file.outputStream().buffered().use { its.copyToSuspend(it) } }
                    videoFile = file
                }

                part.dispose()
            }


            call.respond(mapOf("status" to true))


    }


}

suspend fun InputStream.copyToSuspend(
    out: OutputStream,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    yieldSize: Int = 4 * 1024 * 1024,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
): Long {
    return withContext(dispatcher) {
        val buffer = ByteArray(bufferSize)
        var bytesCopied = 0L
        var bytesAfterYield = 0L
        while (true) {
            val bytes = read(buffer).takeIf { it >= 0 } ?: break
            out.write(buffer, 0, bytes)
            if (bytesAfterYield >= yieldSize) {
                yield()
                bytesAfterYield %= yieldSize
            }
            bytesCopied += bytes
            bytesAfterYield += bytes
        }
        return@withContext bytesCopied
    }
}


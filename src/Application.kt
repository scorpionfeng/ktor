package com.phoenix

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import de.nielsfalk.ktor.swagger.*
import de.nielsfalk.ktor.swagger.version.shared.Contact
import de.nielsfalk.ktor.swagger.version.shared.Group
import de.nielsfalk.ktor.swagger.version.shared.Information
import de.nielsfalk.ktor.swagger.version.v2.Swagger
import de.nielsfalk.ktor.swagger.version.v3.OpenApi
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.jwt.jwt
import io.ktor.auth.principal
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.html.respondHtml
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.jackson.jackson
import io.ktor.locations.Location
import io.ktor.locations.Locations
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import kotlinx.html.*
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.set

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Location("/databasex")
class DataResult{
    @Location("/find")
    data class Find(val id:Int)

    @Location("/select")
    data class Select(val page:Int=1)

    @Location("/save")
    data class Save(val par:String="")
}


@Location("/account")
class Account{
    @Location("/login")
    data class login(val name:String)


    @Location("/register")
    data class register(val username: String, val password: String)
}
const val petUuid = "petUuid"
/** 上传页面 **/
@Location("/uploadpage")
class Upload

@Location("/download")
class DownLoad

const val JDBC_DRIVER = "com.mysql.jdbc.Driver"
const val DB_URL = "jdbc:mysql://localhost:3306/ktor?useUnicode=true&characterEncoding=UTF-8"
const val DB_USER = "root"
const val DB_PASSWORD = ""

val sizeSchemaMap = mapOf(
    "type" to "number",
    "minimum" to 0
)

val petIdSchema = mapOf(
    "type" to "string",
    "format" to "date",
    "description" to "The identifier of the pet to be accessed"
)
fun rectangleSchemaMap(refBase: String) = mapOf(
    "type" to "object",
    "properties" to mapOf(
        "a" to mapOf("${'$'}ref" to "$refBase/size"),
        "b" to mapOf("${'$'}ref" to "$refBase/size")
    )
)


@Group("pet operations")
@Location("/pets")
class Pets

@Group("pet operations")
@Location("/pets/{id}")
class pet(val id: Int)

@Group("cats")
@Location("/cats")
class Cats

@Group("/dogs")
@Location("/dogs")
class Dogs

data class PetsModel(val pets: MutableList<PetModel>) {
    companion object {
        val exampleModel = mapOf(
            "pets" to listOf(
                PetModel.exampleSpike,
                PetModel.exampleRover
            )
        )
    }
}

data class PetModel(val id: Int?, val name: String) {
    companion object {
        val exampleSpike = mapOf(
            "id" to 1,
            "name" to "Spikex"
        )

        val exampleRover = mapOf(
            "id" to 2,
            "name" to "Roverx"
        )
    }
}

val data = PetsModel(
    mutableListOf(
        PetModel(1, "max"),
        PetModel(2, "moritz")
    )
)

fun newId() = ((data.pets.map { it.id ?: 0 }.max()) ?: 0) + 1

fun Application.module() {




    val uploadDirPath: String = "/Users/phoenix/proj/upload"
    val uploadDir = File(uploadDirPath)
    if (!uploadDir.mkdirs() && !uploadDir.exists()) {
        throw IOException("Failed to create directory ${uploadDir.absolutePath}")
    }

    val simpleJwt = SimpleJWT("my-super-secret-for-jwt")

    install(Locations)

    install(SwaggerSupport){
        forwardRoot = true
        val information = Information(
            version = "0.1",
            title = "sample api implemented in ktor",
            description = "This is a sample ",
            contact = Contact(
                name = "Phoenix",
                url = "https://www.xxx.com"
            )
        )
        swagger = Swagger().apply {
            info = information
            definitions["size"] = sizeSchemaMap
            definitions[petUuid] = petIdSchema
            definitions["Rectangle"] = rectangleSchemaMap("#/definitions")
        }
        openApi = OpenApi().apply {
            info = information
            components.schemas["size"] = sizeSchemaMap
            components.schemas[petUuid] = petIdSchema
            components.schemas["Rectangle"] = rectangleSchemaMap("#/components/schemas")
        }
    }

    install(CORS) {


        method(HttpMethod.Options)
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        allowCredentials = true
        anyHost()
    }
    install(StatusPages) {
        exception<InvalidCredentialsException> { exception ->
            call.respond(HttpStatusCode.Unauthorized, mapOf("OK" to false, "error" to (exception.message ?: "")))
        }
        exception<MissingKotlinParameterException>{ exception->
            call.respond(HttpStatusCode.BadRequest, mapOf("status" to false,"error" to (exception.message?:"")))
        }
    }
    install(Authentication) {
        jwt {
            verifier(simpleJwt.verifier)
            validate {
                UserIdPrincipal(it.payload.getClaim("name").asString())
            }
        }
    }
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT) // Pretty Prints the JSON
        }
    }
    routing {

        account()
        upload(uploadDir)
        download()
        databasex()

        get<Pets>("all".responds(ok<PetsModel>(example("model", PetsModel.exampleModel)))) {
            call.respond(data)
        }

        get<Cats>("allc".responds()){
            call.respond(mapOf("succ" to true))

        }

        get<Dogs>("dogs".responds()){
            call.respond(mapOf("succ" to true))

        }

        post<Pets, PetModel>(
            "create"
                .description("Save a pet in our wonderful database!")
                .examples(
                    example("rover", PetModel.exampleRover, summary = "Rover is one possible pet."),
                    example("spike", PetModel.exampleSpike, summary = "Spike is a different posssible pet.")
                )
                .responds(
                    created<PetModel>(
                        example("rover", PetModel.exampleRover),
                        example("spike", PetModel.exampleSpike)
                    ),
                    ok<PetModel>(
                        example("rover", PetModel.exampleRover),
                        example("spike", PetModel.exampleSpike)
                    )
                )
        ) { _, entity ->
            call.respond(Created, entity.copy(id = newId()).apply {
                data.pets.add(this)
            })
        }

        get<pet>(
            "find".responds(
                ok<PetModel>(),
                notFound()
            )
        ) { params ->
            data.pets.find { it.id == params.id }
                ?.let {
                    call.respond(it)
                }
        }

        put<pet, PetModel>(
            "update"
                .responds(
                ok<PetModel>( example("rover", PetModel.exampleRover)),
                notFound()
            )
        ) { params, entity ->
                call.respond(entity)
        }


        post("/login-register") {
            val post = call.receive<LoginRegister>()
            val user = users.getOrPut(post.user) { User(post.user, post.password) }
            if (user.password != post.password) throw InvalidCredentialsException("Invalid credentials")
            call.respond(mapOf("token" to simpleJwt.sign(user.name)))
        }
        route("/snippets") {
            get {
                call.respond(mapOf("snippets" to synchronized(snippets) { snippets.toList() }))
            }


            authenticate {
                post {
                    val post = call.receive<PostSnippet>()
                    val principal = call.principal<UserIdPrincipal>() ?: error("No principal")
                    snippets += Snippet(principal.name, post.snippet.text)
                    call.respond(mapOf("OK" to true))
                }
            }
        }

        get("/index") {
            call.respondHtml {

                head {
                    title {
                        +"Ktor 入门"
                    }
                }
                body {

                    form {  }
                    a {
                        href = "http://127.0.0.1:8080/register"
                        +"注册"
                    }

                    form {
                        action = "http://127.0.0.1：8080/user/login"
                        method=FormMethod.post
                        input {
                            type = InputType.text
                            value = ""
                            name="username"
                        }
                        input {
                            type = InputType.password
                            value = ""
                            name="password"
                        }
                        input {
                            type = InputType.submit
                            value = "登录"
                        }
                    }
                }
            }
        }
    }
}



data class PostSnippet(val snippet: PostSnippet.Text) {
    data class Text(val text: String)
}

data class Snippet(val user: String, val text: String)

val snippets = Collections.synchronizedList(mutableListOf(
    Snippet(user = "test", text = "hello"),
    Snippet(user = "test", text = "world")
))

open class SimpleJWT(val secret: String) {
    private val algorithm = Algorithm.HMAC256(secret)
    val verifier = JWT.require(algorithm).build()
    fun sign(name: String): String = JWT.create().withClaim("name", name).sign(algorithm)
}

class User(val name: String, val password: String)

val users = Collections.synchronizedMap(
    listOf(User("test", "test"))
        .associateBy { it.name }
        .toMutableMap()
)

class InvalidCredentialsException(message: String) : RuntimeException(message)

class LoginRegister(val user: String, val password: String)
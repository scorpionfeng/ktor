package com.phoenix

import io.ktor.application.call
import io.ktor.locations.get

import io.ktor.locations.post
import io.ktor.response.respondText
import io.ktor.routing.Route

fun Route.account() {
    get<Account.login> { user->
            call.respondText("登录成功${user.name}")


    }

    post<Account.register> { user ->
        run {
            call.respondText("用户${user.username}${user.password}")
        }
    }
}
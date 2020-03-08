package com.phoenix

import io.ktor.application.call
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.response.respondText
import io.ktor.routing.Route
import me.liuwj.ktorm.database.Database
import me.liuwj.ktorm.dsl.from
import me.liuwj.ktorm.dsl.select


fun Route.account() {
    get<Account.login> { user->
        val database = Database.connect("jdbc:mysql://localhost:3306/ktor?user=root&password=123456")

        var nama=""
        for (row in database.from(Employees).select()) {
            println(row[Employees.name])
            nama+=row[Employees.name]
        }
            call.respondText("登录成功${nama}")


    }

    post<Account.register> { user ->
        run {
            call.respondText("用户${user.username}${user.password}")
        }
    }
}
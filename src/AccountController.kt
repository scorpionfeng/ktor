package com.phoenix

import io.ktor.application.call
import io.ktor.locations.get

import io.ktor.locations.post
import io.ktor.response.respondText
import io.ktor.routing.Route
import me.liuwj.ktorm.database.Database
import me.liuwj.ktorm.dsl.from
import me.liuwj.ktorm.dsl.select
import me.liuwj.ktorm.schema.*


object Departments : Table<Nothing>("t_department") {
    val id by int("id").primaryKey()
    val name by varchar("name")
    val location by varchar("location")
}

object Employees : Table<Nothing>("t_employee") {
    val id by int("id").primaryKey()
    val name by varchar("name")
    val job by varchar("job")
    val managerId by int("manager_id")
    val hireDate by date("hire_date")
    val salary by long("salary")
    val departmentId by int("department_id")
}

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
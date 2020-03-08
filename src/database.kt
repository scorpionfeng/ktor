package com.phoenix

import io.ktor.application.call
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Route
import me.liuwj.ktorm.database.Database
import me.liuwj.ktorm.dsl.eq
import me.liuwj.ktorm.dsl.from
import me.liuwj.ktorm.dsl.limit
import me.liuwj.ktorm.dsl.select
import me.liuwj.ktorm.entity.Entity
import me.liuwj.ktorm.entity.add
import me.liuwj.ktorm.entity.find
import me.liuwj.ktorm.entity.sequenceOf
import me.liuwj.ktorm.schema.*
import java.time.LocalDate

interface Department : Entity<Department> {
    companion object : Entity.Factory<Department>()
    val id: Int
    var name: String
    var location: String
}

interface Employee : Entity<Employee> {
    companion object : Entity.Factory<Employee>()
    val id: Int?
    var name: String
    var job: String
    var manager: Employee?
    var hireDate: LocalDate
    var salary: Long
    var department: Department
}

object Departments : Table<Department>("t_department") {
    val id by int("id").primaryKey().bindTo { it.id }
    val name by varchar("name").bindTo { it.name }
    val location by varchar("location").bindTo { it.location }
}

object Employees : Table<Employee>("t_employee") {
    val id by int("id").primaryKey().bindTo { it.id }
    val name by varchar("name").bindTo { it.name }
    val job by varchar("job").bindTo { it.job }
    val managerId by int("manager_id").bindTo { it.manager?.id }
    val hireDate by date("hire_date").bindTo { it.hireDate }
    val salary by long("salary").bindTo { it.salary }
    val departmentId by int("department_id").references(Departments) { it.department }
}

val database by lazy {
    Database.connect("jdbc:mysql://localhost:3306/ktor?user=root&password=123456")
}

fun Route.databasex(){

    //databasex/find?id=1
    get<DataResult.Find>{
        val sequence = database.sequenceOf(Employees)
        val employee = sequence.find { it.id eq 1}
        call.respond(mapOf("name" to employee?.name))
    }

    //databasex/select?page=1
    get<DataResult.Select>{
        form->
            val seq = database.from(Employees)
            val employee = seq.select().limit((form.page-1)*10,10).toList()
            call.respond(mapOf("size" to employee.size))
    }

    ///databasex/save?par=1
    get<DataResult.Save>{


        val seq= database.sequenceOf(Employees)

        var add = database.sequenceOf(Departments).add(Department{
            name = "techx"
            location = "test"
        })


        val employee = Employee {
            name = "jerry"
            job = "trainee"
            hireDate = LocalDate.now()
            salary = 50
            department = database.sequenceOf(Departments).find { it.name eq "techx" }!!
        }

        var addStatus = seq.add(employee)

        call.respond(mapOf("name" to addStatus,"dep" to (add>0)))

    }

}
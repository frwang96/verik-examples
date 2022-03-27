/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress(
    "ConvertSecondaryConstructorToPrimary",
    "FunctionName",
    "unused",
    "ClassName",
    "JoinDeclarationAndAssignment"
)

import io.verik.core.*

abstract class animal : Class {

    var age = -1

    constructor(a: Int) {
        age = a
    }

    fun set_age(a: Int) {
        age = a
    }

    fun get_age(): Int {
        if (age == -1) {
            fatal("You didn't set the age")
        } else {
            return age
        }
    }

    abstract fun make_sound()
}

class lion : animal {

    val name: String

    constructor(age: Int, n: String) : super(age) {
        name = n
    }

    override fun make_sound() {
        println("$name says ROAR")
    }

    fun get_name(): String {
        return name
    }
}

object lion_cage : Class() {

    val cage: Queue<lion> = nc()

    fun cage_lion(lion: lion) {
        cage.add(lion)
    }

    fun list_lions() {
        println("Lions in cage")
        cage.forEach { println(it.get_name()) }
    }
}

@Entry
object top : Module() {

    @Run
    fun run() {
        var lion_h: lion
        lion_h = lion(2, "Kimba")
        lion_cage.cage_lion(lion_h)
        lion_h = lion(3, "Simba")
        lion_cage.cage_lion(lion_h)
        lion_h = lion(15, "Mustafa")
        lion_cage.cage_lion(lion_h)
        lion_cage.list_lions()
    }
}

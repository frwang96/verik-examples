/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "FunctionName", "JoinDeclarationAndAssignment")

import io.verik.core.*

abstract class animal : Class {

    val age: Int

    constructor(a: Int) : super() {
        age = a
    }

    open fun make_sound() {
        fatal("Generic animals don't have a sound")
    }
}

class lion : animal {

    constructor(age: Int) : super(age)

    override fun make_sound() {
        println("The lion says ROAR")
    }
}

class chicken : animal {

    constructor(age: Int) : super(age)

    override fun make_sound() {
        println("The chicken says BECAWW")
    }
}

@Entry
object top : Module() {

    @Run
    fun run() {
        var lion_h: lion
        var chicken_h: chicken
        var animal_h: animal

        lion_h = lion(15)
        lion_h.make_sound()
        println("The lion is ${lion_h.age} years old")

        chicken_h = chicken(1)
        chicken_h.make_sound()
        println("The chicken is ${chicken_h.age} years old")

        animal_h = lion_h
        animal_h.make_sound()
        println("The animal is ${animal_h.age} years old")

        animal_h = chicken_h
        animal_h.make_sound()
        println("The animal is ${animal_h.age} years old")
    }
}

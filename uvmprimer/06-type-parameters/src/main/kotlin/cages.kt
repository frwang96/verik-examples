/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "FunctionName", "unused")

import io.verik.core.*

abstract class animal : Class {

    var age = -1
    val name: String

    constructor(a: Int, n: String) : super() {
        age = a
        name = n
    }

    fun get_age(): Int {
        return age
    }

    fun get_name(): String {
        return name
    }

    abstract fun make_sound()
}

class lion : animal {

    constructor(age: Int, n: String) : super(age, n)

    override fun make_sound() {
        println("The lion ${get_name()} says ROAR")
    }
}

class chicken : animal {

    constructor(age: Int, n: String) : super(age, n)

    override fun make_sound() {
        println("The chicken ${get_name()} says BECAWW")
    }
}

class animal_cage<T : animal> : Class() {

    private val cage: Queue<T> = nc()

    fun cage_animal(l: T) {
        cage.add(l)
    }

    fun list_animals() {
        println("Animals in cage")
        cage.forEach { println(it.get_name()) }
    }
}

@Entry
object top : Module() {

    lateinit var lion_h: lion
    lateinit var chicken_h: chicken
    lateinit var  lion_cage: animal_cage<lion>
    lateinit var  chicken_cage: animal_cage<chicken>

    @Run
    fun run() {
        lion_cage = animal_cage()
        lion_h = lion(15, "Mustafa")
        lion_cage.cage_animal(lion_h)
        lion_h = lion(15, "Simba")
        lion_cage.cage_animal(lion_h)

        chicken_cage = animal_cage()
        chicken_h = chicken(1, "Little Red Hen")
        chicken_cage.cage_animal(chicken_h)
        chicken_h = chicken(1, "Lady Clucksalot")
        chicken_cage.cage_animal(chicken_h)

        println("-- Lions --")
        lion_cage.list_animals()
        println("-- Chickens --")
        chicken_cage.list_animals()
    }
}

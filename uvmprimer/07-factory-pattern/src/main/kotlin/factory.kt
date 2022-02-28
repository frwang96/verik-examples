/*
 * Copyright (c) 2021 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Verik

import io.verik.core.*

abstract class animal : Class {

    val age: Int
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

    var thorn_in_paw = false

    constructor(age: Int, n: String) : super(age, n)

    override fun make_sound() {
        println("The lion, ${get_name()}, says ROAR")
    }
}

class chicken : animal {

    constructor(age: Int, n: String) : super(age, n)

    override fun make_sound() {
        println("The chicken, ${get_name()}, says BECAWW")
    }
}

object animal_factory : Class() {

    fun make_animal(species: String, age: Int, name: String): animal {
        when (species) {
            "lion" -> return lion(age, name)
            "chicken" -> return chicken(age, name)
            else -> fatal("No such animal: $species")
        }
    }
}

class animal_cage<T : animal> : Class() {

    private val cage: Queue<T> = nc()

    fun cage_animal(l: T) {
        cage.add(l)
    }

    fun list_animals() {
        println("Animals in cage:")
        cage.forEach { println(it.get_name()) }
    }
}

@Entry
object top : Module() {

    @Run
    fun run() {
        val lion_cage = animal_cage<lion>()
        val chicken_cage = animal_cage<chicken>()
        var animal_h: animal
        var lion_h: lion
        var chicken_h: chicken

        animal_h = animal_factory.make_animal("lion", 15, "Mustafa")
        animal_h.make_sound()

        lion_h = animal_h as lion
        if (lion_h.thorn_in_paw) println("He looks angry!")
        lion_cage.cage_animal(lion_h)

        lion_h = animal_factory.make_animal("lion", 2, "Simba") as lion
        lion_cage.cage_animal(lion_h)

        chicken_h = animal_factory.make_animal("chicken", 1, "Clucker") as chicken
        chicken_cage.cage_animal(chicken_h)

        chicken_h = animal_factory.make_animal("chicken", 1, "Boomer") as chicken
        chicken_cage.cage_animal(chicken_h)

        println("--- Lions ---")
        lion_cage.list_animals()
        println("--- Chickens ---")
        chicken_cage.list_animals()
    }
}

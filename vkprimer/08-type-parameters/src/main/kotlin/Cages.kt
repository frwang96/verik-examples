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

abstract class Animal(
    @Suppress("unused") val age: Int,
    val name: String
) : Class() {

    abstract fun makeSound()
}

class Lion(age: Int, name: String) : Animal(age, name) {

    override fun makeSound() {
        println("The lion $name says ROAR")
    }
}

class Chicken(age: Int, name: String): Animal(age, name) {

    override fun makeSound() {
        println("The chicken $name says BECAWW")
    }
}

class AnimalCage<A : Animal> : Class() {

    private val cage = ArrayList<A>()

    fun cageAnimal(animal: A) {
        cage.add(animal)
    }

    fun listAnimals() {
        cage.forEach { println(it.name) }
    }
}

@Entry
object Top : Module() {

    @Run
    fun run() {
        val lion_cage = AnimalCage<Lion>()
        lion_cage.cageAnimal(Lion(15, "Mustafa"))
        lion_cage.cageAnimal(Lion(15, "Simba"))

        val chicken_cage = AnimalCage<Chicken>()
        chicken_cage.cageAnimal(Chicken(1, "Little Red Hen"))
        chicken_cage.cageAnimal(Chicken(1, "Lady Clucksalot"))

        println("-- Lions --")
        lion_cage.listAnimals()
        println("-- Chickens --")
        chicken_cage.listAnimals()
    }
}

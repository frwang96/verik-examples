/*
 * SPDX-License-Identifier: Apache-2.0
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

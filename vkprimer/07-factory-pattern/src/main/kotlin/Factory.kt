/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

abstract class Animal(val age: Int, val name: String) : Class() {

    abstract fun makeSound()
}

class Lion(age: Int, name: String) : Animal(age, name) {

    var thorn_in_paw = false

    override fun makeSound() {
        println("The Lion, $name, $age, says ROAR")
    }
}

class Chicken(age: Int, name: String) : Animal(age, name) {

    override fun makeSound() {
        println("The Chicken, $name, $age, says BECAWW")
    }
}

object AnimalFactory : Class() {

    fun makeAnimal(species: String, age: Int, name: String): Animal {
        return when (species) {
            "lion" -> Lion(age, name)
            "chicken" -> Chicken(age, name)
            else -> fatal("No such animal: $species")
        }
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
        val chicken_cage = AnimalCage<Chicken>()

        val lion_mustafa = AnimalFactory.makeAnimal("lion", 15, "Mustafa")
        lion_mustafa.makeSound()
        if (lion_mustafa is Lion) {
            if (lion_mustafa.thorn_in_paw) println("He looks angry!")
            lion_cage.cageAnimal(lion_mustafa)
        }

        lion_cage.cageAnimal(AnimalFactory.makeAnimal("lion", 2, "Simba") as Lion)
        chicken_cage.cageAnimal(AnimalFactory.makeAnimal("chicken", 1, "Clucker") as Chicken)
        chicken_cage.cageAnimal(AnimalFactory.makeAnimal("chicken", 1, "Boomer") as Chicken)

        println("--- Lions ---")
        lion_cage.listAnimals()
        println("--- Chickens ---")
        chicken_cage.listAnimals()
    }
}

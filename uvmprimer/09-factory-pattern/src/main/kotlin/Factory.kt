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

import io.verik.core.*

@SimTop
class FactoryTop : Module() {

    @Run
    fun run() {
        val lionCage = AnimalCage<Lion>()
        val chickenCage = AnimalCage<Chicken>()

        val lionMustafa = AnimalFactory.makeAnimal("lion", 15, "Mustafa")
        lionMustafa.makeSound()
        if (lionMustafa is Lion)
            lionCage.cageAnimal(lionMustafa)

        val lionSimba = AnimalFactory.makeAnimal("lion", 2, "Simba")
        if (lionSimba is Lion)
            lionCage.cageAnimal(lionSimba)

        val chickenClucker = AnimalFactory.makeAnimal("chicken", 1, "Clucker")
        chickenCage.cageAnimal(chickenClucker as Chicken)

        val chickenBoomer = AnimalFactory.makeAnimal("chicken", 1, "Boomer")
        chickenCage.cageAnimal(chickenBoomer as Chicken)

        println("--- Lions ---")
        lionCage.listAnimals()
        println("--- Chickens ---")
        chickenCage.listAnimals()
    }
}

abstract class Animal(val age: Int, val name: String) {

    abstract fun makeSound()
}

class Lion(age: Int, name: String) : Animal(age, name) {

    override fun makeSound() {
        println("The Lion, $name, $age, says ROAR")
    }
}

class Chicken(age: Int, name: String) : Animal(age, name) {

    override fun makeSound() {
        println("The Chicken, $name, $age, says BECAWW")
    }
}

class AnimalCage<A : Animal> {

    private val cage = ArrayList<A>()

    fun cageAnimal(animal: A) {
        cage.add(animal)
    }

    fun listAnimals() {
        cage.forEach { println(it.name) }
    }
}

object AnimalFactory {

    fun makeAnimal(species: String, age: Int, name: String): Animal {
        return when (species) {
            "lion" -> Lion(age, name)
            "chicken" -> Chicken(age, name)
            else -> fatal("No such animal: $species")
        }
    }
}
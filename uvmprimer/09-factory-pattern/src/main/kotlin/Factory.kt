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

@SimTop
object FactoryTop : Module() {

    @Run
    fun run() {
        val lion_cage = AnimalCage<Lion>()
        val chicken_cage = AnimalCage<Chicken>()

        val lion_mustafa = AnimalFactory.makeAnimal("lion", 15, "Mustafa")
        lion_mustafa.makeSound()
        if (lion_mustafa is Lion)
            lion_cage.cageAnimal(lion_mustafa)

        val lion_simba = AnimalFactory.makeAnimal("lion", 2, "Simba")
        if (lion_simba is Lion)
            lion_cage.cageAnimal(lion_simba)

        val chicken_clucker = AnimalFactory.makeAnimal("chicken", 1, "Clucker")
        chicken_cage.cageAnimal(chicken_clucker as Chicken)

        val chicken_boomer = AnimalFactory.makeAnimal("chicken", 1, "Boomer")
        chicken_cage.cageAnimal(chicken_boomer as Chicken)

        println("--- Lions ---")
        lion_cage.listAnimals()
        println("--- Chickens ---")
        chicken_cage.listAnimals()
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
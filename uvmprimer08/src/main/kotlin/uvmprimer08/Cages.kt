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

package uvmprimer08

import io.verik.core.*

@SimTop
class CagesTop : Module() {

    @Run
    fun run() {
        val lionCage = AnimalCage<Lion>()
        lionCage.cageAnimal(Lion("Mustafa"))
        lionCage.cageAnimal(Lion("Simba"))

        val chickenCage = AnimalCage<Chicken>()
        chickenCage.cageAnimal(Chicken("Little Red Hen"))
        chickenCage.cageAnimal(Chicken("Lady Clucksalot"))

        println("-- Lions --")
        lionCage.listAnimals()
        println("-- Chickens --")
        chickenCage.listAnimals()
    }
}

abstract class Animal(val name: String)

class Lion(name: String) : Animal(name)

class Chicken(name: String): Animal(name)

class AnimalCage<A : Animal> {

    private val cage = ArrayList<A>()

    fun cageAnimal(animal: A) {
        cage.add(animal)
    }

    fun listAnimals() {
        cage.forEach { println(it.name) }
    }
}

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
    @Suppress("unused") val age: Int
) : Class() {

    abstract fun makeSound()
}

class Lion(age: Int, val name: String) : Animal(age) {

    override fun makeSound() {
        println("$name says ROAR")
    }
}

object LionCage : Class() {

    val cage = ArrayList<Lion>()

    fun cageLion(lion: Lion) {
        cage.add(lion)
    }

    fun listLions() {
        cage.forEach { println(it.name) }
    }
}

@Entry
object Top : Module() {

    @Run
    fun run() {
        LionCage.cageLion(Lion(2, "Kimba"))
        LionCage.cageLion(Lion(3, "Simba"))
        LionCage.cageLion(Lion(15, "Mustafa"))
        LionCage.listLions()
    }
}

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

abstract class animal(val age: Int) : Class() {

    open fun make_sound() {
        fatal("Generic animals don't have a sound")
    }
}

class lion(age: Int) : animal(age) {

    override fun make_sound() {
        println("The lion says ROAR")
    }
}

class chicken(age: Int) : animal(age) {

    override fun make_sound() {
        println("The chicken says BECAWW")
    }
}

@Entry
object top : Module() {

    @Run
    fun run() {
        val lion_h = lion(15)
        lion_h.make_sound()
        println("The lion is ${lion_h.age} years old")

        val chicken_h = chicken(1)
        chicken_h.make_sound()
        println("The chicken is ${chicken_h.age} years old")

        var animal_h: animal = lion_h
        animal_h.make_sound()
        println("The animal is ${animal_h.age} years old")

        animal_h = chicken_h
        animal_h.make_sound()
        println("The animal is ${animal_h.age} years old")
    }
}

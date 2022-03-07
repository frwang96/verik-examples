/*
 * Copyright (c) 2022 Francis Wang
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

abstract class Animal(var age: Int) : Class() {

    override fun toString(): String {
        return "age=$age"
    }

    open fun copyFrom(animal: Animal) {
        age = animal.age
    }
}

open class Mammal(
    age: Int,
    var baby_count: Int
) : Animal(age) {

    override fun toString(): String {
        return "${super.toString()} baby_count=$baby_count"
    }

    override fun copyFrom(animal: Animal) {
        super.copyFrom(animal)
        if (animal is Mammal) baby_count = animal.baby_count
    }
}

open class Lion(
    age: Int,
    baby_count: Int,
    var isFemale: Boolean
) : Mammal(age, baby_count) {

    override fun toString(): String {
        val gender = if (isFemale) "female" else "male"
        return "${super.toString()} gender=$gender"
    }

    override fun copyFrom(animal: Animal) {
        super.copyFrom(animal)
        if (animal is Lion) isFemale = animal.isFemale
    }
}

open class CaptiveLion(
    age: Int,
    baby_count: Int,
    isFemale: Boolean,
    var name: String
) : Lion(age, baby_count, isFemale) {

    override fun toString(): String {
        return "${super.toString()} name=$name"
    }

    override fun copyFrom(animal: Animal) {
        super.copyFrom(animal)
        if (animal is CaptiveLion) name = animal.name
    }
}

class CircusLion(
    age: Int,
    baby_count: Int,
    isFemale: Boolean,
    name: String,
    var num_tricks: Int
) : CaptiveLion(age, baby_count, isFemale, name) {

    override fun toString(): String {
        return "${super.toString()} num_tricks=$num_tricks"
    }

    override fun copyFrom(animal: Animal) {
        super.copyFrom(animal)
        if (animal is CircusLion) num_tricks = animal.num_tricks
    }
}

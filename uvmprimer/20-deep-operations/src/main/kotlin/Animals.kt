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

    open fun copyTo(animal: Animal) {
        animal.age = age
    }
}

open class Lion(
    age: Int,
    var isFemale: Boolean
) : Animal(age) {

    override fun toString(): String {
        val gender = if (isFemale) "female" else "male"
        return "${super.toString()} gender=$gender"
    }

    override fun copyTo(animal: Animal) {
        super.copyTo(animal)
        if (animal is Lion) animal.isFemale = isFemale
    }
}

open class CaptiveLion(
    age: Int,
    isFemale: Boolean,
    var name: String
) : Lion(age, isFemale) {

    override fun toString(): String {
        return "${super.toString()} name=$name"
    }

    override fun copyTo(animal: Animal) {
        super.copyTo(animal)
        if (animal is CaptiveLion) animal.name = name
    }
}

class CircusLion(
    age: Int,
    isFemale: Boolean,
    name: String,
    var numTricks: Int
) : CaptiveLion(age, isFemale, name) {

    override fun toString(): String {
        return "${super.toString()} numTricks=$numTricks"
    }

    override fun copyTo(animal: Animal) {
        super.copyTo(animal)
        if (animal is CircusLion) animal.numTricks = numTricks
    }
}

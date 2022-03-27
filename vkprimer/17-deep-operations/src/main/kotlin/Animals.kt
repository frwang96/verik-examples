/*
 * SPDX-License-Identifier: Apache-2.0
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

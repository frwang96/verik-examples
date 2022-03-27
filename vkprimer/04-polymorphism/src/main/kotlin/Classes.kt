/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

abstract class Animal(val age: Int) : Class() {

    abstract fun makeSound()
}

class Lion(age: Int) : Animal(age) {

    override fun makeSound() {
        println("The lion says ROAR")
    }
}

class Chicken(age: Int) : Animal(age) {

    override fun makeSound() {
        println("The chicken says BECAWW")
    }
}

@Entry
object Top : Module() {

    @Run
    fun run() {
        val lion = Lion(15)
        lion.makeSound()
        println("The lion is ${lion.age} years old")
        val chicken = Chicken(1)
        chicken.makeSound()
        println("The chicken is ${chicken.age} years old")

        var animal: Animal = lion
        animal.makeSound()
        println("The animal is ${animal.age} years old")
        animal = chicken
        animal.makeSound()
        println("The animal is ${animal.age} years old")
    }
}

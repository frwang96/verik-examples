/*
 * SPDX-License-Identifier: Apache-2.0
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

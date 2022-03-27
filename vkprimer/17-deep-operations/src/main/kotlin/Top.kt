/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

@Entry
class Top : Module() {

    @Run
    fun run() {
        val circus_lion1 = CircusLion(
            age = 2,
            baby_count = 2,
            isFemale = true,
            name = "Agnes",
            num_tricks = 2
        )
        val circus_lion2 = CircusLion(
            age = 3,
            baby_count = 0,
            isFemale = false,
            name = "Simba",
            num_tricks = 0
        )
        println("Lion 1: $circus_lion1")
        println("Lion 2 before copy: $circus_lion2")
        circus_lion2.copyFrom(circus_lion1)
        println("Lion 2 after copy: $circus_lion2")
    }
}

/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

@Entry
object PopulationCountTest : Module() {

    fun populationCountReference(a: Ubit<`4`>): Ubit<`3`> {
        return u(0b000) + a[0].toUbit<`1`>() + a[1].toUbit<`1`>() + a[2].toUbit<`1`>() + a[3].toUbit<`1`>()
    }

    @Run
    fun test() {
        var a: Ubit<`4`> = u0()
        var error = false
        do {
            val actual = populationCount(a)
            val expected = populationCountReference(a)
            if (actual != expected) {
                println("populationCount($a) = $actual (ERROR)")
                error = true
            } else {
                println("populationCount($a) = $actual")
            }
            a++
        } while (a != u0<`*`>())
        println()
        println("populationCount: ${if (error) "FAILED" else "PASS"}")
        if (error) fatal()
        else finish()
    }
}

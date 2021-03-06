/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

@Entry
object VectorEqualTest : Module() {

    @Run
    fun test() {
        var expected: Ubit<`4`> = u0()
        var error = false
        do {
            val a: Ubit<`16`> = randomUbit()
            var b: Ubit<`16`> = a

            if (!expected[0])
                b[0] = !b[0]
            if (!expected[1])
                b[4] = !b[4]
            if (!expected[2])
                b[8] = !b[8]
            if (!expected[3])
                b[12] = !b[12]

            val actual = vectorEqual(a, b)
            if (actual != expected) {
                println("vectorEqual($a, $b) = $actual (ERROR)")
                error = true
            } else {
                println("vectorEqual($a, $b) = $actual")
            }
            expected++
        } while (expected != u0<`*`>())
        println()
        println("vectorEqual: ${if (error) "FAILED" else "PASS"}")
        if (error) fatal()
        else finish()
    }
}

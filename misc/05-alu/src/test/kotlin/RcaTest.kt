/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

@Entry
object RcaTest : Module() {

    @Run
    fun test() {
        println("rca: Running random 32-bit inputs")
        repeat(1024) {
            val a: Ubit<`32`> = randomUbit()
            val b: Ubit<`32`> = randomUbit()
            val c = randomBoolean()
            val actual = rca<`32`>(a, b, c)
            val expected = a + b + if (c) u(1) else u(0)
            if (actual != expected) {
                println("rca: FAILED rca($a, $b, $c) = $actual")
                fatal()
            }
        }
        println("rca: PASSED")
    }
}

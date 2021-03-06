/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

@Entry
object Lt32Test : Module() {

    @Run
    fun test() {
        println("lt32: Running exhaustive 4-bit inputs")
        var x: Ubit<`8`> = u0()
        for (i in 0 until 256) {
            val a: Ubit<`32`> = x[3, 0].sext()
            val b: Ubit<`32`> = x[7, 4].sext()
            var actual = lt32(a, b, false)
            var expected = (a < b)
            if (actual != expected) {
                println("lt32: FAILED lt32($a, $b, 1'b0) = $actual")
                fatal()
            }
            actual = lt32(a, b, true)
            expected = (a.toSbit() < b.toSbit())
            if (actual != expected) {
                println("lt32: FAILED lt32($a, $b, 1'b1) = $actual")
                fatal()
            }
            x++
        }

        println("lt32: Running random 32-bit inputs")
        repeat(1024) {
            val a: Ubit<`32`> = randomUbit()
            val b: Ubit<`32`> = randomUbit()
            val isSigned = randomBoolean()
            val actual = lt32(a, b, isSigned)
            val expected = if (isSigned) (a.toSbit() < b.toSbit()) else (a < b)
            if (actual != expected) {
                println("lt32: FAILED lt32($a, $b, $isSigned) = $actual")
                fatal()
            }
        }

        println("lt32: PASSED")
        finish()
    }
}

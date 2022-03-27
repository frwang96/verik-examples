/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

@Entry
object MultiplyFunctionTest : Module() {

    @Run
    fun test() {
        repeat(128) {
            val a: Ubit<`32`> = randomUbit()
            val b: Ubit<`32`> = randomUbit()
            val actual = multiplyByAdding(a, b)
            val expected = a mul b
            if (actual != expected) {
                println("$a * $b expected $expected actual $actual")
                fatal()
            }
        }
        finish()
    }
}

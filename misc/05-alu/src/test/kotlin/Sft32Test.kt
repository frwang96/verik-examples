/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

@Entry
object Sft32Test : Module() {

    @Run
    fun test() {
        println("sft32: Running random 32-bit inputs")
        repeat(1024) {
            val a: Ubit<`32`> = randomUbit()
            val b: Ubit<`5`> = randomUbit()
            val shiftType = when (random(3)) {
                0 -> ShiftType.LEFT_LOGICAL
                1 -> ShiftType.RIGHT_LOGICAL
                else -> ShiftType.RIGHT_ARITHMETIC
            }

            val actual = sft32(a, b, shiftType)
            val expected = when (shiftType) {
                ShiftType.LEFT_LOGICAL -> a shl b
                ShiftType.RIGHT_LOGICAL -> a shr b
                ShiftType.RIGHT_ARITHMETIC -> a sshr b
            }
            if (actual != expected) {
                println("sft32: FAILED sft($a, $b, $shiftType) = $actual")
                fatal()
            }
        }
        println("sft32: PASSED")
        finish()
    }
}

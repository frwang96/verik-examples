/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

@Entry
object AluTest : Module() {

    fun aluReference(a: Ubit<`32`>, b: Ubit<`32`>, func: AluFunc): Ubit<`32`> {
        return when (func) {
            AluFunc.ADD -> a + b
            AluFunc.SUB -> a - b
            AluFunc.AND -> a and b
            AluFunc.OR -> a or b
            AluFunc.XOR -> a xor b
            AluFunc.SLT -> cat(u("31'b0"), a.toSbit() < b.toSbit())
            AluFunc.SLTU -> cat(u("31'b0"), a < b)
            AluFunc.SLL -> a shl b.tru<`5`>()
            AluFunc.SRL -> a shr b.tru<`5`>()
            AluFunc.SRA -> a sshr b.tru<`5`>()
        }
    }

    @Run
    fun test() {
        println("alu: Running random 32-bit inputs")
        repeat(1024) {
            val a: Ubit<`32`> = randomUbit()
            val b: Ubit<`32`> = randomUbit()
            val func = when (random(10)) {
                0 -> AluFunc.ADD
                1 -> AluFunc.SUB
                2 -> AluFunc.AND
                3 -> AluFunc.OR
                4 -> AluFunc.XOR
                5 -> AluFunc.SLT
                6 -> AluFunc.SLTU
                7 -> AluFunc.SLL
                8 -> AluFunc.SRL
                else -> AluFunc.SRA
            }
            val actual = alu(a, b, func)
            val expected = aluReference(a, b, func)
            if (actual != expected) {
                println("alu: FAILED alu($a, $b, $func) = $actual")
                fatal()
            }
        }
        println("alu: PASSED")
        finish()
    }
}

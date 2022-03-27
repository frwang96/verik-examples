/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

@Entry
object MultiplyModuleTest : Module() {

    val NUM_TESTS = 128

    var clk: Boolean = nc()
    var mul_in: MultiplierInput = nc()
    var mul_in_valid: Boolean = nc()
    var res: Ubit<`64`> = nc()
    var res_valid: Boolean = nc()

    @Make
    val multiplier = FoldedMultiplier(
        clk = clk,
        mul_in = mul_in,
        mul_in_valid = mul_in_valid,
        res = res,
        res_valid = res_valid
    )

    @Run
    fun test() {
        repeat(NUM_TESTS) {
            val a: Ubit<`32`> = randomUbit()
            val b: Ubit<`32`> = randomUbit()
            val expected = a mul b

            mul_in = MultiplierInput(a, b)
            mul_in_valid = true
            wait(posedge(clk))
            mul_in_valid = false
            while (!res_valid) wait(posedge(clk))
            if (res != expected) {
                println("FAIL $a * $b expected $expected actual $res")
                fatal()
            }
        }
        finish()
    }

    @Run
    fun runClk() {
        clk = false
        repeat(NUM_TESTS * 32 * 4) {
            clk = !clk
            delay(1)
        }
        println("FAIL due to timeout")
        fatal()
    }
}

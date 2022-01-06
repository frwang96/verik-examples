/*
 * Copyright (c) 2021 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Verik

import io.verik.core.*

@SimTop
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

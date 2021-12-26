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

    var clk: Boolean = nc()
    var mulIn: MultiplierInput = nc()
    var mulInValid: Boolean = nc()
    var res: Ubit<`64`> = nc()
    var resValid: Boolean = nc()

    val numTests = 128

    @Make
    val multiplier = FoldedMultiplier(
        clk = clk,
        mulIn = mulIn,
        mulInValid = mulInValid,
        res = res,
        resValid = resValid
    )

    @Run
    fun test() {
        repeat(numTests) {
            val a: Ubit<`32`> = randomUbit()
            val b: Ubit<`32`> = randomUbit()
            val expected = a mul b

            mulIn = MultiplierInput(a, b)
            mulInValid = true
            wait(posedge(clk))
            mulInValid = false
            while (!resValid) wait(posedge(clk))
            if (res != expected) {
                println("FAIL $a * $b expected $expected actual $res")
                fatal()
            }
        }
        finish()
    }

    @Run
    fun toggleClk() {
        clk = false
        repeat(numTests * 32 * 4) {
            clk = !clk
            delay(1)
        }
        println("FAIL due to timeout")
        fatal()
    }
}

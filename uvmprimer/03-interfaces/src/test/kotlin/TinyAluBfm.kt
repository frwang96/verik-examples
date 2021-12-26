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

class TinyAluBfm : ModuleInterface() {

    var clk: Boolean = nc()
    var rst_n: Boolean = nc()
    var a: Ubit<`8`> = nc()
    var b: Ubit<`8`> = nc()
    var op: Op = nc()
    var start: Boolean = nc()
    var done: Boolean = nc()
    var result: Ubit<`16`> = nc()

    @Run
    fun toggleClk() {
        clk = false
        repeat(1000) {
            delay(10)
            clk = !clk
        }
        println("FAIL timeout")
        fatal()
    }

    @Task
    fun resetAlu() {
        rst_n = false
        repeat(2) { wait(negedge(clk)) }
        rst_n = true
        start = false
    }

    @Task
    fun sendOp(nextA: Ubit<`8`>, nextB: Ubit<`8`>, nextOp: Op) {
        op = nextOp
        if (nextOp == Op.RST) {
            wait(posedge(clk))
            rst_n = false
            start = false
            wait(posedge(clk))
            delay(1)
            rst_n = true
        } else {
            wait(negedge(clk))
            a = nextA
            b = nextB
            start = true
            if (nextOp == Op.NOP) {
                wait(posedge(clk))
                delay(1)
            } else {
                do { wait(negedge(clk)) } while (!done)
            }
            start = false
        }
    }
}

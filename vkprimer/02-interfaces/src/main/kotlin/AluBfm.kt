/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import dut.Op
import io.verik.core.*

class AluBfm : ModuleInterface() {

    var clk: Boolean = nc()
    var reset_n: Boolean = nc()
    var a: Ubit<`8`> = nc()
    var b: Ubit<`8`> = nc()
    var op: Op = nc()
    var start: Boolean = nc()
    var done: Boolean = nc()
    var result: Ubit<`16`> = nc()

    @Run
    fun runClk() {
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
        reset_n = false
        repeat(2) { wait(negedge(clk)) }
        reset_n = true
        start = false
    }

    @Task
    fun sendOp(next_a: Ubit<`8`>, next_b: Ubit<`8`>, next_op: Op) {
        op = next_op
        if (next_op == Op.RST) {
            wait(posedge(clk))
            reset_n = false
            start = false
            wait(posedge(clk))
            delay(1)
            reset_n = true
        } else {
            wait(negedge(clk))
            a = next_a
            b = next_b
            start = true
            if (next_op == Op.NOP) {
                wait(posedge(clk))
                delay(1)
            } else {
                do { wait(negedge(clk)) } while (!done)
            }
            start = false
        }
    }
}

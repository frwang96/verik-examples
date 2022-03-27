/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "FunctionName", "LiftReturnOrAssignment")

import dut.operation_t
import dut.operation_t.no_op
import dut.operation_t.rst_op
import io.verik.core.*

class tinyalu_bfm : ModuleInterface() {

    var A: Ubit<`8`> = nc()
    var B: Ubit<`8`> = nc()
    var clk: Boolean = nc()
    var reset_n: Boolean = nc()
    var op: Ubit<`3`> = nc()
    var start: Boolean = nc()
    var done: Boolean = nc()
    var result: Ubit<`16`> = nc()
    var op_set: operation_t = nc()

    @Com
    fun set_op() {
        op = op_set.value
    }

    @Run
    fun run_clk() {
        clk = false
        repeat(1000) {
            delay(10)
            clk = !clk
        }
        println("FAIL timeout")
        fatal()
    }

    @Task
    fun reset_alu() {
        reset_n = false
        wait(negedge(clk))
        wait(negedge(clk))
        reset_n = true
        start = false
    }

    @Task
    fun send_op(iA: Ubit<`8`>, iB: Ubit<`8`>, iop: operation_t): Ubit<`16`> {
        op_set = iop
        if (iop == rst_op) {
            wait(posedge(clk))
            reset_n = false
            start = false
            wait(posedge(clk))
            delay(1)
            reset_n = true
        } else {
            wait(negedge(clk))
            A = iA
            B = iB
            start = true
            if (iop == no_op) {
                wait(posedge(clk))
                delay(1)
                start = false
            } else {
                do { wait(negedge(clk)) } while (!done)
                start = false
            }
        }
        return result
    }
}

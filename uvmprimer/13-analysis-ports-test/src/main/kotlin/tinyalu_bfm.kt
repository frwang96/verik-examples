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

    var command_monitor_h: command_monitor? = null
    lateinit var result_monitor_h: result_monitor

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

    var new_command: Boolean = nc()

    @Seq
    fun cmd_monitor() {
        on(posedge(clk)) {
            if (!start) {
                new_command = true
            } else {
                if (new_command) {
                    command_monitor_h!!.write_to_monitor(A, B, op)
                    new_command = (op == u(0b000))
                }
            }
        }
    }

    @Seq
    fun rst_monitor() {
        on(negedge(reset_n)) {
            if (command_monitor_h != null) {
                command_monitor_h!!.write_to_monitor(A, B, rst_op.value)
            }
        }
    }

    @Run
    fun result_monitor_thread() {
        forever {
            wait(posedge(clk))
            delay(1)
            if (done) result_monitor_h.write_to_monitor(result)
        }
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
}

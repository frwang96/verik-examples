/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("FunctionName", "unused", "LiftReturnOrAssignment", "ClassName")

import dut.operation_t
import dut.operation_t.*
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

    var command_monitor_h: command_monitor? = null

    fun op2enum(): operation_t {
        when (op) {
            u(0b000) -> return no_op
            u(0b001) -> return add_op
            u(0b010) -> return and_op
            u(0b011) -> return xor_op
            u(0b100) -> return mul_op
            u(0b111) -> return rst_op
            else -> fatal("Illegal operation on op bus")
        }
    }

    var in_command: Boolean = false

    @Seq
    fun op_monitor() {
        on(posedge(clk)) {
            if (start) {
                if (!in_command) {
                    command_monitor_h!!.write_to_monitor(A, B, op2enum())
                    in_command = (op2enum() != no_op)
                }
            } else {
                in_command = false
            }
        }
    }

    @Seq
    fun rst_monitor() {
        on(negedge(reset_n)) {
            if (command_monitor_h != null) {
                command_monitor_h!!.write_to_monitor(randomUbit(), u0(), rst_op)
            }
        }
    }

    lateinit var result_monitor_h: result_monitor

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

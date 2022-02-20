/*
 * Copyright (c) 2022 Francis Wang
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

import dut.Op
import io.verik.core.*

class TinyAluBfm : ModuleInterface() {

    lateinit var result_monitor: ResultMonitor
    lateinit var command_monitor: CommandMonitor

    var clk: Boolean = nc()
    var rst_n: Boolean = nc()
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
    fun sendOp(next_a: Ubit<`8`>, next_b: Ubit<`8`>, next_op: Op): Ubit<`16`> {
        op = next_op
        if (next_op == Op.RST) {
            wait(posedge(clk))
            rst_n = false
            start = false
            wait(posedge(clk))
            delay(1)
            rst_n = true
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
        return result
    }

    var new_command: Boolean = nc()

    @Seq
    fun monitorCommand() {
        on(posedge(clk)) {
            if (!start) {
                new_command = true
            } else {
                if (new_command) {
                    command_monitor.write(a, b, op)
                    new_command = (op == Op.NOP)
                }
            }
        }
    }

    @Seq
    fun monitorReset() {
        on(negedge(rst_n)) {
            command_monitor.write(a, b, Op.RST)
        }
    }

    @Seq
    fun monitorResult() {
        on(posedge(clk)) {
            if (done) result_monitor.write(result)
        }
    }
}

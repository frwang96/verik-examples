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
@file:Suppress("NON_EXHAUSTIVE_WHEN")

import dut.operation_t
import dut.operation_t.*
import dut.tinyalu
import io.verik.core.*

@Entry
object top : Module() {

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

    @Make
    val tiny_alu = tinyalu(
        A = A,
        B = B,
        clk = clk,
        op = op,
        reset_n = reset_n,
        start = start,
        done = done,
        result = result
    )

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

    fun get_op(): operation_t {
        val op_choice = randomUbit<`3`>()
        when (op_choice) {
            u(0b000) -> return no_op
            u(0b001) -> return add_op
            u(0b010) -> return and_op
            u(0b011) -> return xor_op
            u(0b100) -> return mul_op
            u(0b101) -> return no_op
            u(0b110) -> return rst_op
            u(0b111) -> return rst_op
        }
        return rst_op
    }

    fun get_data(): Ubit<`8`> {
        val zero_ones = randomUbit<`2`>()
        when (zero_ones) {
            u(0b00) -> return u(0x00)
            u(0b11) -> return u(0xff)
            else -> return randomUbit<`8`>()
        }
    }

    @Seq
    fun scoreboard() {
        on(posedge(done)) {
            var predicted_result: Ubit<`16`> = u0()
            when (op_set) {
                add_op -> predicted_result = (A add B).ext()
                and_op -> predicted_result = (A and B).ext()
                xor_op -> predicted_result = (A xor B).ext()
                mul_op -> predicted_result = A mul B
            }
            if (op_set != no_op && op_set != rst_op) {
                if (predicted_result != result) {
                    error("FAILED: A: $A  B: $B  op=$op  result=$result")
                }
            }
        }
    }

    @Run
    fun test() {
        reset_n = false
        wait(negedge(clk))
        wait(negedge(clk))
        reset_n = true
        start = false
        op_set = no_op
        repeat(100) {
            wait(negedge(clk))
            op_set = get_op()
            A = get_data()
            B = get_data()
            start = true
            when (op_set) {
                no_op -> {
                    wait(posedge(clk))
                    start = false
                }
                rst_op -> {
                    reset_n = false
                    start = false
                    wait(negedge(clk))
                    reset_n = true
                }
                else -> {
                    wait(done)
                    start = false
                }
            }
        }
        finish()
    }
}

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

import dut.operation_t
import dut.tinyalu
import io.verik.core.*

@Entry
object TbTop : Module() {

    var clk: Boolean = nc()
    var rst_n: Boolean = nc()
    var a: Ubit<`8`> = nc()
    var b: Ubit<`8`> = nc()
    var op: operation_t = nc()
    var start: Boolean = nc()
    var done: Boolean = nc()
    var result: Ubit<`16`> = nc()

    @Make
    val tiny_alu = tinyalu(
        A = a,
        B = b,
        clk = clk,
        op = op.value,
        reset_n = rst_n,
        start = start,
        done = done,
        result = result
    )

    @Run
    fun test() {
        rst_n = false
        repeat(2) { wait(negedge(clk)) }
        rst_n = true
        start = false
        op = operation_t.no_op
        repeat(100) {
            wait(negedge(clk))
            a = randomData()
            b = randomData()
            op = randomOp()
            start = true
            when (op) {
                operation_t.no_op -> {
                    wait(posedge(clk))
                    start = false
                }
                operation_t.rst_op -> {
                    rst_n = false
                    start = false
                    wait(negedge(clk))
                    rst_n = true
                }
                else -> {
                    wait(done)
                    start = false
                }
            }
        }
        finish()
    }

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

    @Seq
    fun scoreboard() {
        on(posedge(done)) {
            val expected: Ubit<`16`> = when(op) {
                operation_t.add_op -> (a add b).ext()
                operation_t.and_op -> (a and b).ext()
                operation_t.xor_op -> (a xor b).ext()
                operation_t.mul_op -> a mul b
                else -> u0()
            }
            if (op != operation_t.no_op && op != operation_t.rst_op) {
                print("[${time()}] ")
                if (expected != result) print("FAIL ") else print("PASS ")
                println("a=$a b=$b op=$op result=$result expected=$expected")
            }
        }
    }

    fun randomOp(): operation_t {
        return when (random(6)) {
            0 -> operation_t.no_op
            1 -> operation_t.add_op
            2 -> operation_t.and_op
            3 -> operation_t.xor_op
            4 -> operation_t.mul_op
            else -> operation_t.rst_op
        }
    }

    fun randomData(): Ubit<`8`> {
        return when (random(4)) {
            0 -> u(0x00)
            1 -> u(0xff)
            else -> randomUbit<`8`>()
        }
    }
}

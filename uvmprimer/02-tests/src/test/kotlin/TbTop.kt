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
object TbTop : Module() {

    var clk: Boolean = nc()
    var rst_n: Boolean = nc()
    var a: Ubit<`8`> = nc()
    var b: Ubit<`8`> = nc()
    var op: Op = nc()
    var start: Boolean = nc()
    var done: Boolean = nc()
    var result: Ubit<`16`> = nc()

    @Make
    val tiny_alu = TinyAlu(
        clk = clk,
        rst_n = rst_n,
        a = a,
        b = b,
        op = op,
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
        repeat(100) {
            wait(negedge(clk))
            a = randomData()
            b = randomData()
            op = randomOp()
            start = true
            when (op) {
                Op.NOP -> {
                    wait(posedge(clk))
                    start = false
                }
                Op.RST -> {
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
                Op.ADD -> (a add b).ext()
                Op.AND -> (a and b).ext()
                Op.XOR -> (a xor b).ext()
                Op.MUL -> a mul b
                else -> u0()
            }
            if (op != Op.NOP && op != Op.RST) {
                print("[${time()}] ")
                if (expected != result) print("FAIL ") else print("PASS ")
                println("a=$a b=$b op=$op result=$result expected=$expected")
            }
        }
    }

    fun randomOp(): Op {
        return when (random(5)) {
            0 -> Op.NOP
            1 -> Op.ADD
            2 -> Op.AND
            3 -> Op.XOR
            4 -> Op.MUL
            else -> Op.RST
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

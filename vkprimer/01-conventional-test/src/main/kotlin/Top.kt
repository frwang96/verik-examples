/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import dut.Alu
import dut.Op
import io.verik.core.*

@Entry
object Top : Module() {

    var clk: Boolean = nc()
    var reset_n: Boolean = nc()
    var a: Ubit<`8`> = nc()
    var b: Ubit<`8`> = nc()
    var op: Op = nc()
    var start: Boolean = nc()
    var done: Boolean = nc()
    var result: Ubit<`16`> = nc()

    @Make
    val alu = Alu(
        clk = clk,
        reset_n = reset_n,
        a = a,
        b = b,
        op = op,
        start = start,
        done = done,
        result = result
    )

    class OpCoverGroup(
        @In var op: Op,
        @In var a: Ubit<`8`>,
        @In var b: Ubit<`8`>,
    ) : CoverGroup() {

        @Cover
        val cp_op = cp(op)

        @Cover
        val cp_a = cp(
            a,
            "bins zeros = {8'h00}",
            "bins others = {[8'h01:8'hfe]}",
            "bins ones = {8'hff}"
        )

        @Cover
        val cp_b = cp(
            b,
            "bins zeros = {8'h00}",
            "bins others = {[8'h01:8'hfe]}",
            "bins ones = {8'hff}"
        )

        @Cover
        val cc_op_a_b = cc(
            cp_op,
            cp_a,
            cp_b,
            "ignore_bins others = binsof($cp_a.others) && binsof($cp_b.others)"
        )
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

    @Run
    fun runCoverage() {
        val op_cg = OpCoverGroup(op, a, b)
        forever {
            wait(negedge(clk))
            op_cg.sample()
        }
    }

    fun randomOp(): Op {
        return when (random(6)) {
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
                val resultString = "a=$a b=$b op=$op result=$result expected=$expected"
                if (expected != result) {
                    println("FAIL $resultString")
                } else {
                    println("PASS $resultString")
                }
            }
        }
    }

    @Run
    fun test() {
        reset_n = false
        repeat(2) { wait(negedge(clk)) }
        reset_n = true
        start = false
        op = Op.NOP
        repeat(100) {
            wait(negedge(clk))
            op = randomOp()
            a = randomData()
            b = randomData()
            start = true
            when (op) {
                Op.NOP -> {
                    wait(posedge(clk))
                    start = false
                }
                Op.RST -> {
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

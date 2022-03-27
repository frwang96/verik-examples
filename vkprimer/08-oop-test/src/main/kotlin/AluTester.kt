/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import dut.Op
import io.verik.core.*

class AluTester(val bfm: AluBfm) : Class() {

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

    @Task
    fun execute() {
        bfm.resetAlu()
        repeat(100) {
            val a = randomData()
            val b = randomData()
            val op = randomOp()
            val result = bfm.sendOp(a, b, op)
            println("$a $op $b = $result")
        }
        delay(100)
        finish()
    }
}

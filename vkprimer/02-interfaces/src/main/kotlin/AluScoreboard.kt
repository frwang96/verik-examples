/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import dut.Op
import io.verik.core.*

class AluScoreboard(val bfm: AluBfm) : Module() {

    @Seq
    fun check() {
        on(posedge(bfm.done)) {
            val expected: Ubit<`16`> = when(bfm.op) {
                Op.ADD -> (bfm.a add bfm.b).ext()
                Op.AND -> (bfm.a and bfm.b).ext()
                Op.XOR -> (bfm.a xor bfm.b).ext()
                Op.MUL -> (bfm.a mul bfm.b).ext()
                else -> u0()
            }
            if (bfm.op != Op.NOP && bfm.op != Op.RST) {
                val resultString = "a=${bfm.a} b=${bfm.b} op=${bfm.op} result=${bfm.result} expected=$expected"
                if (expected != bfm.result) {
                    println("FAIL $resultString")
                } else {
                    println("PASS $resultString")
                }
            }
        }
    }
}

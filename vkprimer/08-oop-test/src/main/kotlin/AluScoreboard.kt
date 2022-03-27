/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import dut.Op
import io.verik.core.*

class AluScoreboard(val bfm: AluBfm) : Class() {

    @Task
    fun execute() {
        forever {
            wait(posedge(bfm.done))
            delay(1)
            val predicted_result = when (bfm.op) {
                Op.ADD -> (bfm.a add bfm.b).ext()
                Op.AND -> (bfm.a and bfm.b).ext()
                Op.XOR -> (bfm.a xor bfm.b).ext()
                Op.MUL -> bfm.a mul bfm.b
                else -> u0()
            }
            if (bfm.op != Op.NOP && bfm.op != Op.RST) {
                assert(predicted_result == bfm.result) {
                    error("FAILED a=${bfm.a} b=${bfm.b} op=${bfm.op} result=${bfm.result}")
                }
            }
        }
    }
}

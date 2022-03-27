/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("NON_EXHAUSTIVE_WHEN", "ClassName", "ConvertSecondaryConstructorToPrimary")

import dut.operation_t.*
import io.verik.core.*

class scoreboard : Class {

    val bfm: tinyalu_bfm

    constructor(b: tinyalu_bfm) : super() {
        bfm = b
    }

    @Task
    fun execute() {
        var predicted_result: Ubit<`16`> = u0()
        forever {
            wait(posedge(bfm.done))
            delay(1)
            when (bfm.op_set) {
                add_op -> predicted_result = (bfm.A add bfm.B).ext()
                and_op -> predicted_result = (bfm.A and bfm.B).ext()
                xor_op -> predicted_result = (bfm.A xor bfm.B).ext()
                mul_op -> predicted_result = bfm.A mul bfm.B
            }
            if (bfm.op_set != no_op && bfm.op_set != rst_op) {
                if (predicted_result != bfm.result) {
                    error("FAILED: A: ${bfm.A}  b:${bfm.B}  op:${bfm.op_set}  result:${bfm.result}")
                }
            }
        }
    }
}

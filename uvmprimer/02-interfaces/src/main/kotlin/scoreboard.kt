/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("NON_EXHAUSTIVE_WHEN", "ClassName")

import dut.operation_t.*
import io.verik.core.*

class scoreboard(val bfm: tinyalu_bfm) : Module() {

    @Seq
    fun check() {
        on(posedge(bfm.done)) {
            var predicted_result = u0<`16`>()
            when(bfm.op_set) {
                add_op -> predicted_result = (bfm.A add bfm.B).ext()
                and_op -> predicted_result = (bfm.A and bfm.B).ext()
                xor_op -> predicted_result = (bfm.A xor bfm.B).ext()
                mul_op -> predicted_result = (bfm.A mul bfm.B).ext()
            }
            if (bfm.op_set != no_op && bfm.op_set != rst_op) {
                if (predicted_result != bfm.result) {
                    error("FAILED: A: ${bfm.A}  B: ${bfm.B}  op=${bfm.op}  result=${bfm.result}")
                }
            }
        }
    }
}

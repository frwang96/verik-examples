/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

package dut

import io.verik.core.*

class SingleCycleAlu(
    @In var clk: Boolean,
    @In var reset_n: Boolean,
    @In var a: Ubit<`8`>,
    @In var b: Ubit<`8`>,
    @In var op: Op,
    @In var start: Boolean,
    @Out var done: Boolean,
    @Out var result: Ubit<`16`>
) : Module() {

    @Seq
    fun compute() {
        on (posedge(clk)) {
            if (!reset_n) {
                done = false
                result = u0()
            } else {
                done = (start && op != Op.NOP)
                result = when(op) {
                    Op.ADD -> (a add b).ext()
                    Op.AND -> (a and b).ext()
                    Op.XOR -> (a xor b).ext()
                    else -> u0()
                }
            }
        }
    }
}

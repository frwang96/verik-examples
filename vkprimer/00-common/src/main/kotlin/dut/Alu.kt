/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

package dut

import io.verik.core.*

class Alu(
    @In var clk: Boolean,
    @In var reset_n: Boolean,
    @In var a: Ubit<`8`>,
    @In var b: Ubit<`8`>,
    @In var op: Op,
    @In var start: Boolean,
    @Out var done: Boolean,
    @Out var result: Ubit<`16`>
) : Module() {

    var done_single: Boolean = nc()
    var done_multi: Boolean = nc()
    var result_single: Ubit<`16`> = nc()
    var result_multi: Ubit<`16`> = nc()
    var start_single: Boolean = nc()
    var start_multi: Boolean = nc()

    @Make
    val single_cycle_alu = SingleCycleAlu(
        clk = clk,
        reset_n = reset_n,
        a = a,
        b = b,
        op = op,
        start = start_single,
        done = done_single,
        result = result_single
    )

    @Make
    val multi_cycle_alu = MultiCycleAlu(
        clk = clk,
        reset_n = reset_n,
        a = a,
        b = b,
        start = start_multi,
        done = done_multi,
        result = result_multi
    )

    @Com
    fun demux() {
        when (op) {
            Op.MUL -> {
                start_single = false
                start_multi = start
            }
            else -> {
                start_single = start
                start_multi = false
            }
        }
    }

    @Com
    fun mux() {
        when (op) {
            Op.MUL -> {
                done = done_multi
                result = result_multi
            }
            else -> {
                done = done_single
                result = result_single
            }
        }
    }
}

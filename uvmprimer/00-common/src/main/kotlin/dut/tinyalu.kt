/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("REDUNDANT_ELSE_IN_WHEN", "ClassName", "FunctionName", "LiftReturnOrAssignment")

package dut

import io.verik.core.*

class tinyalu(
    @In var A: Ubit<`8`>,
    @In var B: Ubit<`8`>,
    @In var clk: Boolean,
    @In var op: Ubit<`3`>,
    @In var reset_n: Boolean,
    @In var start: Boolean,
    @Out var done: Boolean,
    @Out var result: Ubit<`16`>
) : Module() {

    var done_aax: Boolean = nc()
    var done_mult: Boolean = nc()
    var result_aax: Ubit<`16`> = nc()
    var result_mult: Ubit<`16`> = nc()
    var start_single: Boolean = nc()
    var start_mult: Boolean = nc()
    var done_internal: Boolean = nc()

    @Make
    val single_cycle = single_cycle(
        A = A,
        B = B,
        clk = clk,
        op = op,
        reset_n = reset_n,
        start = start_single,
        done_aax = done_aax,
        result_aax = result_aax
    )

    @Make
    val three_cycle = three_cycle(
        A = A,
        B = B,
        clk = clk,
        reset_n = reset_n,
        start = start_mult,
        done_mult = done_mult,
        result_mult = result_mult
    )

    @Com
    fun start_demux() {
        when (op[2]) {
            false -> {
                start_single = start
                start_mult = false
            }
            true -> {
                start_single = false
                start_mult = start
            }
        }
    }

    @Com
    fun result_mux() {
        when (op[2]) {
            false -> result = result_aax
            true -> result = result_mult
            else -> result = ux()
        }
    }

    @Com
    fun done_mux() {
        when (op[2]) {
            false -> done_internal = done_aax
            true -> done_internal = done_mult
            else -> done_internal = unknown
        }
    }

    @Com
    fun set_done() {
        done = done_internal
    }
}

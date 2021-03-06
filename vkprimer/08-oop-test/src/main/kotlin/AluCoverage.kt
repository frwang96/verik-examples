/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import dut.Op
import io.verik.core.*

class AluCoverage(
    val bfm: AluBfm
) : Class() {

    @Task
    fun execute() {
        val op_cg = OpCoverGroup(bfm.op, bfm.a, bfm.b)
        forever {
            wait(negedge(bfm.clk))
            op_cg.sample()
        }
    }
}

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

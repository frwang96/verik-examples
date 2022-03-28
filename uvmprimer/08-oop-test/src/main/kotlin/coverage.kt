/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "JoinDeclarationAndAssignment", "ConvertSecondaryConstructorToPrimary")

import dut.operation_t
import dut.operation_t.*
import io.verik.core.*

class coverage : Class {

    val bfm: tinyalu_bfm

    var A: Ubit<`8`> = nc()
    var B: Ubit<`8`> = nc()
    var op_set: operation_t = nc()

    var op_cov_i: op_cov
    var zeros_or_ones_on_ops_i: zeros_or_ones_on_ops

    constructor(b: tinyalu_bfm) : super() {
        op_cov_i = op_cov(op_set)
        zeros_or_ones_on_ops_i = zeros_or_ones_on_ops(op_set, A, B)
        bfm = b
    }

    @Task
    fun execute() {
        forever {
            wait(negedge(bfm.clk))
            A = bfm.A
            B = bfm.B
            op_set = bfm.op_set
            op_cov_i.sample()
            zeros_or_ones_on_ops_i.sample()
        }
    }
}

class op_cov(
    @In var op_set: operation_t
) : CoverGroup() {

    @Cover
    val cp_op_set = cp(
        op_set,
        "bins single_cycle[] = {[$add_op:$xor_op], $rst_op, $no_op}",
        "bins multi_cycle = {$mul_op}",
        "bins opn_rst[] = ([$add_op:$mul_op] => $rst_op)",
        "bins rst_opn[] = ($rst_op => [$add_op:$mul_op])",
        "bins sngl_mul[] = ([$add_op:$xor_op], $no_op => $mul_op)",
        "bins mul_sngl[] = ($mul_op => [$add_op:$xor_op], $no_op)",
        "bins twoops[] = ([$add_op:$mul_op] [* 2])",
        "bins manymult = ($mul_op [* 3:5])"
    )
}

class zeros_or_ones_on_ops(
    @In var op_set: operation_t,
    @In var A: Ubit<`8`>,
    @In var B: Ubit<`8`>
) : CoverGroup() {

    @Cover
    val all_ops = cp(
        op_set,
        "ignore_bins null_ops = {$rst_op, $no_op}"
    )

    @Cover
    val a_leg = cp(
        A,
        "bins zeros = {8'h00}",
        "bins others = {[8'h01:8'hfe]}",
        "bins ones = {8'hff}"
    )

    @Cover
    val b_leg = cp(
        B,
        "bins zeros = {8'h00}",
        "bins others = {[8'h01:8'hfe]}",
        "bins ones = {8'hff}"
    )

    @Cover
    val op_00_FF = cc(
        a_leg,
        b_leg,
        all_ops,
        "bins add_00 = binsof ($all_ops) intersect {$add_op} && (binsof ($a_leg.zeros) || binsof ($b_leg.zeros))",
        "bins add_FF = binsof ($all_ops) intersect {$add_op} && (binsof ($a_leg.ones) || binsof ($b_leg.ones))",
        "bins and_00 = binsof ($all_ops) intersect {$and_op} && (binsof ($a_leg.zeros) || binsof ($b_leg.zeros))",
        "bins and_FF = binsof ($all_ops) intersect {$and_op} && (binsof ($a_leg.ones) || binsof ($b_leg.ones))",
        "bins xor_00 = binsof ($all_ops) intersect {$xor_op} && (binsof ($a_leg.zeros) || binsof ($b_leg.zeros))",
        "bins xor_FF = binsof ($all_ops) intersect {$xor_op} && (binsof ($a_leg.ones) || binsof ($b_leg.ones))",
        "bins mul_00 = binsof ($all_ops) intersect {$mul_op} && (binsof ($a_leg.zeros) || binsof ($b_leg.zeros))",
        "bins mul_FF = binsof ($all_ops) intersect {$mul_op} && (binsof ($a_leg.ones) || binsof ($b_leg.ones))",
        "bins mul_max = binsof ($all_ops) intersect {$mul_op} && (binsof ($a_leg.ones) && binsof ($b_leg.ones))",
        "ignore_bins others_only = binsof($a_leg.others) && binsof($b_leg.others)"
    )
}

/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName")

import dut.operation_t
import dut.operation_t.*
import io.verik.core.*

class coverage(
    val bfm: tinyalu_bfm
) : Module() {

    var A: Ubit<`8`> = nc()
    var B: Ubit<`8`> = nc()
    var op_set: operation_t = nc()

    class op_cov(
        @In var op_set: operation_t
    ) : CoverGroup() {

        @Cover
        val cp_op_set = cp(op_set) {
            bins("single_cycle", "{[$add_op : $xor_op], $rst_op, $no_op}")
            bin("multi_cycle", "{$mul_op}")

            bins("opn_rst", "([$add_op:$mul_op] => $rst_op)")
            bins("rst_opn", "($rst_op => [$add_op:$mul_op])")

            bins("sngl_mul", "([$add_op:$xor_op], $no_op => $mul_op)")
            bins("mul_sngl", "($mul_op => [$add_op:$xor_op], $no_op)")

            bins("twoops", "([$add_op:$mul_op] [* 2])")
            bin("manymult", "($mul_op [* 3:5])")
        }
    }

    class zeros_or_ones_on_ops(
        @In var op_set: operation_t,
        @In var A: Ubit<`8`>,
        @In var B: Ubit<`8`>
    ) : CoverGroup() {

        @Cover
        val all_ops = cp(op_set) {
            ignoreBin("null_ops", "{$rst_op, $no_op}")
        }

        @Cover
        val a_leg = cp(A) {
            bin("zeros", "{'h00}")
            bin("others", "{['h01:'hFE]}")
            bin("ones", "{'hFF}")
        }

        @Cover
        val b_leg = cp(B) {
            bin("zeros", "{'h00}")
            bin("others", "{['h01:'hFE]}")
            bin("ones", "{'hFF}")
        }

        @Cover
        val op_00_FF = cc(a_leg, b_leg, all_ops) {
            bin("add_00", "binsof ($all_ops) intersect {$add_op} && (binsof ($a_leg.zeros) || binsof ($b_leg.zeros))")
            bin("add_FF", "binsof ($all_ops) intersect {$add_op} && (binsof ($a_leg.ones) || binsof ($b_leg.ones))")
            bin("and_00", "binsof ($all_ops) intersect {$and_op} && (binsof ($a_leg.zeros) || binsof ($b_leg.zeros))")
            bin("and_FF", "binsof ($all_ops) intersect {$and_op} && (binsof ($a_leg.ones) || binsof ($b_leg.ones))")
            bin("xor_00", "binsof ($all_ops) intersect {$xor_op} && (binsof ($a_leg.zeros) || binsof ($b_leg.zeros))")
            bin("xor_FF", "binsof ($all_ops) intersect {$xor_op} && (binsof ($a_leg.ones) || binsof ($b_leg.ones))")
            bin("mul_00", "binsof ($all_ops) intersect {$mul_op} && (binsof ($a_leg.zeros) || binsof ($b_leg.zeros))")
            bin("mul_FF", "binsof ($all_ops) intersect {$mul_op} && (binsof ($a_leg.ones) || binsof ($b_leg.ones))")
            bin("mul_max", "binsof ($all_ops) intersect {$mul_op} && (binsof ($a_leg.ones) && binsof ($b_leg.ones))")
            ignoreBin("others_only", "binsof($a_leg.others) && binsof($b_leg.others)")
        }
    }

    lateinit var oc: op_cov
    lateinit var c_00_FF: zeros_or_ones_on_ops

    @Run
    fun coverage() {
        oc = op_cov(op_set)
        c_00_FF = zeros_or_ones_on_ops(op_set, A, B)
        forever {
            wait(negedge(bfm.clk))
            A = bfm.A
            B = bfm.B
            op_set = bfm.op_set
            oc.sample()
            c_00_FF.sample()
        }
    }
}
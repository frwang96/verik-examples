/*
 * Copyright (c) 2022 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Verik
@file:Suppress("ConvertSecondaryConstructorToPrimary", "unused", "ClassName", "JoinDeclarationAndAssignment")

import dut.operation_t
import dut.operation_t.*
import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_subscriber
import io.verik.core.*

class coverage : uvm_subscriber<command_s> {

    @Inj
    val header = "`uvm_component_utils(${t<coverage>()});"

    var A: Ubit<`8`> = nc()
    var B: Ubit<`8`> = nc()
    var op_set: operation_t = nc()

    var op_cov_i: op_cov
    var zeros_or_ones_on_ops_i: zeros_or_ones_on_ops

    constructor(name: String, parent: uvm_component?) : super(name, parent) {
        op_cov_i = op_cov(op_set)
        zeros_or_ones_on_ops_i = zeros_or_ones_on_ops(op_set, A, B)
    }

    override fun write(t: command_s) {
        A = t.A
        B = t.B
        op_set = t.op
        op_cov_i.sample()
        zeros_or_ones_on_ops_i.sample()
    }
}

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

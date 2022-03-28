/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress(
    "NON_EXHAUSTIVE_WHEN",
    "ClassName",
    "FunctionName",
    "MoveVariableDeclarationIntoWhen",
    "LiftReturnOrAssignment"
)

import dut.operation_t
import dut.operation_t.*
import dut.tinyalu
import io.verik.core.*

@Entry
object top : Module() {

    var A: Ubit<`8`> = nc()
    var B: Ubit<`8`> = nc()
    var clk: Boolean = nc()
    var reset_n: Boolean = nc()
    var op: Ubit<`3`> = nc()
    var start: Boolean = nc()
    var done: Boolean = nc()
    var result: Ubit<`16`> = nc()
    var op_set: operation_t = nc()

    @Com
    fun set_op() {
        op = op_set.value
    }

    @Make
    val DUT = tinyalu(
        A = A,
        B = B,
        clk = clk,
        op = op,
        reset_n = reset_n,
        start = start,
        done = done,
        result = result
    )

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

    @Run
    fun run_clk() {
        clk = false
        repeat(1000) {
            delay(10)
            clk = !clk
        }
        println("FAIL timeout")
        fatal()
    }

    lateinit var oc: op_cov
    lateinit var c_00_FF: zeros_or_ones_on_ops

    @Run
    fun coverage() {
        oc = op_cov(op_set)
        c_00_FF = zeros_or_ones_on_ops(op_set, A, B)
        forever {
            wait(negedge(clk))
            oc.sample()
            c_00_FF.sample()
        }
    }

    fun get_op(): operation_t {
        val op_choice = randomUbit<`3`>()
        when (op_choice) {
            u(0b000) -> return no_op
            u(0b001) -> return add_op
            u(0b010) -> return and_op
            u(0b011) -> return xor_op
            u(0b100) -> return mul_op
            u(0b101) -> return no_op
            u(0b110) -> return rst_op
            u(0b111) -> return rst_op
        }
        return rst_op
    }

    fun get_data(): Ubit<`8`> {
        val zero_ones = randomUbit<`2`>()
        when (zero_ones) {
            u(0b00) -> return u(0x00)
            u(0b11) -> return u(0xff)
            else -> return randomUbit<`8`>()
        }
    }

    @Seq
    fun scoreboard() {
        on(posedge(done)) {
            var predicted_result: Ubit<`16`> = u0()
            when (op_set) {
                add_op -> predicted_result = (A add B).ext()
                and_op -> predicted_result = (A and B).ext()
                xor_op -> predicted_result = (A xor B).ext()
                mul_op -> predicted_result = A mul B
            }
            if (op_set != no_op && op_set != rst_op) {
                if (predicted_result != result) {
                    error("FAILED: A: $A  B: $B  op=$op  result=$result")
                }
            }
        }
    }

    @Run
    fun test() {
        reset_n = false
        wait(negedge(clk))
        wait(negedge(clk))
        reset_n = true
        start = false
        op_set = no_op
        repeat(100) {
            wait(negedge(clk))
            op_set = get_op()
            A = get_data()
            B = get_data()
            start = true
            when (op_set) {
                no_op -> {
                    wait(posedge(clk))
                    start = false
                }
                rst_op -> {
                    reset_n = false
                    start = false
                    wait(negedge(clk))
                    reset_n = true
                }
                else -> {
                    wait(done)
                    start = false
                }
            }
        }
        finish()
    }
}

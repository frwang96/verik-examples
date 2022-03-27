/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "FunctionName", "MoveVariableDeclarationIntoWhen", "LiftReturnOrAssignment")

import dut.operation_t
import dut.operation_t.*
import io.verik.core.*

class tester(val bfm: tinyalu_bfm) : Module() {

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

    @Run
    fun test() {
        bfm.reset_alu()
        repeat(100) {
            val op_set = get_op()
            val iA = get_data()
            val iB = get_data()
            bfm.send_op(iA, iB, op_set)
        }
        finish()
    }
}

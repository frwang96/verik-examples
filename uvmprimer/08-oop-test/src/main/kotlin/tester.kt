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
@file:Suppress(
    "ConvertSecondaryConstructorToPrimary",
    "FunctionName",
    "MoveVariableDeclarationIntoWhen",
    "ClassName",
    "LiftReturnOrAssignment"
)

import dut.operation_t
import dut.operation_t.*
import io.verik.core.*

class tester : Class {

    val bfm: tinyalu_bfm

    constructor(b: tinyalu_bfm) : super() {
        bfm = b
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

    @Task
    fun execute() {
        var iA: Ubit<`8`>
        var iB: Ubit<`8`>
        var result: Ubit<`16`>
        var op_set: operation_t
        bfm.reset_alu()
        op_set = rst_op
        iA = get_data()
        iB = get_data()
        bfm.send_op(iA, iB, op_set)
        op_set = mul_op
        bfm.send_op(iA, iB, op_set)
        bfm.send_op(iA, iB, op_set)
        op_set = rst_op
        bfm.send_op(iA, iB, op_set)
        repeat(100) {
            op_set = get_op()
            iA = get_data()
            iB = get_data()
            result = bfm.send_op(iA, iB, op_set)
            println("$iA $op_set $iB = $result")
        }
        finish()
    }
}

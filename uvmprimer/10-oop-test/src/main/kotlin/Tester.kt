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

import dut.operation_t
import io.verik.core.*

class Tester(val bfm: TinyAluBfm) : Class() {

    @Task
    fun execute() {
        bfm.resetAlu()
        repeat(10) {
            val a = getData()
            val b = getData()
            val op = getOp()
            bfm.sendOp(a, b, op)
        }
        delay(100)
        finish()
    }

    private fun getData(): Ubit<`8`> {
        return when (random(4)) {
            0 -> u(0x00)
            1 -> u(0xff)
            else -> randomUbit<`8`>()
        }
    }

    private fun getOp(): operation_t {
        return when (random(6)) {
            0 -> operation_t.no_op
            1 -> operation_t.add_op
            2 -> operation_t.and_op
            3 -> operation_t.xor_op
            4 -> operation_t.mul_op
            else -> operation_t.rst_op
        }
    }
}

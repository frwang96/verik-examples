/*
 * Copyright (c) 2021 Francis Wang
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

import io.verik.core.*

class Tester(val bfm: TinyAluBfm) {

    @Task
    fun execute() {
        bfm.resetAlu()
        bfm.sendOp(u0(), u0(), Op.RST)
        repeat(10) {
            val a = getData()
            val b = getData()
            val op = getOp()
            val result = bfm.sendOp(a, b, op)
            println("$a $op $b = $result")
        }
    }

    private fun getData(): Ubit<`8`> {
        return when (random(4)) {
            0 -> u(0x00)
            1 -> u(0xff)
            else -> randomUbit<`8`>()
        }
    }

    private fun getOp(): Op {
        return when (random(5)) {
            0 -> Op.NOP
            1 -> Op.ADD
            2 -> Op.AND
            3 -> Op.XOR
            4 -> Op.MUL
            else -> Op.RST
        }
    }
}

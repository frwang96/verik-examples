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

import dut.Op
import io.verik.core.*

class Scoreboard(val bfm: AluBfm) : Class() {

    @Task
    fun execute() {
        forever {
            wait(posedge(bfm.done))
            delay(1)
            val predicted_result = when (bfm.op) {
                Op.ADD -> (bfm.a add bfm.b).ext()
                Op.AND -> (bfm.a and bfm.b).ext()
                Op.XOR -> (bfm.a xor bfm.b).ext()
                Op.MUL -> bfm.a mul bfm.b
                else -> u0()
            }
            if (bfm.op != Op.NOP && bfm.op != Op.RST) {
                assert(predicted_result == bfm.result) {
                    error("FAILED a=${bfm.a} b=${bfm.b} op=${bfm.op} result=${bfm.result}")
                }
            }
        }
    }
}

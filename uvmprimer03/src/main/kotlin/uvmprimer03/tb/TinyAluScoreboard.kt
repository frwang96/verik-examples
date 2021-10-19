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

package uvmprimer03.tb

import io.verik.core.*
import uvmprimer03.dut.Op

class TinyAluScoreboard(val bfm: TinyAluBfm) : Module() {

    @Seq
    fun check() {
        on(posedge(bfm.done)) {
            val expected: Ubit<`16`> = when(bfm.op) {
                Op.ADD -> bfm.a add bfm.b
                Op.AND -> bfm.a and bfm.b
                Op.XOR -> bfm.a xor bfm.b
                Op.MUL -> bfm.a mul bfm.b
                else -> u0<`16`>()
            }
            if (bfm.op != Op.NOP && bfm.op != Op.RST) {
                print("[${time()}] ")
                if (expected != bfm.result) print("FAIL ") else print("PASS ")
                println("a=${bfm.a} b=${bfm.b} op=${bfm.op} result=${bfm.result} expected=$expected")
            }
        }
    }
}

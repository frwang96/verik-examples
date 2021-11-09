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

package uvmprimer10.dut

import io.verik.core.*

class SingleCycleAlu(
    @In var clk: Boolean,
    @In var rst_n: Boolean,
    @In var a: Ubit<`8`>,
    @In var b: Ubit<`8`>,
    @In var op: Op,
    @In var start: Boolean,
    @Out var done_aax: Boolean,
    @Out var result_aax: Ubit<`16`>
) : Module() {

    @Seq
    fun compute() {
        on (posedge(clk)) {
            if (!rst_n) {
                done_aax = false
                result_aax = u0()
            } else {
                done_aax = (start && op != Op.NOP)
                result_aax = when(op) {
                    Op.ADD -> (a add b).uext()
                    Op.AND -> (a and b).uext()
                    Op.XOR -> (a xor b).uext()
                    else -> u0()
                }
            }
        }
    }
}

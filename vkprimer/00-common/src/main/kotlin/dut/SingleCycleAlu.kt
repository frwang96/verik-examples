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

package dut

import io.verik.core.*

class SingleCycleAlu(
    @In var clk: Boolean,
    @In var reset_n: Boolean,
    @In var a: Ubit<`8`>,
    @In var b: Ubit<`8`>,
    @In var op: Op,
    @In var start: Boolean,
    @Out var done: Boolean,
    @Out var result: Ubit<`16`>
) : Module() {

    @Seq
    fun compute() {
        on (posedge(clk)) {
            if (!reset_n) {
                done = false
                result = u0()
            } else {
                done = (start && op != Op.NOP)
                result = when(op) {
                    Op.ADD -> (a add b).ext()
                    Op.AND -> (a and b).ext()
                    Op.XOR -> (a xor b).ext()
                    else -> u0()
                }
            }
        }
    }
}

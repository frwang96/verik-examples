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
@file:Suppress("ClassName", "RedundantIf", "LiftReturnOrAssignment", "FunctionName")

package dut

import io.verik.core.*

class single_cycle(
    @In var A: Ubit<`8`>,
    @In var B: Ubit<`8`>,
    @In var clk: Boolean,
    @In var op: Ubit<`3`>,
    @In var reset_n: Boolean,
    @In var start: Boolean,
    @Out var done_aax: Boolean,
    @Out var result_aax: Ubit<`16`>
) : Module() {

    var done_aax_int: Boolean = nc()

    @Seq
    fun single_cycle_ops() {
        on(posedge(clk)) {
            if (!reset_n) {
                result_aax = u0()
            } else if (start) {
                when (op) {
                    u(0b001) -> result_aax = (A add B).ext()
                    u(0b010) -> result_aax = (A and B).ext()
                    u(0b011) -> result_aax = (A xor B).ext()
                }
            }
        }
    }

    @Seq
    fun set_done_int() {
        on(posedge(clk), negedge(reset_n)) {
            if (!reset_n) {
                done_aax_int = false
            } else {
                if (start && op != u(0b000)) {
                    done_aax_int = true
                } else {
                    done_aax_int = false
                }
            }
        }
    }

    @Com
    fun set_done() {
        done_aax = done_aax_int
    }
}

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

@file:Verik

import io.verik.core.*

class TinyAlu(
    @In var clk: Boolean,
    @In var rst_n: Boolean,
    @In var a: Ubit<`8`>,
    @In var b: Ubit<`8`>,
    @In var op: Op,
    @In var start: Boolean,
    @Out var done: Boolean,
    @Out var result: Ubit<`16`>
) : Module() {

    var done_aax: Boolean = nc()
    var done_mult: Boolean = nc()
    var result_aax: Ubit<`16`> = nc()
    var result_mult: Ubit<`16`> = nc()
    var start_single: Boolean = nc()
    var start_mult: Boolean = nc()

    @Make
    val single_cycle = SingleCycleAlu(
        clk = clk,
        rst_n = rst_n,
        a = a,
        b = b,
        op = op,
        start = start_single,
        done_aax = done_aax,
        result_aax = result_aax
    )

    @Make
    val three_cycle = ThreeCycleAlu(
        clk = clk,
        rst_n = rst_n,
        a = a,
        b = b,
        start = start_mult,
        done_mult = done_mult,
        result_mult = result_mult
    )

    @Com
    fun demux() {
        when (op) {
            Op.MUL -> {
                start_single = false
                start_mult = start
            }
            else -> {
                start_single = start
                start_mult = false
            }
        }
    }

    @Com
    fun mux() {
        when (op) {
            Op.MUL -> {
                done = done_mult
                result = result_mult
            }
            else -> {
                done = done_aax
                result = result_aax
            }
        }
    }
}

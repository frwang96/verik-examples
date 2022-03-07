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

import dut.Alu
import io.verik.core.*

@Entry
object Top : Module() {

    @Make
    val bfm = AluBfm()

    @Make
    val alu = Alu(
        clk = bfm.clk,
        reset_n = bfm.rst_n,
        a = bfm.a,
        b = bfm.b,
        op = bfm.op,
        start = bfm.start,
        done = bfm.done,
        result = bfm.result
    )

    @Run
    fun test() {
        val testbench = AluTestbench(bfm)
        testbench.execute()
    }
}

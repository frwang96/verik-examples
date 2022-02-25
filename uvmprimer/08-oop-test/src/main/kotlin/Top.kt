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

import dut.tinyalu
import io.verik.core.*

@Entry
object Top : Module() {

    @Make
    val bfm = TinyAluBfm()

    @Make
    val tiny_alu = tinyalu(
        A = bfm.a,
        B = bfm.b,
        clk = bfm.clk,
        op = bfm.op.value,
        reset_n = bfm.rst_n,
        start = bfm.start,
        done = bfm.done,
        result = bfm.result
    )

    @Run
    fun test() {
        val testbench = Testbench(bfm)
        testbench.execute()
    }
}

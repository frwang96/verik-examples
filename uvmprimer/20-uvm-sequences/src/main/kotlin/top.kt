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
@file:Suppress("ClassName")

import dut.tinyalu
import imported.uvm_pkg.run_test
import imported.uvm_pkg.uvm_config_db
import io.verik.core.*

@Entry
object top : Module() {

    @Make
    val bfm = tinyalu_bfm()

    @Make
    val DUT = tinyalu(
        A = bfm.A,
        B = bfm.B,
        op = bfm.op,
        clk = bfm.clk,
        reset_n = bfm.reset_n,
        start = bfm.start,
        done = bfm.done,
        result = bfm.result
    )

    @Run
    fun run() {
        uvm_config_db.set<tinyalu_bfm>(null, "*", "bfm", bfm)
        run_test()
    }
}

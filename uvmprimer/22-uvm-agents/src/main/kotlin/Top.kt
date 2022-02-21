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

import dut.TinyAlu
import imported.uvm_pkg.run_test
import imported.uvm_pkg.uvm_config_db
import io.verik.core.*

@Entry
object Top : Module() {

    @Make
    val class_bfm = TinyAluBfm()

    @Make
    val class_dut = TinyAlu(
        clk = class_bfm.clk,
        rst_n = class_bfm.rst_n,
        a = class_bfm.a,
        b = class_bfm.b,
        op = class_bfm.op,
        start = class_bfm.start,
        done = class_bfm.done,
        result = class_bfm.result
    )

    @Make
    val module_bfm = TinyAluBfm()

    @Make
    val module_dut = TinyAlu(
        clk = module_bfm.clk,
        rst_n = module_bfm.rst_n,
        a = module_bfm.a,
        b = module_bfm.b,
        op = module_bfm.op,
        start = module_bfm.start,
        done = module_bfm.done,
        result = module_bfm.result
    )

    @Make
    val module_dut_tester = TinyAluTesterModule(module_bfm)

    @Run
    fun run() {
        uvm_config_db.set(null, "*", "class_bfm", class_bfm)
        uvm_config_db.set(null, "*", "module_bfm", module_bfm)
        run_test()
    }
}

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

import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_config_db
import imported.uvm_pkg.uvm_get_port
import imported.uvm_pkg.uvm_phase
import io.verik.core.*

class Consumer(name: String, parent: uvm_component?) : uvm_component(name, parent) {

    @Inj
    val header = """
        import uvm_pkg::*;
        `include "uvm_macros.svh"
        `uvm_component_utils(${t<Consumer>()});
    """.trimIndent()

    lateinit var get_port: uvm_get_port<Int>
    lateinit var clk_bfm: ClkBfm
    var result = 0

    override fun build_phase(phase: uvm_phase?) {
        get_port = uvm_get_port("get_port", this)
        if (!uvm_config_db.get<ClkBfm>(null, "*", "clk_bfm", clk_bfm)) fatal("Failed to get BFM")
    }

    @Task
    override fun run_phase(phase: uvm_phase?) {
        forever {
            wait(posedge(clk_bfm.clk))
            if (get_port.try_get(result)) {
                println("${time()}ns received=$result")
            }
        }
    }
}

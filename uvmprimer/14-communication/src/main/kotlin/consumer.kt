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
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary")

import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_config_db
import imported.uvm_pkg.uvm_get_port
import imported.uvm_pkg.uvm_phase
import io.verik.core.*

class consumer : uvm_component {

    @Inj
    val header = "`uvm_component_utils(${t<consumer>()});"

    lateinit var get_port_h: uvm_get_port<Int>
    lateinit var clk_bfm_i: clk_bfm
    var shared = 0

    override fun build_phase(phase: uvm_phase?) {
        get_port_h = uvm_get_port("get_port_h", this)
        if (!uvm_config_db.get<clk_bfm>(null, "*", "clk_bfm_i", clk_bfm_i)) fatal("Failed to get BFM")
    }

    @Task
    override fun run_phase(phase: uvm_phase?) {
        forever {
            wait(posedge(clk_bfm_i.clk))
            if (get_port_h.try_get(shared)) {
                println("${time()}ns Received: $shared")
            }
        }
    }

    constructor(name: String, parent: uvm_component?) : super(name, parent)
}

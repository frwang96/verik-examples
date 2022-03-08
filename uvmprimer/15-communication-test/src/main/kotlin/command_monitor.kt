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
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "FunctionName", "unused")

import imported.uvm_pkg.uvm_analysis_port
import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_config_db
import imported.uvm_pkg.uvm_phase
import io.verik.core.*

class command_monitor : uvm_component {

    @Inj
    val header = "`uvm_component_utils(${t<command_monitor>()});"

    lateinit var ap: uvm_analysis_port<command_s>

    override fun build_phase(phase: uvm_phase?) {
        val bfm: tinyalu_bfm = nc()
        if (!uvm_config_db.get<tinyalu_bfm>(null, "*", "bfm", bfm)) fatal("Failed to get BFM")
        bfm.command_monitor_h = this
        ap = uvm_analysis_port("ap", this)
    }

    fun write_to_monitor(cmd: command_s) {
        println("COMMAND MONITOR: A:${cmd.A} B:${cmd.B} op:${cmd.op}")
        ap.write(cmd)
    }

    constructor(name: String, parent: uvm_component?) : super(name, parent)
}

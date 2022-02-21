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

class Driver(name: String, parent: uvm_component?) : uvm_component(name, parent) {

    @Inj
    val header = "`uvm_component_utils(${t<Driver>()});"

    lateinit var bfm: TinyAluBfm
    lateinit var command_port: uvm_get_port<CommandTransaction>

    override fun build_phase(phase: uvm_phase?) {
        val agent_config: TinyAluAgentConfig = nc()
        if (!uvm_config_db.get(this, "", "config", agent_config)) {
            inj("`uvm_fatal(${"DRIVER"}, ${"Failed to get config"})")
        }
        bfm = agent_config.bfm
        command_port = uvm_get_port("command_port", this)
    }

    @Task
    override fun run_phase(phase: uvm_phase?) {
        forever {
            var command: CommandTransaction = nc()
            command_port.get(command)
            bfm.sendOp(command.a, command.b, command.op)
        }
    }
}

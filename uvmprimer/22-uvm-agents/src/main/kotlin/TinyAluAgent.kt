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

import imported.uvm_pkg.*
import io.verik.core.*

class TinyAluAgent(name: String, parent: uvm_component?) : uvm_agent(name, parent) {

    @Inj
    val header = "`uvm_component_utils(${t<TinyAluAgent>()});"

    lateinit var agent_config: TinyAluAgentConfig

    lateinit var tester: Tester
    lateinit var driver: Driver
    lateinit var scoreboard: Scoreboard
    lateinit var command_monitor: CommandMonitor
    lateinit var result_monitor: ResultMonitor

    lateinit var command_fifo: uvm_tlm_fifo<CommandTransaction>
    lateinit var command_mon_ap: uvm_analysis_port<CommandTransaction>
    lateinit var result_ap: uvm_analysis_port<ResultTransaction>

    override fun build_phase(phase: uvm_phase?) {
        if (!uvm_config_db.get<TinyAluAgentConfig>(this, "", "config", agent_config)) {
            inj("`uvm_fatal(${"AGENT"}, ${"Failed to get config object"})")
        }
        is_active = agent_config.is_active
        if (get_is_active() == uvm_active_passive_enum.UVM_ACTIVE) {
            command_fifo = uvm_tlm_fifo("command", this)
            tester = inji("${t<Tester>()}::type_id::create(${"tester"}, $this);")
            driver = inji("${t<Driver>()}::type_id::create(${"driver"}, $this);")
        }

        command_monitor = inji("${t<CommandMonitor>()}::type_id::create(${"command_monitor"}, $this);")
        result_monitor = inji("${t<ResultMonitor>()}::type_id::create(${"result_monitor"}, $this);")
        scoreboard = inji("${t<Scoreboard>()}::type_id::create(${"scoreboard"}, $this);")

        command_mon_ap = uvm_analysis_port("command_mon_ap", this)
        result_ap = uvm_analysis_port("result_ap", this)
    }

    override fun connect_phase(phase: uvm_phase?) {
        if (get_is_active() == uvm_active_passive_enum.UVM_ACTIVE) {
            driver.command_port.connect(command_fifo.get_export)
            tester.command_port.connect(command_fifo.put_export)
        }

        command_monitor.ap.connect(command_mon_ap)
        result_monitor.ap.connect(result_ap)

        command_monitor.ap.connect(scoreboard.cmd_fifo.analysis_export)
        result_monitor.ap.connect(scoreboard.analysis_export)
    }
}

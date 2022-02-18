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

package tb

import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_env
import imported.uvm_pkg.uvm_phase
import imported.uvm_pkg.uvm_tlm_fifo
import io.verik.core.*

class Environment(name: String, parent: uvm_component?) : uvm_env(name, parent) {

    @Inj
    val header = """
        import uvm_pkg::*;
        `include "uvm_macros.svh"
        `uvm_component_utils(${t<Environment>()});
    """.trimIndent()

    lateinit var random_tester: RandomTester
    lateinit var driver: Driver
    lateinit var command_fifo: uvm_tlm_fifo<Command>

    lateinit var scoreboard: Scoreboard
    lateinit var command_monitor: CommandMonitor
    lateinit var result_monitor: ResultMonitor

    override fun build_phase(phase: uvm_phase?) {
        command_fifo = uvm_tlm_fifo("command_fifo", this)
        random_tester = inji("${t<RandomTester>()}::type_id::create(${"random_tester"}, $this);")
        driver = inji("${t<Driver>()}::type_id::create(${"driver"}, $this);")

        scoreboard = inji("${t<Scoreboard>()}::type_id::create(${"scoreboard"}, $this);")
        command_monitor = inji("${t<CommandMonitor>()}::type_id::create(${"command_monitor"}, $this);")
        result_monitor = inji("${t<ResultMonitor>()}::type_id::create(${"result_monitor"}, $this);")
    }

    override fun connect_phase(phase: uvm_phase?) {
        driver.command_port.connect(command_fifo.get_export)
        random_tester.command_port.connect(command_fifo.put_export)

        result_monitor.ap.connect(scoreboard.analysis_export)
        command_monitor.ap.connect(scoreboard.cmd_fifo.analysis_export)
    }
}

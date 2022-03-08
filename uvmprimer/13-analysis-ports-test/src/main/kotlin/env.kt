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

import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_env
import imported.uvm_pkg.uvm_phase
import io.verik.core.*

class env(name: String, parent: uvm_component?) : uvm_env(name, parent) {

    @Inj
    val header = "`uvm_component_utils(${t<env>()});"

    lateinit var random_tester_h: random_tester
    lateinit var coverage_h: coverage
    lateinit var scoreboard_h: scoreboard
    lateinit var command_monitor_h: command_monitor
    lateinit var result_monitor_h: result_monitor

    override fun build_phase(phase: uvm_phase?) {
        random_tester_h = inji("${t<random_tester>()}::type_id::create(${"random_tester_h"}, $this);")
        coverage_h = inji("${t<coverage>()}::type_id::create(${"coverage_h"}, $this);")
        scoreboard_h = inji("${t<scoreboard>()}::type_id::create(${"scoreboard_h"}, $this);")
        command_monitor_h = inji("${t<command_monitor>()}::type_id::create(${"command_monitor_h"}, $this);")
        result_monitor_h = inji("${t<result_monitor>()}::type_id::create(${"result_monitor_h"}, $this);")
    }

    override fun connect_phase(phase: uvm_phase?) {
        result_monitor_h.ap.connect(scoreboard_h.analysis_export)
        command_monitor_h.ap.connect(scoreboard_h.cmd_f.analysis_export)
        command_monitor_h.ap.connect(coverage_h.analysis_export)
    }
}

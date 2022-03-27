/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "unused")

import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_env
import imported.uvm_pkg.uvm_phase
import imported.uvm_pkg.uvm_sequencer
import io.verik.core.*

class env : uvm_env {

    @Inj
    val header = "`uvm_component_utils(${t<env>()});"

    lateinit var sequencer_h: sequencer
    lateinit var coverage_h: coverage
    lateinit var scoreboard_h: scoreboard
    lateinit var driver_h: driver
    lateinit var command_monitor_h: command_monitor
    lateinit var result_monitor_h: result_monitor

    constructor(name: String, parent: uvm_component?) : super(name, parent)

    override fun build_phase(phase: uvm_phase?) {
        sequencer_h = uvm_sequencer("sequencer_h", this)
        driver_h = inji("${t<driver>()}::type_id::create(${"driver_h"}, $this);")

        command_monitor_h = inji("${t<command_monitor>()}::type_id::create(${"command_monitor_h"}, $this);")
        result_monitor_h = inji("${t<result_monitor>()}::type_id::create(${"result_monitor_h"}, $this);")

        coverage_h = inji("${t<coverage>()}::type_id::create(${"coverage_h"}, $this);")
        scoreboard_h = inji("${t<scoreboard>()}::type_id::create(${"scoreboard_h"}, $this);")
    }

    override fun connect_phase(phase: uvm_phase?) {
        driver_h.seq_item_port!!.connect(sequencer_h.seq_item_export)

        command_monitor_h.ap.connect(coverage_h.analysis_export)
        command_monitor_h.ap.connect(scoreboard_h.cmd_f.analysis_export)
        result_monitor_h.ap.connect(scoreboard_h.analysis_export)
    }
}

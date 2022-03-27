/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary")

import imported.uvm_pkg.*
import imported.uvm_pkg.uvm_active_passive_enum.UVM_ACTIVE
import io.verik.core.*

class tinyalu_agent : uvm_agent {

    @Inj
    val header = "`uvm_component_utils(${t<tinyalu_agent>()});"

    lateinit var tinyalu_agent_config_h: tinyalu_agent_config

    lateinit var tester_h: tester
    lateinit var driver_h: driver
    lateinit var scoreboard_h: scoreboard
    lateinit var coverage_h: coverage
    lateinit var command_monitor_h: command_monitor
    lateinit var result_monitor_h: result_monitor

    lateinit var command_f: uvm_tlm_fifo<command_transaction>
    lateinit var cmd_mon_ap: uvm_analysis_port<command_transaction>
    lateinit var result_ap: uvm_analysis_port<result_transaction>

    constructor(name: String, parent: uvm_component?) : super(name, parent)

    override fun build_phase(phase: uvm_phase?) {
        if (!uvm_config_db.get<tinyalu_agent_config>(this, "", "config", tinyalu_agent_config_h)) {
            inj("`uvm_fatal(${"AGENT"}, ${"Failed to get config object"})")
        }
        is_active = tinyalu_agent_config_h.get_is_active()
        if (get_is_active() == UVM_ACTIVE) {
            command_f = uvm_tlm_fifo("command", this)
            tester_h = inji("${t<tester>()}::type_id::create(${"tester_h"}, $this);")
            driver_h = inji("${t<driver>()}::type_id::create(${"driver_h"}, $this);")
        }

        command_monitor_h = inji("${t<command_monitor>()}::type_id::create(${"command_monitor_h"}, $this);")
        result_monitor_h = inji("${t<result_monitor>()}::type_id::create(${"result_monitor_h"}, $this);")

        coverage_h = inji("${t<coverage>()}::type_id::create(${"coverage_h"}, $this);")
        scoreboard_h = inji("${t<scoreboard>()}::type_id::create(${"scoreboard_h"}, $this);")

        cmd_mon_ap = uvm_analysis_port("cmd_mon_ap", this)
        result_ap = uvm_analysis_port("result_ap", this)
    }

    override fun connect_phase(phase: uvm_phase?) {
        if (get_is_active() == UVM_ACTIVE) {
            driver_h.command_port.connect(command_f.get_export)
            tester_h.command_port.connect(command_f.put_export)
        }

        command_monitor_h.ap.connect(cmd_mon_ap)
        result_monitor_h.ap.connect(result_ap)

        command_monitor_h.ap.connect(scoreboard_h.cmd_f.analysis_export)
        command_monitor_h.ap.connect(coverage_h.analysis_export)
        result_monitor_h.ap.connect(scoreboard_h.analysis_export)
    }
}

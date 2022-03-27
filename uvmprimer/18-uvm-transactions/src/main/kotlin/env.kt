/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "unused")

import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_env
import imported.uvm_pkg.uvm_phase
import imported.uvm_pkg.uvm_tlm_fifo
import imported.uvm_pkg.uvm_verbosity.UVM_HIGH
import io.verik.core.*

class env : uvm_env {

    @Inj
    val header = "`uvm_component_utils(${t<env>()});"

    lateinit var tester_h: tester
    lateinit var coverage_h: coverage
    lateinit var scoreboard_h: scoreboard
    lateinit var driver_h: driver
    lateinit var command_monitor_h: command_monitor
    lateinit var result_monitor_h: result_monitor
    lateinit var command_f: uvm_tlm_fifo<command_transaction>

    constructor(name: String, parent: uvm_component?) : super(name, parent)

    override fun build_phase(phase: uvm_phase?) {
        command_f = uvm_tlm_fifo("command_fifo", this)
        tester_h = inji("${t<tester>()}::type_id::create(${"tester_h"}, $this);")
        driver_h = inji("${t<driver>()}::type_id::create(${"driver_h"}, $this);")
        coverage_h = inji("${t<coverage>()}::type_id::create(${"coverage_h"}, $this);")
        scoreboard_h = inji("${t<scoreboard>()}::type_id::create(${"scoreboard_h"}, $this);")
        command_monitor_h = inji("${t<command_monitor>()}::type_id::create(${"command_monitor_h"}, $this);")
        result_monitor_h = inji("${t<result_monitor>()}::type_id::create(${"result_monitor_h"}, $this);")
    }

    override fun connect_phase(phase: uvm_phase?) {
        driver_h.command_port.connect(command_f.get_export)
        tester_h.command_port.connect(command_f.put_export)
        command_f.put_ap!!.connect(coverage_h.analysis_export)
        command_monitor_h.ap.connect(scoreboard_h.cmd_f.analysis_export)
        result_monitor_h.ap.connect(scoreboard_h.analysis_export)
    }

    override fun end_of_elaboration_phase(phase: uvm_phase?) {
        scoreboard_h.set_report_verbosity_level_hier(inji("$UVM_HIGH"))
    }
}

/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "FunctionName", "unused")

import imported.uvm_pkg.uvm_analysis_port
import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_config_db
import imported.uvm_pkg.uvm_phase
import imported.uvm_pkg.uvm_verbosity.UVM_MEDIUM
import io.verik.core.*

class command_monitor : uvm_component {

    @Inj
    val header = "`uvm_component_utils(${t<command_monitor>()});"

    lateinit var ap: uvm_analysis_port<command_s>

    override fun build_phase(phase: uvm_phase?) {
        val bfm: tinyalu_bfm = nc()
        if (!uvm_config_db.get<tinyalu_bfm>(null, "*", "bfm", bfm)) {
            inj("`uvm_fatal(${"COMMAND MONITOR"}, ${"Failed to get BFM"})")
        }
        bfm.command_monitor_h = this
        ap = uvm_analysis_port("ap", this)
    }

    fun write_to_monitor(cmd: command_s) {
        inj("`uvm_info(${"COMMAND MONITOR"}, ${"A:${cmd.A} B:${cmd.B} op:${cmd.op}"}, $UVM_MEDIUM)")
        ap.write(cmd)
    }

    constructor(name: String, parent: uvm_component?) : super(name, parent)
}

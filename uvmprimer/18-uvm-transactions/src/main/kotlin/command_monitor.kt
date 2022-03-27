/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "FunctionName", "unused")

import dut.operation_t
import imported.uvm_pkg.uvm_analysis_port
import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_config_db
import imported.uvm_pkg.uvm_phase
import imported.uvm_pkg.uvm_verbosity.UVM_MEDIUM
import io.verik.core.*

class command_monitor : uvm_component {

    @Inj
    val header = "`uvm_component_utils(${t<command_monitor>()});"

    var bfm: tinyalu_bfm = nc()
    lateinit var ap: uvm_analysis_port<command_transaction>

    constructor(name: String, parent: uvm_component?) : super(name, parent)

    override fun build_phase(phase: uvm_phase?) {
        if (!uvm_config_db.get<tinyalu_bfm>(null, "*", "bfm", bfm)) {
            inj("`uvm_fatal(${"COMMAND MONITOR"}, ${"Failed to get BFM"})")
        }
        bfm.command_monitor_h = this
        ap = uvm_analysis_port("ap", this)
    }

    fun write_to_monitor(A: Ubit<`8`>, B: Ubit<`8`>, op: operation_t) {
        inj("`uvm_info(${"COMMAND MONITOR"}, ${"A:$A B:$B op:$op"}, $UVM_MEDIUM)")
        val cmd = command_transaction("cmd")
        cmd.A = A
        cmd.B = B
        cmd.op = op
        ap.write(cmd)
    }
}

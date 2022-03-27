/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress(
    "ClassName",
    "ConvertSecondaryConstructorToPrimary",
    "unused",
    "ConvertToStringTemplate",
    "NON_EXHAUSTIVE_WHEN"
)

import dut.operation_t.*
import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_phase
import imported.uvm_pkg.uvm_subscriber
import imported.uvm_pkg.uvm_tlm_analysis_fifo
import imported.uvm_pkg.uvm_verbosity.UVM_MEDIUM
import io.verik.core.*

class scoreboard : uvm_subscriber<Ubit<`16`>> {

    @Inj
    val header: String = "`uvm_component_utils(${t<scoreboard>()});"

    lateinit var cmd_f: uvm_tlm_analysis_fifo<command_s>

    override fun build_phase(phase: uvm_phase?) {
        cmd_f = uvm_tlm_analysis_fifo("cmd_f", this)
    }

    override fun write(t: Ubit<`16`>) {
        var predicted_result: Ubit<`16`> = u0()
        val data_str: String
        var cmd: command_s = nc()
        cmd.A = u0()
        cmd.B = u0()
        cmd.op = no_op
        do {
            if (!cmd_f.try_get(cmd)) fatal("Missing command in checker")
        } while (cmd.op == no_op || cmd.op == rst_op)

        when (cmd.op) {
            add_op -> predicted_result = (cmd.A add cmd.B).ext()
            and_op -> predicted_result = (cmd.A and cmd.B).ext()
            xor_op -> predicted_result = (cmd.A xor cmd.B).ext()
            mul_op -> predicted_result = cmd.A mul cmd.B
        }

        data_str = "${cmd.A} ${cmd.op} ${cmd.B} = $t ($predicted_result predicted)"
        if (predicted_result != t) {
            inj("`uvm_error(${"SCOREBOARD"}, ${"FAIL: " + data_str})")
        } else {
            inj("`uvm_info(${"SCOREBOARD"}, ${"PASS: " + data_str}, $UVM_MEDIUM)")
        }
    }

    constructor(name: String, parent: uvm_component?) : super(name, parent)
}

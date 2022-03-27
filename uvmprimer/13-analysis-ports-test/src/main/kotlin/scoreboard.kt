/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "NON_EXHAUSTIVE_WHEN", "unused")

import dut.operation_t.*
import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_phase
import imported.uvm_pkg.uvm_subscriber
import imported.uvm_pkg.uvm_tlm_analysis_fifo
import io.verik.core.*

class scoreboard : uvm_subscriber<Ubit<`16`>> {

    @Inj
    val header = "`uvm_component_utils(${t<scoreboard>()});"

    lateinit var cmd_f: uvm_tlm_analysis_fifo<command_s>

    override fun build_phase(phase: uvm_phase?) {
        cmd_f = uvm_tlm_analysis_fifo("cmd_f", this)
    }

    override fun write(t: Ubit<`16`>) {
        var predicted_result: Ubit<`16`> = u0()
        var cmd: command_s = nc()
        cmd.op = no_op
        do {
            if (!cmd_f.try_get(cmd)) fatal("No command in checker")
        } while (cmd.op == no_op || cmd.op == rst_op)

        when (cmd.op) {
            add_op -> predicted_result = (cmd.A add cmd.B).ext()
            and_op -> predicted_result = (cmd.A and cmd.B).ext()
            xor_op -> predicted_result = (cmd.A xor cmd.B).ext()
            mul_op -> predicted_result = cmd.A mul cmd.B
        }
        if (predicted_result != t) {
            error("FAILED: A: ${cmd.A}  b:${cmd.B}  op:${cmd.op}  result:$predicted_result")
        }
    }

    constructor(name: String, parent: uvm_component?) : super(name, parent)
}

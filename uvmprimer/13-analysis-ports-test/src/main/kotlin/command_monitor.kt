/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ConvertSecondaryConstructorToPrimary", "ClassName", "LiftReturnOrAssignment", "FunctionName", "unused")

import dut.operation_t
import dut.operation_t.*
import imported.uvm_pkg.uvm_analysis_port
import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_config_db
import imported.uvm_pkg.uvm_phase
import io.verik.core.*

class command_monitor : uvm_component {

    @Inj
    val header = "`uvm_component_utils(${t<command_monitor>()});"

    lateinit var ap: uvm_analysis_port<command_s>

    override fun build_phase(phase: uvm_phase?) {
        val bfm: tinyalu_bfm = nc()
        if (!uvm_config_db.get<tinyalu_bfm>(null, "*", "bfm", bfm)) fatal("Failed to get BFM")
        bfm.command_monitor_h = this
        ap = uvm_analysis_port("ap", this)
    }

    fun write_to_monitor(A: Ubit<`8`>, B: Ubit<`8`>, op: Ubit<`3`>) {
        val cmd: command_s = nc()
        cmd.A = A
        cmd.B = B
        cmd.op = op2enum(op)
        println("COMMAND MONITOR: A:$A b:$B op:${cmd.op}")
        ap.write(cmd)
    }

    fun op2enum(op: Ubit<`3`>): operation_t {
        when (op) {
            u(0b000) -> return no_op
            u(0b001) -> return add_op
            u(0b010) -> return and_op
            u(0b011) -> return xor_op
            u(0b100) -> return mul_op
            u(0b111) -> return rst_op
            else -> fatal("Illegal operation on op bus: $op")
        }
    }

    constructor(name: String, parent: uvm_component?) : super(name, parent)
}

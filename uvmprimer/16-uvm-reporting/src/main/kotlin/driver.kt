/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ConvertSecondaryConstructorToPrimary", "unused", "ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE", "ClassName")

import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_config_db
import imported.uvm_pkg.uvm_get_port
import imported.uvm_pkg.uvm_phase
import io.verik.core.*

class driver : uvm_component {

    @Inj
    val header = "`uvm_component_utils(${t<driver>()});"

    lateinit var bfm: tinyalu_bfm
    lateinit var command_port: uvm_get_port<command_s>

    override fun build_phase(phase: uvm_phase?) {
        if (!uvm_config_db.get<tinyalu_bfm>(null, "*", "bfm", bfm)) {
            inj("`uvm_fatal(${"COMMAND MONITOR"}, ${"Failed to get BFM"})")
        }
        command_port = uvm_get_port("command_port", this)
    }

    @Task
    override fun run_phase(phase: uvm_phase?) {
        var command: command_s = nc()
        var result: Ubit<`16`> = nc()
        forever {
            command_port.get(command)
            result = bfm.send_op(command.A, command.B, command.op)
        }
    }

    constructor(name: String, parent: uvm_component?) : super(name, parent)
}

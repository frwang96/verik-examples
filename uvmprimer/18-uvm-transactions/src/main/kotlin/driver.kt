/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE", "unused")

import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_config_db
import imported.uvm_pkg.uvm_get_port
import imported.uvm_pkg.uvm_phase
import io.verik.core.*

class driver : uvm_component {

    @Inj
    val header = "`uvm_component_utils(${t<driver>()});"

    lateinit var bfm: tinyalu_bfm
    lateinit var command_port: uvm_get_port<command_transaction>

    constructor(name: String, parent: uvm_component?) : super(name, parent)

    override fun build_phase(phase: uvm_phase?) {
        if (!uvm_config_db.get<tinyalu_bfm>(null, "*", "bfm", bfm)) {
            inj("`uvm_fatal(${"DRIVER"}, ${"Failed to get BFM"})")
        }
        command_port = uvm_get_port("command_port", this)
    }

    @Task
    override fun run_phase(phase: uvm_phase?) {
        var result: Ubit<`16`>
        var command: command_transaction = nc()
        forever {
            command_port.get(command)
            result = bfm.send_op(command.A, command.B, command.op)
        }
    }
}

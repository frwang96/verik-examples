/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ConvertSecondaryConstructorToPrimary", "JoinDeclarationAndAssignment", "ClassName", "unused")

import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_config_db
import imported.uvm_pkg.uvm_driver
import imported.uvm_pkg.uvm_phase
import io.verik.core.*

class driver : uvm_driver<sequence_item, sequence_item> {

    @Inj
    val header = "`uvm_component_utils(${t<driver>()});"

    lateinit var bfm: tinyalu_bfm

    override fun build_phase(phase: uvm_phase?) {
        if (!uvm_config_db.get<tinyalu_bfm>(null, "*", "bfm", bfm)) {
            inj("`uvm_fatal(${"COMMAND MONITOR"}, ${"Failed to get BFM"})")
        }
    }

    @Task
    override fun run_phase(phase: uvm_phase?) {
        var cmd: sequence_item = nc()
        forever {
            val result: Ubit<`16`>
            seq_item_port!!.get_next_item(cmd)
            result = bfm.send_op(cmd.A, cmd.B, cmd.op)
            cmd.result = result
            seq_item_port!!.item_done()
        }
    }

    constructor(name: String, parent: uvm_component?) : super(name, parent)
}

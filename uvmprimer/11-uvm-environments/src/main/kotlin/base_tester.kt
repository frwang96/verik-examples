/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress(
    "ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE",
    "ClassName",
    "ConvertSecondaryConstructorToPrimary",
    "FunctionName"
)

import dut.operation_t
import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_config_db
import imported.uvm_pkg.uvm_phase
import io.verik.core.*

abstract class base_tester : uvm_component {

    @Inj
    private val header = "`uvm_component_utils(${t<base_tester>()});"

    lateinit var bfm: tinyalu_bfm

    override fun build_phase(phase: uvm_phase?) {
        if (!uvm_config_db.get<tinyalu_bfm>(null, "*", "bfm", bfm)) fatal("Failed to get BFM")
    }

    abstract fun get_op(): operation_t

    abstract fun get_data(): Ubit<`8`>

    @Task
    override fun run_phase(phase: uvm_phase?) {
        var iA: Ubit<`8`>
        var iB: Ubit<`8`>
        var op_set: operation_t
        var result: Ubit<`16`>
        phase!!.raise_objection(this)
        bfm.reset_alu()
        repeat(100) {
            op_set = get_op()
            iA = get_data()
            iB = get_data()
            result = bfm.send_op(iA, iB, op_set)
        }
        delay(100)
        phase.drop_objection(this)
    }

    constructor(name: String, parent: uvm_component?) : super(name, parent)
}

/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress(
    "ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE",
    "ConvertSecondaryConstructorToPrimary",
    "FunctionName",
    "MoveVariableDeclarationIntoWhen",
    "ClassName",
    "LiftReturnOrAssignment"
)

import dut.operation_t
import dut.operation_t.*
import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_config_db
import imported.uvm_pkg.uvm_phase
import io.verik.core.*

open class random_tester : uvm_component {

    @Inj
    private val header = "`uvm_component_utils(${t<random_tester>()});"

    lateinit var bfm: tinyalu_bfm

    constructor(name: String, parent: uvm_component?) : super(name, parent)

    open fun get_op(): operation_t {
        val op_choice = randomUbit<`3`>()
        when (op_choice) {
            u(0b000) -> return no_op
            u(0b001) -> return add_op
            u(0b010) -> return and_op
            u(0b011) -> return xor_op
            u(0b100) -> return mul_op
            u(0b101) -> return no_op
            u(0b110) -> return rst_op
            u(0b111) -> return rst_op
        }
        return rst_op
    }

    fun get_data(): Ubit<`8`> {
        val zero_ones = randomUbit<`2`>()
        when (zero_ones) {
            u(0b00) -> return u(0x00)
            u(0b11) -> return u(0xff)
            else -> return randomUbit<`8`>()
        }
    }

    override fun build_phase(phase: uvm_phase?) {
        if (!uvm_config_db.get<tinyalu_bfm>(null, "*", "bfm", bfm)) fatal("Failed to get BFM")
    }

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
}

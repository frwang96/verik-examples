/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "FunctionName")

import dut.operation_t
import dut.operation_t.rst_op
import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_phase
import imported.uvm_pkg.uvm_put_port
import io.verik.core.*

abstract class base_tester : uvm_component {

    @Inj
    private val header = "`uvm_component_utils(${t<base_tester>()});"

    lateinit var command_port: uvm_put_port<command_s>

    override fun build_phase(phase: uvm_phase?) {
        command_port = uvm_put_port("command_port", this)
    }

    abstract fun get_op(): operation_t

    abstract fun get_data(): Ubit<`8`>

    @Task
    override fun run_phase(phase: uvm_phase?) {
        var command: command_s = nc()
        phase!!.raise_objection(this)
        command.op = rst_op
        command_port.put(command)
        repeat(100) {
            command.op = get_op()
            command.A = get_data()
            command.B = get_data()
            command_port.put(command)
        }
        delay(100)
        phase.drop_objection(this)
    }

    constructor(name: String, parent: uvm_component?) : super(name, parent)
}

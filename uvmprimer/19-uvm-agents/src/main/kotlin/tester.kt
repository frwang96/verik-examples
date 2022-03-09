/*
 * Copyright (c) 2022 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Verik
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "unused")

import dut.operation_t
import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_phase
import imported.uvm_pkg.uvm_put_port
import io.verik.core.*

class tester : uvm_component {

    @Inj
    private val header = "`uvm_component_utils(${t<tester>()});"

    lateinit var command_port: uvm_put_port<command_transaction>

    constructor(name: String, parent: uvm_component?) : super(name, parent)

    override fun build_phase(phase: uvm_phase?) {
        command_port = uvm_put_port("command_port", this)
    }

    @Task
    override fun run_phase(phase: uvm_phase?) {
        var command: command_transaction
        phase!!.raise_objection(this)
        command = command_transaction("command")
        command.op = operation_t.rst_op
        command_port.put(command)

        repeat(100) {
            command = inji("${t<command_transaction>()}::type_id::create(${"command"})")
            command.randomize()
            command_port.put(command)
        }

        command = command_transaction("command")
        command.op = operation_t.mul_op
        command.A = u(0xff)
        command.B = u(0xff)
        command_port.put(command)

        delay(100)
        phase.drop_objection(this)
    }
}
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

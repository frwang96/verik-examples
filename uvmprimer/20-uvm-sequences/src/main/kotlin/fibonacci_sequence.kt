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
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "JoinDeclarationAndAssignment")

import dut.operation_t.add_op
import dut.operation_t.rst_op
import imported.uvm_pkg.uvm_sequence
import imported.uvm_pkg.uvm_verbosity.UVM_MEDIUM
import io.verik.core.*

open class fibonacci_sequence : uvm_sequence<sequence_item, sequence_item> {

    @Inj
    private val header = "`uvm_object_utils(${t<fibonacci_sequence>()});"

    constructor(name: String = "fibonacci") : super(name)

    @Task
    override fun body() {
        var n_minus_2 = u(0x00)
        var n_minus_1 = u(0x01)
        val command: sequence_item

        command = inji("${t<sequence_item>()}::type_id::create(${"command"})")
        start_item(command)
        command.op = rst_op
        finish_item(command)

        inj("`uvm_info(${"FIBONACCI"}, ${"Fib(1) == 0"}, $UVM_MEDIUM)")
        inj("`uvm_info(${"FIBONACCI"}, ${"Fib(2) == 1"}, $UVM_MEDIUM)")
        for (ff in 3 .. 14) {
            start_item(command)
            command.A = n_minus_2
            command.B = n_minus_1
            command.op = add_op
            finish_item(command)
            n_minus_2 = n_minus_1
            n_minus_1 = command.result.tru()
            inj("`uvm_info(${"FIBONACCI"}, ${"Fib($ff) = ${n_minus_1.toDecString()}"}, $UVM_MEDIUM)")
        }
    }
}

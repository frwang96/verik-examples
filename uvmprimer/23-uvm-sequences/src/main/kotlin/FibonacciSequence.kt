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

import dut.operation_t
import imported.uvm_pkg.uvm_sequence
import imported.uvm_pkg.uvm_verbosity.UVM_MEDIUM
import io.verik.core.*

open class FibonacciSequence(name: String = "fibonacci") : uvm_sequence<SequenceItem, SequenceItem>(name) {

    @Inj
    private val header = "`uvm_object_utils(${t<FibonacciSequence>()});"

    @Task
    override fun body() {
        val command: SequenceItem = inji("${t<SequenceItem>()}::type_id::create(${"command"})")
        start_item(command)
        command.op = operation_t.rst_op
        finish_item(command)

        var n_minus_2 = u(0x00)
        var n_minus_1 = u(0x01)
        inj("`uvm_info(${"FIBONACCI"}, ${"Fib(1) == 0"}, $UVM_MEDIUM)")
        inj("`uvm_info(${"FIBONACCI"}, ${"Fib(2) == 1"}, $UVM_MEDIUM)")
        for (index in 3 until 15) {
            start_item(command)
            command.a = n_minus_2
            command.b = n_minus_1
            command.op = operation_t.add_op
            finish_item(command)
            n_minus_2 = n_minus_1
            n_minus_1 = command.result.tru()
            inj("`uvm_info(${"FIBONACCI"}, ${"Fib($index) = ${n_minus_1.toDecString()}"}, $UVM_MEDIUM)")
        }
    }
}

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
import io.verik.core.*

open class MaxMultSequence(name: String = "") : uvm_sequence<SequenceItem, SequenceItem>(name) {

    @Inj
    private val header = "`uvm_object_utils(${t<MaxMultSequence>()});"

    @Task
    override fun body() {
        val command: SequenceItem = inji("${t<SequenceItem>()}::type_id::create(${"command"})")
        start_item(command)
        command.op = operation_t.mul_op
        command.a = u(0xff)
        command.b = u(0xff)
        finish_item(command)
    }
}

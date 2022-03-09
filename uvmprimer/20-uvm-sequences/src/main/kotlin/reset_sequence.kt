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

import dut.operation_t.rst_op
import imported.uvm_pkg.uvm_sequence
import io.verik.core.*

open class reset_sequence : uvm_sequence<sequence_item, sequence_item> {

    @Inj
    private val header = "`uvm_object_utils(${t<reset_sequence>()});"

    lateinit var command: sequence_item

    constructor(name: String = "") : super(name)

    @Task
    override fun body() {
        command = inji("${t<sequence_item>()}::type_id::create(${"command"})")
        start_item(command)
        command.op = rst_op
        finish_item(command)
    }
}

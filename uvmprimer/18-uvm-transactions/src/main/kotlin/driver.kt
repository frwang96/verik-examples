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
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE", "unused")

import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_config_db
import imported.uvm_pkg.uvm_get_port
import imported.uvm_pkg.uvm_phase
import io.verik.core.*

class driver : uvm_component {

    @Inj
    val header = "`uvm_component_utils(${t<driver>()});"

    lateinit var bfm: tinyalu_bfm
    lateinit var command_port: uvm_get_port<command_transaction>

    constructor(name: String, parent: uvm_component?) : super(name, parent)

    override fun build_phase(phase: uvm_phase?) {
        if (!uvm_config_db.get<tinyalu_bfm>(null, "*", "bfm", bfm)) {
            inj("`uvm_fatal(${"DRIVER"}, ${"Failed to get BFM"})")
        }
        command_port = uvm_get_port("command_port", this)
    }

    @Task
    override fun run_phase(phase: uvm_phase?) {
        var result: Ubit<`16`>
        var command: command_transaction = nc()
        forever {
            command_port.get(command)
            result = bfm.send_op(command.A, command.B, command.op)
        }
    }
}

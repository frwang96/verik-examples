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

import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_config_db
import imported.uvm_pkg.uvm_driver
import imported.uvm_pkg.uvm_phase
import io.verik.core.*

class Driver(name: String, parent: uvm_component?) : uvm_driver<SequenceItem, SequenceItem>(name, parent) {

    @Inj
    val header = "`uvm_component_utils(${t<Driver>()});"

    lateinit var bfm: TinyAluBfm

    override fun build_phase(phase: uvm_phase?) {
        if (!uvm_config_db.get<TinyAluBfm>(null, "*", "bfm", bfm)) {
            inj("`uvm_fatal(${"COMMAND MONITOR"}, ${"Failed to get BFM"})")
        }
    }

    @Task
    override fun run_phase(phase: uvm_phase?) {
        forever {
            var command: SequenceItem = nc()
            seq_item_port!!.get_next_item(command)
            command.result = bfm.sendOp(command.a, command.b, command.op)
            seq_item_port!!.item_done()
        }
    }
}
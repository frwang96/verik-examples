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
@file:Suppress(
    "FunctionName",
    "ConvertSecondaryConstructorToPrimary",
    "ClassName",
    "ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE"
)

import dut.operation_t
import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_config_db
import imported.uvm_pkg.uvm_phase
import io.verik.core.*

abstract class base_tester : uvm_component {

    @Inj
    private val header = "`uvm_component_utils(${t<base_tester>()});"

    lateinit var bfm: tinyalu_bfm

    override fun build_phase(phase: uvm_phase?) {
        if (!uvm_config_db.get<tinyalu_bfm>(null, "*", "bfm", bfm)) fatal("Failed to get BFM")
    }

    abstract fun get_op(): operation_t

    abstract fun get_data(): Ubit<`8`>

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

    constructor(name: String, parent: uvm_component?) : super(name, parent)
}

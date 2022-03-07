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
@file:Suppress("NON_EXHAUSTIVE_WHEN")

import dut.operation_t
import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_config_db
import imported.uvm_pkg.uvm_phase
import io.verik.core.*

class scoreboard : uvm_component {

    @Inj
    val header: String = "`uvm_component_utils(${t<scoreboard>()});"

    lateinit var bfm: tinyalu_bfm

    constructor(name: String, parent: uvm_component?) : super(name, parent)

    override fun build_phase(phase: uvm_phase?) {
        if (!uvm_config_db.get<tinyalu_bfm>(null, "*", "bfm", bfm)) fatal("Failed to get BFM")
    }

    @Task
    override fun run_phase(phase: uvm_phase?) {
        var predicted_result: Ubit<`16`> = u0()
        forever {
            wait(posedge(bfm.done))
            delay(1)
            when (bfm.op_set) {
                operation_t.add_op -> predicted_result = (bfm.A add bfm.B).ext()
                operation_t.and_op -> predicted_result = (bfm.A and bfm.B).ext()
                operation_t.xor_op -> predicted_result = (bfm.A xor bfm.B).ext()
                operation_t.mul_op -> predicted_result = bfm.A mul bfm.B
            }
            if (bfm.op_set != operation_t.no_op && bfm.op_set != operation_t.rst_op) {
                if (predicted_result != bfm.result) {
                    error("FAILED: A: ${bfm.A}  b:${bfm.B}  op:${bfm.op_set}  result:${bfm.result}")
                }
            }
        }
    }
}

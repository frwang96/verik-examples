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
import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_config_db
import imported.uvm_pkg.uvm_phase
import io.verik.core.*

class Scoreboard(name: String, parent: uvm_component?) : uvm_component(name, parent) {

    @Inj
    val header = "`uvm_component_utils(${t<Scoreboard>()});"

    lateinit var bfm: TinyAluBfm

    override fun build_phase(phase: uvm_phase?) {
        if (!uvm_config_db.get<TinyAluBfm>(null, "*", "bfm", bfm)) fatal("Failed to get BFM")
    }

    @Task
    override fun run_phase(phase: uvm_phase?) {
        forever {
            wait(posedge(bfm.done))
            delay(1)
            val predicted_result = when (bfm.op) {
                operation_t.add_op -> (bfm.a add bfm.b).ext()
                operation_t.and_op -> (bfm.a and bfm.b).ext()
                operation_t.xor_op -> (bfm.a xor bfm.b).ext()
                operation_t.mul_op -> bfm.a mul bfm.b
                else -> u0()
            }
            if (bfm.op != operation_t.no_op && bfm.op != operation_t.rst_op) {
                assert(predicted_result == bfm.result) {
                    error("FAILED: a=${bfm.a} b=${bfm.b} op=${bfm.op} result=${bfm.result}")
                }
            }
        }
    }
}

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
import imported.uvm_pkg.uvm_phase
import imported.uvm_pkg.uvm_subscriber
import imported.uvm_pkg.uvm_tlm_analysis_fifo
import io.verik.core.*

class Scoreboard(name: String, parent: uvm_component?) : uvm_subscriber<Ubit<`16`>>(name, parent) {

    @Inj
    val header = "`uvm_component_utils(${t<Scoreboard>()});"

    lateinit var cmd_fifo: uvm_tlm_analysis_fifo<Command>

    override fun build_phase(phase: uvm_phase?) {
        cmd_fifo = uvm_tlm_analysis_fifo("cmd_fifo", this)
    }

    override fun write(t: Ubit<`16`>) {
        var cmd: Command = nc()
        do {
            if (!cmd_fifo.try_get(cmd)) fatal("No command in checker")
        } while (cmd.op == operation_t.no_op || cmd.op == operation_t.rst_op)

        val predicted_result = when (cmd.op) {
            operation_t.add_op -> (cmd.a add cmd.b).ext()
            operation_t.and_op -> (cmd.a and cmd.b).ext()
            operation_t.xor_op -> (cmd.a xor cmd.b).ext()
            operation_t.mul_op -> cmd.a mul cmd.b
            else -> u0()
        }
        assert(predicted_result == t) {
            error("FAILED: a=${cmd.a} b=${cmd.b} op=${cmd.op} actual=$t expected=$predicted_result")
        }
    }
}

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

import dut.Op
import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_phase
import imported.uvm_pkg.uvm_subscriber
import imported.uvm_pkg.uvm_tlm_analysis_fifo
import imported.uvm_pkg.uvm_verbosity.UVM_MEDIUM
import io.verik.core.*

class Scoreboard(name: String, parent: uvm_component?) : uvm_subscriber<ResultTransaction>(name, parent) {

    @Inj
    val header = "`uvm_component_utils(${t<Scoreboard>()});"

    lateinit var cmd_fifo: uvm_tlm_analysis_fifo<SequenceItem>

    override fun build_phase(phase: uvm_phase?) {
        cmd_fifo = uvm_tlm_analysis_fifo("cmd_fifo", this)
    }

    override fun write(t: ResultTransaction) {
        var command: SequenceItem = nc()
        do {
            if (!cmd_fifo.try_get(command)) inj("`uvm_fatal(${"SCOREBOARD"}, ${"Mising command in checker"})")
        } while (command.op == Op.NOP || command.op == Op.RST)

        val predicted = predictResult(command)
        val result_string = "$command ACTUAL $t EXPECTED $predicted"
        if (!predicted.compare(t)) {
            inj("`uvm_error(${"SCOREBOARD"}, ${"FAIL: $result_string"})")
        } else {
            inj("`uvm_info(${"SCOREBOARD"}, ${"PASS: $result_string"}, $UVM_MEDIUM)")
        }
    }

    private fun predictResult(command: SequenceItem): ResultTransaction {
        val predicted = ResultTransaction("predicted")
        predicted.result = when (command.op) {
            Op.ADD -> (command.a add command.b).ext()
            Op.AND -> (command.a and command.b).ext()
            Op.XOR -> (command.a xor command.b).ext()
            Op.MUL -> command.a mul command.b
            else -> u0()
        }
        return predicted
    }
}

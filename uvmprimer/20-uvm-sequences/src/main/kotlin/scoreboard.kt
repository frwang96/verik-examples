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
    "ClassName",
    "ConvertSecondaryConstructorToPrimary",
    "unused",
    "ConvertToStringTemplate",
    "NON_EXHAUSTIVE_WHEN",
    "JoinDeclarationAndAssignment",
    "FunctionName"
)

import dut.operation_t.*
import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_phase
import imported.uvm_pkg.uvm_subscriber
import imported.uvm_pkg.uvm_tlm_analysis_fifo
import imported.uvm_pkg.uvm_verbosity.UVM_MEDIUM
import io.verik.core.*

class scoreboard : uvm_subscriber<result_transaction> {

    @Inj
    val header = "`uvm_component_utils(${t<scoreboard>()});"

    lateinit var cmd_f: uvm_tlm_analysis_fifo<sequence_item>

    constructor(name: String, parent: uvm_component?) : super(name, parent)

    override fun build_phase(phase: uvm_phase?) {
        cmd_f = uvm_tlm_analysis_fifo("cmd_f", this)
    }

    fun predict_result(cmd: sequence_item): result_transaction {
        val predicted: result_transaction
        predicted = result_transaction("predicted")
        when (cmd.op) {
            add_op -> predicted.result = (cmd.A add cmd.B).ext()
            and_op -> predicted.result = (cmd.A and cmd.B).ext()
            xor_op -> predicted.result = (cmd.A xor cmd.B).ext()
            mul_op -> predicted.result = cmd.A mul cmd.B
        }
        return predicted
    }

    override fun write(t: result_transaction) {
        val data_str: String
        var cmd: sequence_item = nc()
        var predicted: result_transaction
        do {
            if (!cmd_f.try_get(cmd)) fatal("Missing command in checker")
        } while (cmd.op == no_op || cmd.op == rst_op)
        predicted = predict_result(cmd)

        data_str = "${cmd.convert2string()} ==> Actual ${t.convert2string()} ${cmd.B} / Predicted ${predicted.convert2string()}"
        if (!predicted.compare(t)) {
            inj("`uvm_error(${"SCOREBOARD"}, ${"FAIL: " + data_str})")
        } else {
            inj("`uvm_info(${"SCOREBOARD"}, ${"PASS: " + data_str}, $UVM_MEDIUM)")
        }
    }
}
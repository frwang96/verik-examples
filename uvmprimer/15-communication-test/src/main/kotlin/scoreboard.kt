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
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "NON_EXHAUSTIVE_WHEN", "unused")

import dut.operation_t.*
import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_phase
import imported.uvm_pkg.uvm_subscriber
import imported.uvm_pkg.uvm_tlm_analysis_fifo
import io.verik.core.*

class scoreboard : uvm_subscriber<Ubit<`16`>> {

    @Inj
    val header = "`uvm_component_utils(${t<scoreboard>()});"

    lateinit var cmd_f: uvm_tlm_analysis_fifo<command_s>

    constructor(name: String, parent: uvm_component?) : super(name, parent)

    override fun build_phase(phase: uvm_phase?) {
        cmd_f = uvm_tlm_analysis_fifo("cmd_f", this)
    }

    override fun write(t: Ubit<`16`>) {
        var predicted_result: Ubit<`16`> = u0()
        var cmd: command_s = nc()
        cmd.A = u0()
        cmd.B = u0()
        cmd.op = no_op
        do {
            if (!cmd_f.try_get(cmd)) fatal("Missing command in checker")
        } while (cmd.op == no_op || cmd.op == rst_op)

        when (cmd.op) {
            add_op -> predicted_result = (cmd.A add cmd.B).ext()
            and_op -> predicted_result = (cmd.A and cmd.B).ext()
            xor_op -> predicted_result = (cmd.A xor cmd.B).ext()
            mul_op -> predicted_result = cmd.A mul cmd.B
        }
        if (predicted_result != t) {
            error("FAILED: A: ${cmd.A}  b:${cmd.B}  op:${cmd.op}  result:$predicted_result")
        }
    }
}

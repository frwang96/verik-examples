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

import imported.uvm_pkg.uvm_analysis_port
import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_config_db
import imported.uvm_pkg.uvm_phase
import imported.uvm_pkg.uvm_verbosity.UVM_MEDIUM
import io.verik.core.*

class ResultMonitor(name: String, parent: uvm_component?) : uvm_component(name, parent) {

    @Inj
    val header = "`uvm_component_utils(${t<ResultMonitor>()});"

    lateinit var ap: uvm_analysis_port<ResultTransaction>

    fun write(result: Ubit<`16`>) {
        inj("`uvm_info(${"RESULT MONITOR"}, ${"result=$result"}, $UVM_MEDIUM)")
        val resultTransaction = ResultTransaction("result")
        resultTransaction.result = result
        ap.write(resultTransaction)
    }

    override fun build_phase(phase: uvm_phase?) {
        val agent_config: TinyAluAgentConfig = nc()
        if (!uvm_config_db.get(this, "", "config", agent_config)) {
            inj("`uvm_fatal(${"RESULT MONITOR"}, ${"Failed to get config"})")
        }
        agent_config.bfm.result_monitor = this
        ap = uvm_analysis_port("ap", this)
    }
}

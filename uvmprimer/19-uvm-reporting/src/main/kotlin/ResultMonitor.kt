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

package tb

import imported.uvm_pkg.*
import io.verik.core.*

class ResultMonitor(name: String, parent: uvm_component?) : uvm_component(name, parent) {

    @Inj
    val header = """
        import uvm_pkg::*;
        `include "uvm_macros.svh"
        `uvm_component_utils(${t<ResultMonitor>()});
    """.trimIndent()

    lateinit var ap: uvm_analysis_port<Ubit<`16`>>

    fun write(result: Ubit<`16`>) {
        inj("`uvm_info(${"RESULT MONITOR"}, ${"result=$result"}, ${uvm_verbosity.UVM_MEDIUM})")
        ap.write(result)
    }

    override fun build_phase(phase: uvm_phase?) {
        val bfm: TinyAluBfm = nc()
        if (!uvm_config_db.get<TinyAluBfm>(null, "*", "bfm", bfm)) {
            inj("`uvm_fatal(${"COMMAND MONITOR"}, ${"Failed to get BFM"})")
        }
        bfm.result_monitor = this
        // TODO fix type resolution
        ap = uvm_analysis_port<Ubit<`16`>>("ap", this)
    }
}

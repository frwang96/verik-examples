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
import imported.uvm_pkg.uvm_phase
import imported.uvm_pkg.uvm_test
import io.verik.core.*

@EntryPoint
class AddTest(name: String, parent: uvm_component?) : uvm_test(name, parent) {

    @Inject
    val header = """
        import uvm_pkg::*;
        `include "uvm_macros.svh"
        `uvm_component_utils(${t<AddTest>()});
    """.trimIndent()

    val bfm: TinyAluBfm = nc()

    init{
        if (!uvm_config_db.get<TinyAluBfm>(null, "*", "bfm", bfm)) fatal("Failed to get BFM")
    }

    @Task
    override fun run_phase(phase: uvm_phase?) {
        phase!!.raise_objection(this)
        val add_tester = AddTester(bfm)
        val scoreboard = Scoreboard(bfm)
        fork { scoreboard.execute() }
        add_tester.execute()
        phase.drop_objection(this)
    }
}

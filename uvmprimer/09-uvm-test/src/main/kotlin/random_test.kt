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

@Entry
class random_test : uvm_test {

    @Inj
    val header = "`uvm_component_utils(${t<random_test>()});"

    val bfm: tinyalu_bfm = nc()

    constructor(name: String, parent: uvm_component?) : super(name, parent) {
        if (!uvm_config_db.get<tinyalu_bfm>(null, "*", "bfm", bfm)) fatal("Failed to get BFM")
    }

    @Task
    override fun run_phase(phase: uvm_phase?) {
        val random_tester_h: random_tester
        val scoreboard_h: scoreboard

        phase!!.raise_objection(this)

        random_tester_h = random_tester(bfm)
        scoreboard_h = scoreboard(bfm)

        fork { scoreboard_h.execute() }

        random_tester_h.execute()
        phase.drop_objection(this)
    }
}
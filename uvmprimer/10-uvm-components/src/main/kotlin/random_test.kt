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
import imported.uvm_pkg.uvm_phase
import imported.uvm_pkg.uvm_test
import io.verik.core.*

@Entry
open class random_test : uvm_test {

    @Inj
    private val header: String = "`uvm_component_utils(${t<random_test>()});"

    lateinit var tester_h: random_tester
    lateinit var coverage_h: coverage
    lateinit var scoreboard_h: scoreboard

    override fun build_phase(phase: uvm_phase?) {
        tester_h = random_tester("tester_h", this)
        coverage_h = coverage("coverage_h", this)
        scoreboard_h = scoreboard("scoreboard_h", this)
    }

    constructor(name: String, parent: uvm_component?) : super(name, parent)
}

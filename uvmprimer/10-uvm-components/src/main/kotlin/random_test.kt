/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary")

import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_phase
import imported.uvm_pkg.uvm_test
import io.verik.core.*

@Entry
open class random_test : uvm_test {

    @Inj
    private val header = "`uvm_component_utils(${t<random_test>()});"

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

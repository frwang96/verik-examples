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
@file:Suppress("JoinDeclarationAndAssignment", "ConvertSecondaryConstructorToPrimary", "ClassName")

package dice

import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_phase
import imported.uvm_pkg.uvm_subscriber
import io.verik.core.*

class coverage : uvm_subscriber<Int> {

    @Inj
    val header: String = "`uvm_component_utils(${t<coverage>()});"

    var the_roll = 0
    var dice_cg_i: dice_cg

    constructor(name: String, parent: uvm_component?) : super(name, parent) {
        dice_cg_i = dice_cg(the_roll)
    }

    override fun write(t: Int) {
        the_roll = t
        dice_cg_i.sample()
    }

    override fun report_phase(phase: uvm_phase?) {
        println("COVERAGE: ${dice_cg_i.getCoverage()}%")
    }
}

class dice_cg(
    @In var the_roll: Int
) : CoverGroup() {

    @Cover
    val cp_the_roll = cp(the_roll) {
        bins("twod6", "{2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12}")
    }
}
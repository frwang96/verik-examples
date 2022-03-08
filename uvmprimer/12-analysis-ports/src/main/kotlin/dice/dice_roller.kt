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
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary")

package dice

import imported.uvm_pkg.uvm_analysis_port
import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_phase
import io.verik.core.*

class dice_roller : uvm_component {

    @Inj
    val header = "`uvm_component_utils(${t<dice_roller>()});"

    lateinit var roll_ap: uvm_analysis_port<Int>

    override fun build_phase(phase: uvm_phase?) {
        roll_ap = uvm_analysis_port("roll_ap", this)
    }

    @Rand
    var die1: Int = 0

    @Rand
    var die2: Int = 0

    @Cons
    var d6 = c(die1 >= 1, die1 <= 6, die2 >= 1, die2 <= 6)

    @Task
    override fun run_phase(phase: uvm_phase?) {
        var the_roll: Int
        phase!!.raise_objection(this)
        repeat(20) {
            randomize()
            the_roll = die1 + die2
            roll_ap.write(the_roll)
        }
        phase.drop_objection(this)
    }

    constructor(name: String, parent: uvm_component?) : super(name, parent)
}

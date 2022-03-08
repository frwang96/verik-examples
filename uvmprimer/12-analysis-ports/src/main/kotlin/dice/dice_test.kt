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
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "unused")

package dice

import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_phase
import imported.uvm_pkg.uvm_test
import io.verik.core.*

@Entry
class dice_test : uvm_test {

    @Inj
    val header = "`uvm_component_utils(${t<dice_test>()});"

    lateinit var dice_roller_h: dice_roller
    lateinit var coverage_h: coverage
    lateinit var histogram_h: histogram
    lateinit var average_h: average

    override fun connect_phase(phase: uvm_phase?) {
        dice_roller_h.roll_ap.connect(coverage_h.analysis_export)
        dice_roller_h.roll_ap.connect(histogram_h.analysis_export)
        dice_roller_h.roll_ap.connect(average_h.analysis_export)
    }

    constructor(name: String, parent: uvm_component?) : super(name, parent)

    override fun build_phase(phase: uvm_phase?) {
        dice_roller_h = dice_roller("dice_roller_h", this)
        coverage_h = coverage("coverage_h", this)
        histogram_h = histogram("histogram_h", this)
        average_h = average("average_h", this)
    }
}

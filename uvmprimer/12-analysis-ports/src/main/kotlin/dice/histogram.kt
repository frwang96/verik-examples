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
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "JoinDeclarationAndAssignment")

package dice

import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_phase
import imported.uvm_pkg.uvm_subscriber
import io.verik.core.*

class histogram : uvm_subscriber<Int> {

    @Inj
    val header: String = "`uvm_component_utils(${t<histogram>()});"

    // TODO replace with AssociativeArray
    val rolls: Unpacked<`13`, Int> = nc()

    constructor(name: String, parent: uvm_component?) : super(name, parent) {
        for (ii in 2 .. 12) rolls[ii] = 0
    }

    override fun write(t: Int) {
        rolls[t]++
    }

    override fun report_phase(phase: uvm_phase?) {
        var bar: String
        var message: String
        message = "\n"
        for (ii in 2 .. 12) {
            val roll_msg: String
            bar = ""
            repeat(rolls[ii]) { bar += "#" }
            roll_msg = "$ii: $bar\n"
            message += roll_msg
        }
        println(message)
    }
}

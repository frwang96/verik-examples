/*
 * SPDX-License-Identifier: Apache-2.0
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
    val header = "`uvm_component_utils(${t<histogram>()});"

    val rolls: AssociativeArray<Int, Int> = nc()

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

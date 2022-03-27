/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary")

import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_phase
import imported.uvm_pkg.uvm_put_port
import io.verik.core.*

class producer : uvm_component {

    @Inj
    val header = "`uvm_component_utils(${t<producer>()});"

    var shared = 0
    lateinit var put_port_h: uvm_put_port<Int>

    override fun build_phase(phase: uvm_phase?) {
        put_port_h = uvm_put_port("put_port_h", this)
    }

    @Task
    override fun run_phase(phase: uvm_phase?) {
        phase!!.raise_objection(this)
        repeat(10) {
            delay(10)
            put_port_h.put(++shared)
            println("${time()}ns Sent: $shared")
        }
        delay(100)
        phase.drop_objection(this)
    }

    constructor(name: String, parent: uvm_component?) : super(name, parent)
}

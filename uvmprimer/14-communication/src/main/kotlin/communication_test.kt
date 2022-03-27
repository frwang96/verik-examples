/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "unused")

import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_phase
import imported.uvm_pkg.uvm_test
import imported.uvm_pkg.uvm_tlm_fifo
import io.verik.core.*

@Entry
class communication_test : uvm_test {

    @Inj
    val header = "`uvm_component_utils(${t<communication_test>()});"

    lateinit var producer_h: producer
    lateinit var consumer_h: consumer
    lateinit var fifo_h: uvm_tlm_fifo<Int>

    override fun build_phase(phase: uvm_phase?) {
        producer_h = producer("producer_h", this)
        consumer_h = consumer("consumer_h", this)
        fifo_h = uvm_tlm_fifo("fifo_h", this)
    }

    constructor(name: String, parent: uvm_component?) : super(name, parent)

    override fun connect_phase(phase: uvm_phase?) {
        producer_h.put_port_h.connect(fifo_h.put_export)
        consumer_h.get_port_h.connect(fifo_h.get_export)
    }
}
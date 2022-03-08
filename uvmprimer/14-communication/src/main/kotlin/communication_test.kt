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

import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_phase
import imported.uvm_pkg.uvm_test
import imported.uvm_pkg.uvm_tlm_fifo
import io.verik.core.*

@Entry
class communication_test : uvm_test {

    @Inj
    val header: String = "`uvm_component_utils(${t<communication_test>()});"

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
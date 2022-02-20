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
import imported.uvm_pkg.uvm_tlm_fifo
import io.verik.core.*

@Entry
class CommunicationTest(name: String, parent: uvm_component?) : uvm_test(name, parent) {

    @Inj
    val header = "`uvm_component_utils(${t<CommunicationTest>()});"

    lateinit var producer: Producer
    lateinit var consumer: Consumer
    lateinit var fifo: uvm_tlm_fifo<Int>

    override fun build_phase(phase: uvm_phase?) {
        producer = Producer("producer", this)
        consumer = Consumer("consumer", this)
        fifo = uvm_tlm_fifo("fifo", this)
    }

    override fun connect_phase(phase: uvm_phase?) {
        producer.put_port.connect(fifo.put_export)
        consumer.get_port.connect(fifo.get_export)
    }
}
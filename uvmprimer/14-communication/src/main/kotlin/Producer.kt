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
import imported.uvm_pkg.uvm_put_port
import io.verik.core.*

class Producer(name: String, parent: uvm_component?) : uvm_component(name, parent) {

    @Inj
    val header = "`uvm_component_utils(${t<Producer>()});"

    lateinit var put_port: uvm_put_port<Int>
    var result = 0

    override fun build_phase(phase: uvm_phase?) {
        put_port = uvm_put_port("put_port", this)
    }

    @Task
    override fun run_phase(phase: uvm_phase?) {
        phase!!.raise_objection(this)
        repeat(10) {
            delay(10)
            put_port.put(result)
            println("${time()}ns sent=$result")
            result++
        }
        delay(100)
        phase.drop_objection(this)
    }
}

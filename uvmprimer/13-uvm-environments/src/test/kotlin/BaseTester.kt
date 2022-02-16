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
import imported.uvm_pkg.uvm_config_db
import imported.uvm_pkg.uvm_phase
import io.verik.core.*

open class BaseTester(name: String, parent: uvm_component?) : uvm_component(name, parent) {

    @Inject
    private val header = """
        import uvm_pkg::*;
        `include "uvm_macros.svh"
        `uvm_component_utils(${t<BaseTester>()});
    """.trimIndent()

    lateinit var bfm: TinyAluBfm

    override fun build_phase(phase: uvm_phase?) {
        if (!uvm_config_db.get<TinyAluBfm>(null, "*", "bfm", bfm)) fatal("Failed to get BFM")
    }

    @Task
    override fun run_phase(phase: uvm_phase?) {
        phase!!.raise_objection(this)
        bfm.resetAlu()
        repeat(10) {
            val a = getData()
            val b = getData()
            val op = getOp()
            bfm.sendOp(a, b, op)
        }
        delay(100)
        phase.drop_objection(this)
    }

    open fun getOp(): Op {
        return Op.NOP
    }

    open fun getData(): Ubit<`8`> {
        return u0()
    }
}

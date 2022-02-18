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

package tb

import dut.Op
import imported.uvm_pkg.uvm_component
import io.verik.core.*

class AddTester(name: String, parent: uvm_component?) : RandomTester(name, parent) {

    @Inj
    private val header = """
        import uvm_pkg::*;
        `include "uvm_macros.svh"
        `uvm_component_utils(${t<AddTester>()});
    """.trimIndent()

    override fun getOp(): Op {
        return Op.ADD
    }
}
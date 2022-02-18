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

package dice

import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_phase
import imported.uvm_pkg.uvm_subscriber
import io.verik.core.*

class Histogram(name: String, parent: uvm_component?) : uvm_subscriber<Int>(name, parent) {

    @Inj
    val header = """
        import uvm_pkg::*;
        `include "uvm_macros.svh"
        `uvm_component_utils(${t<Histogram>()});
    """.trimIndent()

    val rolls: Unpacked<`12`, Int> = nc()

    override fun write(t: Int) {
        rolls[t - 1]++
    }

    override fun report_phase(phase: uvm_phase?) {
        for (i in 0 until 12) {
            print("${i+1} : ")
            repeat(rolls[i]) { print("#") }
            println()
        }
    }
}

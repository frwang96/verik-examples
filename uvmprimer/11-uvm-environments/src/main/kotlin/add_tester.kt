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

import dut.operation_t
import dut.operation_t.add_op
import imported.uvm_pkg.uvm_component
import io.verik.core.*

class add_tester : random_tester {

    @Inj
    private val header: String = "`uvm_component_utils(${t<add_tester>()});"

    override fun get_op(): operation_t {
        return add_op
    }

    constructor(name: String, parent: uvm_component?) : super(name, parent)
}
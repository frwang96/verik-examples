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
@file:Suppress("ConvertSecondaryConstructorToPrimary", "ClassName")

import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_phase
import imported.uvm_pkg.uvm_test
import io.verik.core.*

@Entry
open class random_test : uvm_test {

    @Inj
    private val header = "`uvm_component_utils(${t<random_test>()});"

    lateinit var env_h: env

    constructor(name: String, parent: uvm_component?) : super(name, parent)

    override fun build_phase(phase: uvm_phase?) {
        env_h = inji("${t<env>()}::type_id::create(${"env_h"}, $this);")
    }
}
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
import imported.uvm_pkg.uvm_config_db
import imported.uvm_pkg.uvm_phase
import imported.uvm_pkg.uvm_test
import io.verik.core.*

@Entry
open class dual_test : uvm_test {

    @Inj
    private val header = "`uvm_component_utils(${t<dual_test>()});"

    lateinit var env_h: env

    constructor(name: String, parent: uvm_component?) : super(name, parent)

    override fun build_phase(phase: uvm_phase?) {
        val class_bfm: tinyalu_bfm = nc()
        val module_bfm: tinyalu_bfm = nc()
        if (!uvm_config_db.get(this, "", "class_bfm", class_bfm)) {
            inj("`uvm_fatal(${"DUAL TEST"}, ${"Failed to get class bfm"})")
        }
        if (!uvm_config_db.get(this, "", "module_bfm", module_bfm)) {
            inj("`uvm_fatal(${"DUAL TEST"}, ${"Failed to get module bfm"})")
        }

        val env_config_h = env_config(class_bfm, module_bfm)
        uvm_config_db.set(this, "env_h*", "config", env_config_h)
        env_h = inji("${t<env>()}::type_id::create(${"env_h"}, $this);")
    }
}

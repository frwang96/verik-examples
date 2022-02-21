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

import imported.uvm_pkg.*
import io.verik.core.*

class Environment(name: String, parent: uvm_component?) : uvm_env(name, parent) {

    @Inj
    val header = "`uvm_component_utils(${t<Environment>()});"

    lateinit var class_agent: TinyAluAgent
    lateinit var module_agent: TinyAluAgent

    lateinit var class_config: TinyAluAgentConfig
    lateinit var module_config: TinyAluAgentConfig

    override fun build_phase(phase: uvm_phase?) {
        val environment_config: EnvironmentConfig = nc()
        if (!uvm_config_db.get(this, "", "config", environment_config)) {
            inj("`uvm_fatal(${"ENVIRONMENT"}, ${"Failed to get config object"})")
        }
        class_config = TinyAluAgentConfig(environment_config.class_bfm, uvm_active_passive_enum.UVM_ACTIVE)
        module_config = TinyAluAgentConfig(environment_config.module_bfm, uvm_active_passive_enum.UVM_PASSIVE)

        uvm_config_db.set(this, "class_agent*", "config", class_config)
        uvm_config_db.set(this, "module_agent*", "config", module_config)

        class_agent = TinyAluAgent("class_agent", this)
        module_agent = TinyAluAgent("module_agent", this)
    }
}

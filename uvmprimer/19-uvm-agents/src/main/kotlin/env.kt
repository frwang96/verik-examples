/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "unused")

import imported.uvm_pkg.uvm_active_passive_enum.UVM_ACTIVE
import imported.uvm_pkg.uvm_active_passive_enum.UVM_PASSIVE
import imported.uvm_pkg.uvm_component
import imported.uvm_pkg.uvm_config_db
import imported.uvm_pkg.uvm_env
import imported.uvm_pkg.uvm_phase
import io.verik.core.*

class env : uvm_env {

    @Inj
    val header = "`uvm_component_utils(${t<env>()});"

    lateinit var class_tinyalu_agent_h: tinyalu_agent
    lateinit var module_tinyalu_agent_h: tinyalu_agent

    lateinit var class_config_h: tinyalu_agent_config
    lateinit var module_config_h: tinyalu_agent_config

    constructor(name: String, parent: uvm_component?) : super(name, parent)

    override fun build_phase(phase: uvm_phase?) {
        val env_config_h: env_config = nc()
        if (!uvm_config_db.get(this, "", "config", env_config_h)) {
            inj("`uvm_fatal(${"ENVIRONMENT"}, ${"Failed to get config object"})")
        }
        class_config_h = tinyalu_agent_config(env_config_h.class_bfm, UVM_ACTIVE)
        module_config_h = tinyalu_agent_config(env_config_h.module_bfm, UVM_PASSIVE)

        uvm_config_db.set(this, "class_tinyalu_agent_h*", "config", class_config_h)
        uvm_config_db.set(this, "module_tinyalu_agent_h*", "config", module_config_h)

        class_tinyalu_agent_h = tinyalu_agent("class_tinyalu_agent_h", this)
        module_tinyalu_agent_h = tinyalu_agent("module_tinyalu_agent_h", this)
    }
}

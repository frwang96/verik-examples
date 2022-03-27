/*
 * SPDX-License-Identifier: Apache-2.0
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

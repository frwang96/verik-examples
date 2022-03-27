/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "FunctionName")

import imported.uvm_pkg.uvm_active_passive_enum
import io.verik.core.*

class tinyalu_agent_config : Class {

    val bfm: tinyalu_bfm
    private val is_active: uvm_active_passive_enum

    constructor(bfm: tinyalu_bfm, is_active: uvm_active_passive_enum) : super() {
        this.bfm = bfm
        this.is_active = is_active
    }

    fun get_is_active(): uvm_active_passive_enum {
        return is_active
    }
}

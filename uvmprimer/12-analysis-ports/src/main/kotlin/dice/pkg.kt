/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

package dice

import io.verik.core.*

@Inj
val header = """
    import uvm_pkg::*;
    
    `include "uvm_macros.svh"
""".trimIndent()

/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName")

import dut.operation_t
import io.verik.core.*

class command_s(
    var A: Ubit<`8`>,
    var B: Ubit<`8`>,
    var op: operation_t
) : Struct()

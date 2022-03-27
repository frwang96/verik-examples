/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "EnumEntryName")

package dut

import io.verik.core.*

enum class operation_t(val value: Ubit<`3`>) {
    no_op(u(0b000)),
    add_op(u(0b001)),
    and_op(u(0b010)),
    xor_op(u(0b011)),
    mul_op(u(0b100)),
    rst_op(u(0b111))
}

/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("UNUSED_TYPEALIAS_PARAMETER")

package riscv

import io.verik.core.*

typealias REGINDEX_BITS<
    ENABLE_REGS_16_31,
    ENABLE_IRQ,
    ENABLE_IRQ_QREGS
> = ADD<IF<ENABLE_REGS_16_31, `5`, `4`>, AND<ENABLE_IRQ, ENABLE_IRQ_QREGS>>

enum class CpuState {
    CPU_STATE_TRAP,
    CPU_STATE_FETCH,
    CPU_STATE_LD_RS1,
    CPU_STATE_LD_RS2,
    CPU_STATE_EXEC,
    CPU_STATE_SHIFT,
    CPU_STATE_STMEM,
    CPU_STATE_LDMEM
}
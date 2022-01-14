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
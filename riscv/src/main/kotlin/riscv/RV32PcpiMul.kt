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

package riscv

import io.verik.core.*

class RV32PcpiMul(
    @In var clk: Boolean,
    @In var resetn: Boolean,
    @In var pcpi_valid: Boolean,
    @In var pcpi_insn: Ubit<`32`>,
    @In var pcpi_rs1: Ubit<`32`>,
    @In var pcpi_rs2: Ubit<`32`>,
    @Out var pcpi_wr: Boolean,
    @Out var pcpi_rd: Ubit<`32`>,
    @Out var pcpi_wait: Boolean,
    @Out var pcpi_ready: Boolean
) : Module() {

    var instr_mul: Boolean = nc()
    var instr_mulh: Boolean = nc()
    var instr_mulhsu: Boolean = nc()
    var instr_mulhu: Boolean = nc()
    var instr_any_mul: Boolean = nc()
    var instr_any_mulh: Boolean = nc()
    var instr_rs1_signed: Boolean = nc()
    var instr_rs2_signed: Boolean = nc()

    var pcpi_wait_q: Boolean = nc()
    var mul_start: Boolean = nc()

    @Seq
    fun seqInstr() {
        instr_mul = false
        instr_mulh = false
        instr_mulhsu = false
        instr_mulhu = false

        if (resetn && pcpi_valid && (pcpi_insn[6, 0] == u(0b0110011)) && (pcpi_insn[31, 25] == u(0b0000001))) {
            when (pcpi_insn[14, 12]) {
                u(0b000) -> instr_mul = true
                u(0b001) -> instr_mulh = true
                u(0b010) -> instr_mulhsu = true
                u(0b011) -> instr_mulhu = true
            }
        }

        pcpi_wait = instr_any_mul
        pcpi_wait_q = pcpi_wait
    }

    var rs1: Ubit<`64`> = nc()
    var rs2: Ubit<`64`> = nc()
    var rd: Ubit<`64`> = nc()
    var rdx: Ubit<`64`> = nc()

    var next_rs1: Ubit<`64`> = nc()
    var next_rs2: Ubit<`64`> = nc()
    var this_rs2: Ubit<`64`> = nc()
    var next_rd: Ubit<`64`> = nc()
    var next_rdx: Ubit<`64`> = nc()
    var next_rdt: Ubit<`64`> = nc()

    var mul_counter: Ubit<`7`> = nc()
    var mul_waiting: Boolean = nc()
    var mul_finish: Boolean = nc()

    @Com
    fun comNext() {
        next_rd = rd
        next_rdx = rdx
        next_rs1 = rs1
        next_rs2 = rs2
    }
}
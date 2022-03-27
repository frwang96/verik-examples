/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

package riscv

import io.verik.core.*

class RV32PcpiDiv(
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

    var instr_div: Boolean = nc()
    var instr_divu: Boolean = nc()
    var instr_rem: Boolean = nc()
    var instr_remu: Boolean = nc()
    var instr_any_div_rem: Boolean = nc()

    var pcpi_wait_q: Boolean = nc()
    var start: Boolean = nc()

    @Seq
    fun seqInstr() {
        on(posedge(clk)) {
            instr_div = false
            instr_divu = false
            instr_rem = false
            instr_remu = false

            if (resetn && pcpi_valid && !pcpi_ready && pcpi_insn[6, 0] == u(0b0110011) && pcpi_insn[31, 25] == u(0b0000001)) {
                when (pcpi_insn[14, 12]) {
                    u(0b100) -> instr_div = true
                    u(0b101) -> instr_divu = true
                    u(0b110) -> instr_rem = true
                    u(0b111) -> instr_remu = true
                }
            }

            pcpi_wait = instr_any_div_rem && resetn
            pcpi_wait_q = pcpi_wait && resetn
        }
    }

    var dividend: Ubit<`32`> = nc()
    var divisor: Ubit<`63`> = nc()
    var quotient: Ubit<`32`> = nc()
    var quotient_msk: Ubit<`32`> = nc()
    var running: Boolean = nc()
    var outsign: Boolean = nc()

    @Seq
    fun seqDiv() {
        on(posedge(clk)) {
            pcpi_ready = false
            pcpi_wr = false
            pcpi_rd = ux()

            if (!resetn) {
                running = false
            } else if (start) {
                running = true
                dividend = if ((instr_div || instr_rem) && pcpi_rs1[31]) -pcpi_rs1 else pcpi_rs1
                divisor = (if ((instr_div || instr_rem) && pcpi_rs2[31]) -pcpi_rs2.ext<`63`>() else pcpi_rs2.ext<`63`>()) shl 31
                outsign = (instr_div && (pcpi_rs1[31] != pcpi_rs2[31]) && pcpi_rs2.orRed()) || (instr_rem && pcpi_rs1[31])
                quotient = u("32'd1")
                quotient_msk = u("32'd1") shl 31
            } else if (quotient_msk.eqz() && running) {
                running = false
                pcpi_ready = true
                pcpi_wr = true
                pcpi_rd = if (instr_div || instr_divu) {
                    if (outsign) -quotient else quotient
                } else {
                    if (outsign) -dividend else dividend
                }
            } else {
                if (divisor <= dividend.ext()) {
                    dividend -= divisor.tru<`32`>()
                    quotient = quotient or quotient_msk
                }
                divisor = divisor shr 1
                quotient_msk = quotient_msk shr 1
            }
        }
    }
}
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
import riscv.CpuState.*

/**
 * RISC-V RV32IMC core based on the PicoRV32 project.
 */
class RV32<
    ENABLE_REGS_16_31 : `*`,
    TWO_CYCLE_ALU : `*`,
    ENABLE_MUL: `*`,
    ENABLE_FAST_MUL: `*`,
    ENABLE_DIV: `*`,
    ENABLE_IRQ : `*`,
    ENABLE_IRQ_QREGS : `*`
>(
    @In val ENABLE_COUNTERS: Boolean,
    @In val ENABLE_COUNTERS64: Boolean,
    @In val ENABLE_REGS_DUALPORT: Boolean,
    @In val LATCHED_MEM_RDATA: Boolean,
    @In val TWO_STAGE_SHIFT: Boolean,
    @In val BARREL_SHIFTER: Boolean,
    @In val TWO_CYCLE_COMPARE: Boolean,
    @In val COMPRESSED_ISA: Boolean,
    @In val CATCH_MISALIGN: Boolean,
    @In val CATCH_ILLINSN: Boolean,
    @In val ENABLE_PCPI: Boolean,
    @In val ENABLE_IRQ_TIMER: Boolean,
    @In val ENABLE_TRACE: Boolean,
    @In val MASKED_IRQ: Ubit<`32`>,
    @In val LATCHED_IRQ: Ubit<`32`>,
    @In val PROGADDR_RESET: Ubit<`32`>,
    @In val PROGADDR_IRQ: Ubit<`32`>,
    @In val STACKADDR: Ubit<`32`>,

    @In var clk: Boolean,
    @In var resetn: Boolean,
    @Out var trap: Boolean,

    @Out var mem_valid: Boolean,
    @Out var mem_instr: Boolean,
    @In var mem_ready: Boolean,
    @Out var mem_addr: Ubit<`32`>,
    @Out var mem_wdata: Ubit<`32`>,
    @Out var mem_wstrb: Ubit<`4`>,
    @In var mem_rdata: Ubit<`32`>,

    // Look-Ahead Interface
    @Out var mem_la_read: Boolean,
    @Out var mem_la_write: Boolean,
    @Out var mem_la_addr: Ubit<`32`>,
    @Out var mem_la_wdata: Ubit<`32`>,
    @Out var mem_la_wstrb: Ubit<`4`>,

    // Pico Co-Processor Interface (PCPI)
    @Out var pcpi_valid: Boolean,
    @Out var pcpi_insn: Ubit<`32`>,
    @Out var pcpi_rs1: Ubit<`32`>,
    @Out var pcpi_rs2: Ubit<`32`>,
    @In var pcpi_wr: Boolean,
    @In var pcpi_rd: Ubit<`32`>,
    @In var pcpi_wait: Boolean,
    @In var pcpi_ready: Boolean,

    // IRQ Interface
    @In var irq: Ubit<`32`>,
    @Out var eoi: Ubit<`32`>,

    // Trace Interface
    @Out var trace_valid: Boolean,
    @Out var trace_data: Ubit<`36`>
) : Module() {

    val ENABLE_REGS_16_31 = b<ENABLE_REGS_16_31>()
    val TWO_CYCLE_ALU = b<TWO_CYCLE_ALU>()
    val ENABLE_MUL = b<ENABLE_MUL>()
    val ENABLE_FAST_MUL = b<ENABLE_FAST_MUL>()
    val ENABLE_DIV = b<ENABLE_DIV>()
    val ENABLE_IRQ = b<ENABLE_IRQ>()
    val ENABLE_IRQ_QREGS = b<ENABLE_IRQ_QREGS>()
    val REGINDEX_BITS = i<REGINDEX_BITS<ENABLE_REGS_16_31, ENABLE_IRQ, ENABLE_IRQ_QREGS>>()

    val IRQREGS_OFFSET = if (ENABLE_REGS_16_31) u(32) else u(16)

    val IRQ_TIMER = 0
    val IRQ_EBREAK = 1
    val IRQ_BUSERROR = 2

    val WITH_PCPI = ENABLE_PCPI || ENABLE_MUL || ENABLE_FAST_MUL || ENABLE_DIV

    val TRACE_BRANCH: Ubit<`36`> = cat(u(0b0001), u("32'b0"))
    val TRACE_ADDR: Ubit<`36`>   = cat(u(0b0010), u("32'b0"))
    val TRACE_IRQ: Ubit<`36`>    = cat(u(0b1000), u("32'b0"))

    var count_cycle: Ubit<`64`> = nc()
    var count_instr: Ubit<`64`> = nc()
    var reg_pc: Ubit<`32`> = nc()
    var reg_next_pc: Ubit<`32`> = nc()
    var reg_op1: Ubit<`32`> = nc()
    var reg_op2: Ubit<`32`> = nc()
    var reg_out: Ubit<`32`> = nc()
    var reg_sh: Ubit<`5`> = nc()

    var next_insn_opcode: Ubit<`32`> = nc()
    var dbg_insn_opcode: Ubit<`32`> = nc()
    var dbg_insn_addr: Ubit<`32`> = nc()

    @Com
    fun comPcpiRs() {
        pcpi_rs1 = reg_op1
        pcpi_rs2 = reg_op2
    }

    var next_pc: Ubit<`32`> = nc()

    var irq_delay: Boolean = nc()
    var irq_active: Boolean = nc()
    var irq_mask: Ubit<`32`> = nc()
    var irq_pending: Ubit<`32`> = nc()
    var timer: Ubit<`32`> = nc()

    // Internal PCPI Cores

    var pcpi_mul_wr: Boolean = nc()
    var pcpi_mul_rd: Ubit<`32`> = nc()
    var pcpi_mul_wait: Boolean = nc()
    var pcpi_mul_ready: Boolean = nc()

    var pcpi_div_wr: Boolean = nc()
    var pcpi_div_rd: Ubit<`32`> = nc()
    var pcpi_div_wait: Boolean = nc()
    var pcpi_div_ready: Boolean = nc()

    var pcpi_int_wr: Boolean = nc()
    var pcpi_int_rd: Ubit<`32`> = nc()
    var pcpi_int_wait: Boolean = nc()
    var pcpi_int_ready: Boolean = nc()

    @Make
    val pcpi_fast_mul = optional<ENABLE_FAST_MUL, RV32PcpiFastMul> {
        RV32PcpiFastMul(
            clk = clk,
            resetn = resetn,
            pcpi_valid = pcpi_valid,
            pcpi_insn = pcpi_insn,
            pcpi_rs1 = pcpi_rs1,
            pcpi_rs2 = pcpi_rs2,
            pcpi_wr = pcpi_mul_wr,
            pcpi_rd = pcpi_mul_rd,
            pcpi_wait = pcpi_mul_wait,
            pcpi_ready = pcpi_mul_ready
        )
    }

    @Make
    val pcpi_mul = optional<ENABLE_MUL, RV32PcpiMul> {
        RV32PcpiMul(
            clk = clk,
            resetn = resetn,
            pcpi_valid = pcpi_valid,
            pcpi_insn = pcpi_insn,
            pcpi_rs1 = pcpi_rs1,
            pcpi_rs2 = pcpi_rs2,
            pcpi_wr = pcpi_mul_wr,
            pcpi_rd = pcpi_mul_rd,
            pcpi_wait = pcpi_mul_wait,
            pcpi_ready = pcpi_mul_ready
        )
    }

    @Com
    fun comPcpiMul() {
        if (!ENABLE_MUL && !ENABLE_FAST_MUL) {
            pcpi_mul_wr = false
            pcpi_mul_rd = ux()
            pcpi_mul_wait = false
            pcpi_mul_ready = false
        }
    }

    @Make
    val pcpi_div = optional<ENABLE_DIV, RV32PcpiDiv> {
        RV32PcpiDiv(
            clk = clk,
            resetn = resetn,
            pcpi_valid = pcpi_valid,
            pcpi_insn = pcpi_insn,
            pcpi_rs1 = pcpi_rs1,
            pcpi_rs2 = pcpi_rs2,
            pcpi_wr = pcpi_div_wr,
            pcpi_rd = pcpi_div_rd,
            pcpi_wait = pcpi_div_wait,
            pcpi_ready = pcpi_div_ready
        )
    }

    @Com
    fun comPcpiDiv() {
        if (!ENABLE_DIV) {
            pcpi_div_wr = false
            pcpi_div_rd = ux()
            pcpi_div_wait = false
            pcpi_div_ready = false
        }
    }

    @Com
    fun comPcpiInt() {
        pcpi_int_wr = false
        pcpi_int_rd = ux()
        pcpi_int_wait = cat(ENABLE_PCPI && pcpi_wait, (ENABLE_MUL || ENABLE_FAST_MUL) && pcpi_mul_wait, ENABLE_DIV && pcpi_div_wait).orRed()
        pcpi_int_ready = cat(ENABLE_PCPI && pcpi_ready, (ENABLE_MUL || ENABLE_FAST_MUL) && pcpi_mul_ready, ENABLE_DIV && pcpi_div_ready).orRed()
        when {
            ENABLE_PCPI && pcpi_ready -> {
                pcpi_int_wr = if (ENABLE_PCPI) pcpi_wr else false
                pcpi_int_rd = if (ENABLE_PCPI) pcpi_rd else u0()
            }
            (ENABLE_MUL || ENABLE_FAST_MUL) && pcpi_mul_ready -> {
                pcpi_int_wr = pcpi_mul_wr
                pcpi_int_rd = pcpi_mul_rd
            }
            ENABLE_DIV && pcpi_div_ready  -> {
                pcpi_int_wr = pcpi_div_wr
                pcpi_int_rd = pcpi_div_rd
            }
        }
    }

    // Memory Interface

    var mem_state: Ubit<`2`> = nc()
    var mem_wordsize: Ubit<`2`> = nc()
    var mem_rdata_word: Ubit<`32`> = nc()
    var mem_rdata_q: Ubit<`32`> = nc()
    var mem_do_prefetch: Boolean = nc()
    var mem_do_rinst: Boolean = nc()
    var mem_do_rdata: Boolean = nc()
    var mem_do_wdata: Boolean = nc()

    var mem_xfer: Boolean = nc()
    var mem_la_secondword: Boolean = nc()
    var mem_la_firstword_reg: Boolean = nc()
    var last_mem_valid: Boolean = nc()

    @Com
    var mem_la_firstword =
        COMPRESSED_ISA && (mem_do_prefetch || mem_do_rinst) && next_pc[1] && !mem_la_secondword
    @Com
    var mem_la_firstword_xfer =
        COMPRESSED_ISA && mem_xfer && if (!last_mem_valid) mem_la_firstword else mem_la_firstword_reg

    var prefetched_high_word: Boolean = nc()
    var clear_prefetched_high_word: Boolean = nc()
    var mem_16bit_buffer: Ubit<`16`> = nc()

    var mem_rdata_latched_noshuffle: Ubit<`32`> = nc()
    var mem_rdata_latched: Ubit<`32`> = nc()

    @Com
    var mem_la_use_prefetched_high_word = COMPRESSED_ISA && mem_la_firstword && prefetched_high_word && !clear_prefetched_high_word

    @Com
    var mem_busy = cat(mem_do_prefetch, mem_do_rinst, mem_do_rdata, mem_do_wdata).orRed()
    @Com
    var mem_done = resetn &&
        ((mem_xfer && mem_state.orRed() && (mem_do_rinst || mem_do_rdata || mem_do_wdata)) || (mem_state.andRed() && mem_do_rinst)) &&
        (!mem_la_firstword || (!mem_rdata_latched[1, 0].andRed() && mem_xfer))

    @Com
    fun comMem() {
        mem_xfer = (mem_valid && mem_ready) || (mem_la_use_prefetched_high_word && mem_do_rinst)
        mem_la_write = resetn && !mem_state && mem_do_wdata
        mem_la_read = resetn && (
            (!mem_la_use_prefetched_high_word && !mem_state && (mem_do_rinst || mem_do_prefetch || mem_do_rdata)) ||
            (COMPRESSED_ISA && mem_xfer && (if (!last_mem_valid) mem_la_firstword else mem_la_firstword_reg) && !mem_la_secondword && mem_rdata_latched[1, 0].andRed())
        )
        mem_la_addr = if (mem_do_prefetch || mem_do_rinst) {
            cat(next_pc[31, 2] + mem_la_firstword_xfer.toUbit<`1`>(), u(0b00))
        } else cat(reg_op1[31, 2], u(0b00))
        mem_rdata_latched_noshuffle = if (mem_xfer || LATCHED_MEM_RDATA) mem_rdata else mem_rdata_q
        mem_rdata_latched = when {
            COMPRESSED_ISA && mem_la_use_prefetched_high_word -> cat(u("16'bx"), mem_16bit_buffer)
            COMPRESSED_ISA && mem_la_secondword -> cat(mem_rdata_latched_noshuffle[15, 0], mem_16bit_buffer)
            COMPRESSED_ISA && mem_la_firstword -> cat(u("16'bx"), mem_rdata_latched_noshuffle[31, 16])
            else -> mem_rdata_latched_noshuffle
        }
    }

    @Seq
    fun seqLastMem() {
        on(posedge(clk)) {
            if (!resetn) {
                mem_la_firstword_reg = false
                last_mem_valid = false
            } else {
                if (!last_mem_valid)
                    mem_la_firstword_reg = mem_la_firstword
                last_mem_valid = mem_valid && !mem_ready
            }
        }
    }

    @Com
    fun comMemRdata() {
        when (mem_wordsize) {
            u(0b00) -> {
                mem_la_wdata = reg_op2
                mem_la_wstrb = u(0b1111)
                mem_rdata_word = mem_rdata
            }
            u(0b01) -> {
                mem_la_wdata = rep<`2`>(reg_op2.tru<`16`>())
                mem_la_wstrb = if (reg_op1[1]) u(0b1100) else u(0b0011)
                mem_rdata_word = when (reg_op1[1]) {
                    false -> cat(u("16'b0"), mem_rdata[15, 0])
                    true -> cat(u("16'b0"), mem_rdata[31, 16])
                }
            }
            u(0b10) -> {
                mem_la_wdata = rep<`4`>(reg_op2.tru<`8`>())
                mem_la_wstrb = u(0b0001) shl reg_op1.tru<`2`>()
                mem_rdata_word = when (reg_op1.tru<`2`>()) {
                    u(0b00) -> cat(u("24'b0"), mem_rdata[7, 0])
                    u(0b01) -> cat(u("24'b0"), mem_rdata[15, 8])
                    u(0b10) -> cat(u("24'b0"), mem_rdata[23, 16])
                    u(0b11) -> cat(u("24'b0"), mem_rdata[31, 24])
                    else -> ux()
                }
            }
            else -> {
                mem_la_wdata = ux()
                mem_la_wstrb = ux()
                mem_rdata_word = ux()
            }
        }
    }

    @Seq
    fun seqMemRdataQ() {
        on(posedge(clk)) {
            if (mem_xfer) {
                mem_rdata_q = if (COMPRESSED_ISA) mem_rdata_latched else mem_rdata
                next_insn_opcode = if (COMPRESSED_ISA) mem_rdata_latched else mem_rdata
            }
            if (COMPRESSED_ISA && mem_done && (mem_do_prefetch || mem_do_rinst)) {
                when (mem_rdata_latched[1, 0]) {
                    u(0b00) -> { // Quadrant 0
                        when (mem_rdata_latched[15, 13]) {
                            u(0b000) -> { // C.ADDI4SPN
                                mem_rdata_q[14, 12] = u(0b000)
                                mem_rdata_q[31, 20] = cat(u(0b00), mem_rdata_latched[10, 7], mem_rdata_latched[12, 11], mem_rdata_latched[5], mem_rdata_latched[6], u(0b00))
                            }
                            u(0b010) -> { // C.LW
                                mem_rdata_q[31, 20] = cat(u(0b0_0000), mem_rdata_latched[5], mem_rdata_latched[12, 10], mem_rdata_latched[6], u(0b00))
                                mem_rdata_q[14, 12] = u(0b010)
                            }
                            u(0b110) -> { // C.SW
                                val mem_rdata_q_scrambled = cat(u(0b0_0000), mem_rdata_latched[5], mem_rdata_latched[12, 10], mem_rdata_latched[6], u(0b00))
                                mem_rdata_q[11, 7] = mem_rdata_q_scrambled[4, 0]
                                mem_rdata_q[31, 25] = mem_rdata_q_scrambled[11, 5]
                                mem_rdata_q[14, 12] = u(0b010)
                            }
                        }
                    }
                    u(0b01) -> { // Quadrant 1
                        when (mem_rdata_latched[15, 13]) {
                            u(0b000) -> { // C.ADDI
                                mem_rdata_q[14, 12] = u(0b000)
                                mem_rdata_q[31, 20] = cat(mem_rdata_latched[12], mem_rdata_latched[6, 2]).sext<`12`>()
                            }
                            u(0b010) -> { // C.LI
                                mem_rdata_q[14, 12] = u(0b000)
                                mem_rdata_q[31, 20] = cat(mem_rdata_latched[12], mem_rdata_latched[6, 2]).sext<`12`>()
                            }
                            u(0b011) -> {
                                if (mem_rdata_latched[11, 7] == u("5'd2")) { // C.ADDI16SP
                                    mem_rdata_q[14, 12] = u(0b000)
                                    mem_rdata_q[31, 20] = cat(
                                        mem_rdata_latched[12],
                                        mem_rdata_latched[4, 3],
                                        mem_rdata_latched[5],
                                        mem_rdata_latched[2],
                                        mem_rdata_latched[6],
                                        u(0b0000)
                                    ).sext<`12`>()
                                } else { // C.LUI
                                    mem_rdata_q[31, 20] = cat(mem_rdata_latched[12], mem_rdata_latched[6, 2]).sext<`12`>()
                                }
                            }
                            u(0b100) -> {
                                when {
                                    mem_rdata_latched[11, 10] == u(0b00) -> { // C.SRLI
                                        mem_rdata_q[31, 25] = u(0b000_0000)
                                        mem_rdata_q[14, 12] = u(0b101)
                                    }
                                    mem_rdata_latched[11, 10] == u(0b01) -> { // C.SRAI
                                        mem_rdata_q[31, 25] = u(0b010_0000)
                                        mem_rdata_q[14, 12] = u(0b101)
                                    }
                                    mem_rdata_latched[11, 10] == u(0b10) -> { // C.ANDI
                                        mem_rdata_q[14, 12] = u(0b111)
                                        mem_rdata_q[31, 20] = cat(mem_rdata_latched[12], mem_rdata_latched[6, 2]).sext<`12`>()
                                    }
                                    mem_rdata_latched[12, 10] == u(0b011) -> { // C.SUB, C.XOR, C.OR, C.AND
                                        when (mem_rdata_latched[6, 5]) {
                                            u(0b00) -> mem_rdata_q[14, 12] = u(0b000)
                                            u(0b01) -> mem_rdata_q[14, 12] = u(0b100)
                                            u(0b10) -> mem_rdata_q[14, 12] = u(0b110)
                                            u(0b11) -> mem_rdata_q[14, 12] = u(0b111)
                                        }
                                        mem_rdata_q[31, 25] = if (mem_rdata_latched[6, 5] == u(0b00)) u(0b010_0000) else u(0b000_0000)
                                    }
                                }
                            }
                            u(0b110) -> { // C.BEQZ
                                mem_rdata_q[14, 12] = u(0b000)
                                val mem_rdata_q_scrambled = cat(
                                    mem_rdata_latched[12],
                                    mem_rdata_latched[6, 5],
                                    mem_rdata_latched[2],
                                    mem_rdata_latched[11, 10],
                                    mem_rdata_latched[4, 3]
                                ).sext<`12`>()
                                mem_rdata_q[11, 8] = mem_rdata_q_scrambled[3, 0]
                                mem_rdata_q[30, 25] = mem_rdata_q_scrambled[9, 4]
                                mem_rdata_q[7] = mem_rdata_q_scrambled[10]
                                mem_rdata_q[31] = mem_rdata_q_scrambled[11]
                            }
                            u(0b111) -> { // C.BNEZ
                                mem_rdata_q[14, 12] = u(0b001)
                                val mem_rdata_q_scrambled = cat(
                                    mem_rdata_latched[12],
                                    mem_rdata_latched[6, 5],
                                    mem_rdata_latched[2],
                                    mem_rdata_latched[11, 10],
                                    mem_rdata_latched[4, 3]
                                ).sext<`12`>()
                                mem_rdata_q[11, 8] = mem_rdata_q_scrambled[3, 0]
                                mem_rdata_q[30, 25] = mem_rdata_q_scrambled[9, 4]
                                mem_rdata_q[7] = mem_rdata_q_scrambled[10]
                                mem_rdata_q[31] = mem_rdata_q_scrambled[11]
                            }
                        }
                    }
                    u(0b10) -> { // Quadrant 2
                        when (mem_rdata_latched[15, 13]) {
                            u(0b000) -> { // C.SLLI
                                mem_rdata_q[31, 25] = u(0b000_0000)
                                mem_rdata_q[14, 12] = u(0b001)
                            }
                            u(0b010) -> { // C.LWSP
                                mem_rdata_q[31, 20] = cat(u(0b0000), mem_rdata_latched[3, 2], mem_rdata_latched[12], mem_rdata_latched[6, 4], u(0b00))
                                mem_rdata_q[14, 12] = u(0b010)
                            }
                            u(0b100) -> {
                                when {
                                    !mem_rdata_latched[12] && mem_rdata_latched[6, 2].eqz() -> { // C.JR
                                        mem_rdata_q[14, 12] = u("3'b0")
                                        mem_rdata_q[31, 20] = u("12'b0")
                                    }
                                    !mem_rdata_latched[12] && mem_rdata_latched[6, 2].neqz() -> { // C.MC
                                        mem_rdata_q[14, 12] = u("3'b0")
                                        mem_rdata_q[31, 25] = u("7'b0")
                                    }
                                    mem_rdata_latched[12] && mem_rdata_latched[11, 7].neqz() && mem_rdata_latched[6, 2].eqz() -> { // C.JALR
                                        mem_rdata_q[14, 12] = u("3'b0")
                                        mem_rdata_q[31, 20] = u("12'b0")
                                    }
                                    mem_rdata_latched[12] && mem_rdata_latched[6, 2].neqz() -> { // C.ADD
                                        mem_rdata_q[14, 12] = u("3'b0")
                                        mem_rdata_q[31, 25] = u("7'b0")
                                    }
                                }
                            }
                            u(0b110) -> { // C.SWSP
                                val mem_rdata_latched_scrambled = cat(u(0b0000), mem_rdata_latched[8, 7], mem_rdata_latched[12, 9], u(0b00))
                                mem_rdata_q[11, 7] = mem_rdata_latched_scrambled[4, 0]
                                mem_rdata_q[31, 25] = mem_rdata_latched_scrambled[11, 5]
                                mem_rdata_q[14, 12] = u(0b010)
                            }
                        }
                    }
                }
            }
        }
    }

    @Seq
    fun assertMemDo() {
        on(posedge(clk)) {
            if (resetn && !trap) {
                if (mem_do_prefetch || mem_do_rinst || mem_do_rdata)
                    assert(!mem_do_wdata)
                if (mem_do_prefetch || mem_do_rinst)
                    assert(!mem_do_rdata)
                if (mem_do_rdata)
                    assert(!mem_do_prefetch && !mem_do_rinst)
                if (mem_do_wdata)
                    assert(!(mem_do_prefetch || mem_do_rinst || mem_do_rdata))
                if (mem_state == u("2'd2") || mem_state == u("2'd3"))
                    assert(mem_valid || mem_do_prefetch)
            }
        }
    }

    @Seq
    fun seqMemState() {
        on(posedge(clk)) {
            if (!resetn || trap) {
                if (!resetn)
                    mem_state = u(0b00)
                if (!resetn || mem_ready)
                    mem_valid = false
                mem_la_secondword = false
                prefetched_high_word = false
            } else {
                if (mem_la_read || mem_la_write) {
                    mem_addr = mem_la_addr
                    mem_wstrb = mem_la_wstrb and rep<`4`>(mem_la_write)
                }
                if (mem_la_write) {
                    mem_wdata = mem_la_wdata
                }
                when (mem_state) {
                    u(0b00) -> {
                        if (mem_do_prefetch || mem_do_rinst || mem_do_rdata) {
                            mem_valid = !mem_la_use_prefetched_high_word
                            mem_instr = mem_do_prefetch || mem_do_rinst
                            mem_wstrb = u(0b0000)
                            mem_state = u(0b01)
                        }
                        if (mem_do_wdata) {
                            mem_valid = true
                            mem_instr = false
                            mem_state = u(0b10)
                        }
                    }
                    u(0b01) -> {
                        assert(mem_wstrb == u(0b0000))
                        assert(mem_do_prefetch || mem_do_rinst || mem_do_rdata)
                        assert(mem_valid == !mem_la_use_prefetched_high_word)
                        assert(mem_instr == (mem_do_prefetch || mem_do_rinst))
                        if (mem_xfer) {
                            if (COMPRESSED_ISA && mem_la_read) {
                                mem_valid = true
                                mem_la_secondword = true
                                if (!mem_la_use_prefetched_high_word)
                                    mem_16bit_buffer = mem_rdata[31, 16]
                            } else {
                                mem_valid = false
                                mem_la_secondword = false
                                if (COMPRESSED_ISA && !mem_do_rdata) {
                                    if (!mem_rdata_latched[1, 0].andRed() || mem_la_secondword) {
                                        mem_16bit_buffer = mem_rdata_latched[31, 16]
                                        prefetched_high_word = true
                                    } else {
                                        prefetched_high_word = false
                                    }
                                }
                                mem_state = if (mem_do_rinst || mem_do_rdata) u(0b00) else u(0b11)
                            }
                        }
                    }
                    u(0b10) -> {
                        assert(mem_wstrb.neqz())
                        assert(mem_do_wdata)
                        if (mem_xfer) {
                            mem_valid = false
                            mem_state = u(0b00)
                        }
                    }
                    u(0b11) -> {
                        assert(mem_wstrb.neqz())
                        assert(mem_do_prefetch)
                        if (mem_do_rinst)
                            mem_state = u(0b00)
                    }
                }
            }
            if (clear_prefetched_high_word)
                prefetched_high_word = false
        }
    }

    // Instruction Decoder

    var instr_lui: Boolean = nc()
    var instr_auipc: Boolean = nc()
    var instr_jal: Boolean = nc()
    var instr_jalr: Boolean = nc()

    var instr_beq: Boolean = nc()
    var instr_bne: Boolean = nc()
    var instr_blt: Boolean = nc()
    var instr_bge: Boolean = nc()
    var instr_bltu: Boolean = nc()
    var instr_bgeu: Boolean = nc()

    var instr_lb: Boolean = nc()
    var instr_lh: Boolean = nc()
    var instr_lw: Boolean = nc()
    var instr_lbu: Boolean = nc()
    var instr_lhu: Boolean = nc()
    var instr_sb: Boolean = nc()
    var instr_sh: Boolean = nc()
    var instr_sw: Boolean = nc()

    var instr_addi: Boolean = nc()
    var instr_slti: Boolean = nc()
    var instr_sltiu: Boolean = nc()
    var instr_xori: Boolean = nc()
    var instr_ori: Boolean = nc()
    var instr_andi: Boolean = nc()
    var instr_slli: Boolean = nc()
    var instr_srli: Boolean = nc()
    var instr_srai: Boolean = nc()

    var instr_add: Boolean = nc()
    var instr_sub: Boolean = nc()
    var instr_sll: Boolean = nc()
    var instr_slt: Boolean = nc()
    var instr_sltu: Boolean = nc()
    var instr_xor: Boolean = nc()
    var instr_srl: Boolean = nc()
    var instr_sra: Boolean = nc()
    var instr_or: Boolean = nc()
    var instr_and: Boolean = nc()

    var instr_rdcycle: Boolean = nc()
    var instr_rdcycleh: Boolean = nc()
    var instr_rdinstr: Boolean = nc()
    var instr_rdinstrh: Boolean = nc()
    var instr_ecall_ebreak: Boolean = nc()

    var instr_getq: Boolean = nc()
    var instr_setq: Boolean = nc()
    var instr_retirq: Boolean = nc()
    var instr_maskirq: Boolean = nc()
    var instr_waitirq: Boolean = nc()
    var instr_timer: Boolean = nc()
    var instr_trap: Boolean = nc()

    var decoded_rd: Ubit<REGINDEX_BITS<ENABLE_REGS_16_31, ENABLE_IRQ, ENABLE_IRQ_QREGS>> = nc()
    var decoded_rs1: Ubit<REGINDEX_BITS<ENABLE_REGS_16_31, ENABLE_IRQ, ENABLE_IRQ_QREGS>> = nc()
    var decoded_rs2: Ubit<REGINDEX_BITS<ENABLE_REGS_16_31, ENABLE_IRQ, ENABLE_IRQ_QREGS>> = nc()
    var decoded_imm: Ubit<`32`> = nc()
    var decoded_imm_j: Ubit<`32`> = nc()

    var decoder_trigger: Boolean = nc()
    var decoder_trigger_q: Boolean = nc()
    var decoder_pseudo_trigger: Boolean = nc()
    var decoder_pseudo_trigger_q: Boolean = nc()
    var compressed_instr: Boolean = nc()

    var is_lui_auipc_jal: Boolean = nc()
    var is_lb_lh_lw_lbu_lhu: Boolean = nc()
    var is_slli_srli_srai: Boolean = nc()
    var is_jalr_addi_slti_sltiu_xori_ori_andi: Boolean = nc()
    var is_sb_sh_sw: Boolean = nc()
    var is_sll_srl_sra: Boolean = nc()
    var is_lui_auipc_jal_jalr_addi_add_sub: Boolean = nc()
    var is_slti_blt_slt: Boolean = nc()
    var is_sltiu_bltu_sltu: Boolean = nc()
    var is_beq_bne_blt_bge_bltu_bgeu: Boolean = nc()
    var is_lbu_lhu_lw: Boolean = nc()
    var is_alu_reg_imm: Boolean = nc()
    var is_alu_reg_reg: Boolean = nc()
    var is_compare: Boolean = nc()

    @Com
    fun comInstrTrap() {
        instr_trap = (CATCH_ILLINSN || WITH_PCPI) && cat(
            instr_lui, instr_auipc, instr_jal, instr_jalr,
            instr_beq, instr_bne, instr_blt, instr_bge, instr_bltu, instr_bgeu,
            instr_lb, instr_lh, instr_lw, instr_lbu, instr_lhu, instr_sb, instr_sh, instr_sw,
            instr_addi, instr_slti, instr_sltiu, instr_xori, instr_ori, instr_andi, instr_slli, instr_srli, instr_srai,
            instr_add, instr_sub, instr_sll, instr_slt, instr_sltu, instr_xor, instr_srl, instr_sra, instr_or, instr_and,
            instr_rdcycle, instr_rdcycleh, instr_rdinstr, instr_rdinstrh,
            instr_getq, instr_setq, instr_retirq, instr_maskirq, instr_waitirq, instr_timer
        ).eqz()
    }

    @Com
    var is_rdcycle_rdcycleh_rdinstr_rdinstrh = cat(instr_rdcycle, instr_rdcycleh, instr_rdinstr, instr_rdinstrh).orRed()

    var new_ascii_instr: String = nc()
    var dbg_ascii_instr: String = nc()
    var dbg_insn_imm: Ubit<`32`> = nc()
    var dbg_insn_rs1: Ubit<`5`> = nc()
    var dbg_insn_rs2: Ubit<`5`> = nc()
    var dbg_insn_rd: Ubit<`5`> = nc()
    var dbg_rs1val: Ubit<`32`> = nc()
    var dbg_rs2val: Ubit<`32`> = nc()
    var dbg_rs1val_valid: Boolean = nc()
    var dbg_rs2val_valid: Boolean = nc()

    @Com
    fun comDbgNewAsciiInstr() {
        new_ascii_instr = ""
        if (instr_lui)      new_ascii_instr = "lui"
        if (instr_auipc)    new_ascii_instr = "auipc"
        if (instr_jal)      new_ascii_instr = "jal"
        if (instr_jalr)     new_ascii_instr = "jalr"

        if (instr_beq)      new_ascii_instr = "beq"
        if (instr_bne)      new_ascii_instr = "bne"
        if (instr_blt)      new_ascii_instr = "blt"
        if (instr_bge)      new_ascii_instr = "bge"
        if (instr_bltu)     new_ascii_instr = "bltu"
        if (instr_bgeu)     new_ascii_instr = "bgeu"

        if (instr_lb)       new_ascii_instr = "lb"
        if (instr_lh)       new_ascii_instr = "lh"
        if (instr_lw)       new_ascii_instr = "lw"
        if (instr_lbu)      new_ascii_instr = "lbu"
        if (instr_lhu)      new_ascii_instr = "lhu"
        if (instr_sb)       new_ascii_instr = "sb"
        if (instr_sh)       new_ascii_instr = "sh"
        if (instr_sw)       new_ascii_instr = "sw"

        if (instr_addi)     new_ascii_instr = "addi"
        if (instr_slti)     new_ascii_instr = "slti"
        if (instr_sltiu)    new_ascii_instr = "sltiu"
        if (instr_xori)     new_ascii_instr = "xori"
        if (instr_ori)      new_ascii_instr = "ori"
        if (instr_andi)     new_ascii_instr = "andi"
        if (instr_slli)     new_ascii_instr = "slli"
        if (instr_srli)     new_ascii_instr = "srli"
        if (instr_srai)     new_ascii_instr = "srai"

        if (instr_add)      new_ascii_instr = "add"
        if (instr_sub)      new_ascii_instr = "sub"
        if (instr_sll)      new_ascii_instr = "sll"
        if (instr_slt)      new_ascii_instr = "slt"
        if (instr_sltu)     new_ascii_instr = "sltu"
        if (instr_xor)      new_ascii_instr = "xor"
        if (instr_srl)      new_ascii_instr = "srl"
        if (instr_sra)      new_ascii_instr = "sra"
        if (instr_or)       new_ascii_instr = "or"
        if (instr_and)      new_ascii_instr = "and"

        if (instr_rdcycle)  new_ascii_instr = "rdcycle"
        if (instr_rdcycleh) new_ascii_instr = "rdcycleh"
        if (instr_rdinstr)  new_ascii_instr = "rdinstr"
        if (instr_rdinstrh) new_ascii_instr = "rdinstrh"

        if (instr_getq)     new_ascii_instr = "getq"
        if (instr_setq)     new_ascii_instr = "setq"
        if (instr_retirq)   new_ascii_instr = "retirq"
        if (instr_maskirq)  new_ascii_instr = "maksirq"
        if (instr_waitirq)  new_ascii_instr = "waitirq"
        if (instr_timer)    new_ascii_instr = "timer"
    }

    var q_ascii_instr: String = nc()
    var q_insn_imm: Ubit<`32`> = nc()
    var q_insn_opcode: Ubit<`32`> = nc()
    var q_insn_rs1: Ubit<`5`> = nc()
    var q_insn_rs2: Ubit<`5`> = nc()
    var q_insn_rd: Ubit<`5`> = nc()
    var dbg_next: Boolean = nc()

    var launch_next_insn: Boolean = nc()
    var dbg_valid_insn: Boolean = nc()

    var cached_ascii_instr: String = nc()
    var cached_insn_imm: Ubit<`32`> = nc()
    var cached_insn_opcode: Ubit<`32`> = nc()
    var cached_insn_rs1: Ubit<`5`> = nc()
    var cached_insn_rs2: Ubit<`5`> = nc()
    var cached_insn_rd: Ubit<`5`> = nc()

    @Seq
    fun seqDbgInsn() {
        on(posedge(clk)) {
            q_ascii_instr = dbg_ascii_instr
            q_insn_imm = dbg_insn_imm
            q_insn_opcode = dbg_insn_opcode
            q_insn_rs1 = dbg_insn_rs1
            q_insn_rs2 = dbg_insn_rs2
            q_insn_rd = dbg_insn_rd
            dbg_next = launch_next_insn

            if (!resetn || trap)
                dbg_valid_insn = false
            else if (launch_next_insn)
                dbg_valid_insn = true

            if (decoder_trigger_q) {
                cached_ascii_instr = new_ascii_instr
                cached_insn_imm = decoded_imm
                cached_insn_opcode = if (next_insn_opcode[1, 0].andRed()) next_insn_opcode else cat(u0<`16`>(), next_insn_opcode.tru<`16`>())
                cached_insn_rs1 = decoded_rs1
                cached_insn_rs2 = decoded_rs2
                cached_insn_rd = decoded_rd
            }

            if (launch_next_insn)
                dbg_insn_addr = next_pc
        }
    }

    @Com
    fun comDbgInsn() {
        dbg_ascii_instr = q_ascii_instr
        dbg_insn_imm = q_insn_imm
        dbg_insn_opcode = q_insn_opcode
        dbg_insn_rs1 = q_insn_rs1
        dbg_insn_rs2 = q_insn_rs2
        dbg_insn_rd = q_insn_rd

        if (dbg_next) {
            if (decoder_pseudo_trigger_q) {
                dbg_ascii_instr = cached_ascii_instr
                dbg_insn_imm = cached_insn_imm
                dbg_insn_opcode = cached_insn_opcode
                dbg_insn_rs1 = cached_insn_rs1
                dbg_insn_rs2 = cached_insn_rs2
                dbg_insn_rd = cached_insn_rd
            } else {
                dbg_ascii_instr = new_ascii_instr
                dbg_insn_opcode = if (next_insn_opcode[1, 0].andRed()) next_insn_opcode else cat(u0<`16`>(), next_insn_opcode.tru<`16`>())
                dbg_insn_imm = decoded_imm
                dbg_insn_rs1 = decoded_rs1
                dbg_insn_rs2 = decoded_rs2
                dbg_insn_rd = decoded_rd
            }
        }
    }

    @Seq
    fun seqDbgPrintDecode() {
        on(posedge(clk)) {
            if (dbg_next) {
                if (dbg_insn_opcode[1, 0].andRed()) {
                    println("DECODE 0x$dbg_insn_addr 0x$dbg_insn_opcode ${if (dbg_ascii_instr != "") dbg_ascii_instr else "UNKNOWN"}")
                } else {
                    println("DECODE 0x$dbg_insn_addr 0x${dbg_insn_opcode.tru<`16`>()} ${if (dbg_ascii_instr != "") dbg_ascii_instr else "UNKNOWN"}")
                }
            }
        }
    }

    @Seq
    fun seqInstr() {
        on(posedge(clk)) {
            is_lui_auipc_jal = cat(instr_lui, instr_auipc, instr_jal).orRed()
            is_lui_auipc_jal_jalr_addi_add_sub = cat(instr_lui, instr_auipc, instr_jal, instr_jalr, instr_addi, instr_add, instr_sub).orRed()
            is_slti_blt_slt = cat(instr_slti, instr_blt, instr_slt).orRed()
            is_sltiu_bltu_sltu = cat(instr_sltiu, instr_bltu, instr_sltu).orRed()
            is_lbu_lhu_lw = cat(instr_lbu, instr_lhu, instr_lw).orRed()
            is_compare = cat(is_beq_bne_blt_bge_bltu_bgeu, instr_slti, instr_slt, instr_sltiu, instr_sltu).orRed()

            if (mem_do_rinst && mem_done) {
                instr_lui = mem_rdata_latched[6, 0] == u(0b0110111)
                instr_auipc = mem_rdata_latched[6, 0] == u(0b0010111)
                instr_jal = mem_rdata_latched[6, 0] == u(0b1101111)
                instr_jalr = mem_rdata_latched[6, 0] == u(0b1100111) && mem_rdata_latched[14, 12] == u(0b000)
                instr_retirq = mem_rdata_latched[6, 0] == u(0b0001011) && mem_rdata_latched[31, 25] == u(0b0000010) && ENABLE_IRQ
                instr_waitirq = mem_rdata_latched[6, 0] == u(0b0001011) && mem_rdata_latched[31, 25] == u(0b0000100) && ENABLE_IRQ

                is_beq_bne_blt_bge_bltu_bgeu = mem_rdata_latched[6, 0] == u(0b1100011)
                is_lb_lh_lw_lbu_lhu = mem_rdata_latched[6, 0] == u(0b0000011)
                is_sb_sh_sw = mem_rdata_latched[6, 0] == u(0b0100011)
                is_alu_reg_imm = mem_rdata_latched[6, 0] == u(0b0010011)
                is_alu_reg_reg = mem_rdata_latched[6, 0] == u(0b0110011)

                var decoded_imm_j_scrambled = cat(mem_rdata_latched[31, 12], false).sext<`32`>()
                decoded_imm_j[0] = decoded_imm_j_scrambled[0]
                decoded_imm_j[19, 12] = decoded_imm_j_scrambled[8, 1]
                decoded_imm_j[11] = decoded_imm_j_scrambled[9]
                decoded_imm_j[10, 1] = decoded_imm_j_scrambled[19, 10]
                decoded_imm_j[31, 20] = decoded_imm_j_scrambled[31, 20]

                decoded_rd = mem_rdata_latched[11, 7]
                decoded_rs1 = mem_rdata_latched[19, 15]
                decoded_rs2 = mem_rdata_latched[24, 20]

                if (mem_rdata_latched[6, 0] == u(0b0001011) && mem_rdata_latched[31, 25] == u(0b0000000) && ENABLE_IRQ && ENABLE_IRQ_QREGS) { // instr_getq
                    decoded_rs1[REGINDEX_BITS - 1] = true
                }
                if (mem_rdata_latched[6, 0] == u(0b0001011) && mem_rdata_latched[31, 25] == u(0b0000010) && ENABLE_IRQ) { // instr_retirq
                    decoded_rs1 = if (ENABLE_IRQ_QREGS) IRQREGS_OFFSET else u(3).ext()
                }

                compressed_instr = false
                if (COMPRESSED_ISA && mem_rdata_latched[1, 0] == u(0b11)) {
                    compressed_instr = true
                    decoded_rd = u0()
                    decoded_rs1 = u0()
                    decoded_rs2 = u0()

                    decoded_imm_j_scrambled = cat(mem_rdata_latched[12, 2], false).sext()
                    decoded_imm_j[0] = decoded_imm_j_scrambled[0]
                    decoded_imm_j[5] = decoded_imm_j_scrambled[1]
                    decoded_imm_j[3, 1] = decoded_imm_j_scrambled[4, 2]
                    decoded_imm_j[7] = decoded_imm_j_scrambled[5]
                    decoded_imm_j[6] = decoded_imm_j_scrambled[6]
                    decoded_imm_j[10] = decoded_imm_j_scrambled[7]
                    decoded_imm_j[9, 8] = decoded_imm_j_scrambled[9, 8]
                    decoded_imm_j[4] = decoded_imm_j_scrambled[10]
                    decoded_imm_j[31, 11] = decoded_imm_j_scrambled[31, 11]

                    when (mem_rdata_latched[1, 0]) {
                        u(0b00) -> {  // Quadrant 0
                            when (mem_rdata_latched[15, 13]) {
                                u(0b000) -> { /// C.ADDI4SPN
                                    is_alu_reg_imm = mem_rdata_latched[12, 5].orRed()
                                    decoded_rs1 = u(2).ext()
                                    decoded_rd = (u(8) + mem_rdata_latched[4, 2]).ext()
                                }
                                u(0b010) -> { // C.LW
                                    is_lb_lh_lw_lbu_lhu = true
                                    decoded_rs1 = (u(8) + mem_rdata_latched[9, 7]).ext()
                                    decoded_rd = (u(8) + mem_rdata_latched[4, 2]).ext()
                                }
                                u(0b110) -> { // C.SW
                                    is_sb_sh_sw = true
                                    decoded_rs1 = (u(8) + mem_rdata_latched[9, 7]).ext()
                                    decoded_rd = (u(8) + mem_rdata_latched[4, 2]).ext()
                                }
                            }
                        }
                        u(0b01) -> {  // Quadrant 1
                            when(mem_rdata_latched[15, 13]) {
                                u(0b000) -> { // C.NOP / C.ADDI
                                    is_alu_reg_imm = true
                                    decoded_rd = mem_rdata_latched[11, 7].res()
                                    decoded_rs1 = mem_rdata_latched[11, 7].res()
                                }
                                u(0b001) -> { // C.JAL
                                    instr_jal = true
                                    decoded_rd = u(1).ext()
                                }
                                u(0b011) -> {
                                    if (mem_rdata_latched[12] || mem_rdata_latched[6, 2].orRed()) { // C.ADDI16SP
                                        is_alu_reg_imm = true
                                        decoded_rd = mem_rdata_latched[11, 7].res()
                                        decoded_rs1 = mem_rdata_latched[11, 7].res()
                                    } else {
                                        instr_lui = true
                                        decoded_rd = mem_rdata_latched[11, 7].res()
                                        decoded_rs1 = u0()
                                    }
                                }
                                u(0b100) -> {
                                    when {
                                        !mem_rdata_latched[11] && !mem_rdata_latched[12] -> { // C.SRLI, C.SRAI
                                            is_alu_reg_imm = true
                                            decoded_rd = (u(8) + mem_rdata_latched[9, 7]).ext()
                                            decoded_rs1 = (u(8) + mem_rdata_latched[9, 7]).ext()
                                            decoded_rs2 = cat(mem_rdata_latched[12], mem_rdata_latched[6, 2]).tru()
                                        }
                                        mem_rdata_latched[11, 10] == u(0b10) -> { // C.ANDI
                                            is_alu_reg_imm = true
                                            decoded_rd = (u(8) + mem_rdata_latched[9, 7]).ext()
                                            decoded_rs1 = (u(8) + mem_rdata_latched[9, 7]).ext()
                                        }
                                        mem_rdata_latched[12, 10] == u(0b011) -> { // C.SUB, C.XOR, C.OR, C.AND
                                            is_alu_reg_imm = true
                                            decoded_rd = (u(8) + mem_rdata_latched[9, 7]).ext()
                                            decoded_rs1 = (u(8) + mem_rdata_latched[9, 7]).ext()
                                            decoded_rs2 = (u(8) + mem_rdata_latched[4, 2]).ext()
                                        }
                                    }
                                }
                                u(0b101) -> { // C.J
                                    instr_jal = true
                                }
                                u(0b110) -> { // C.BEQZ
                                    is_beq_bne_blt_bge_bltu_bgeu = true
                                    decoded_rs1 = (u(8) + mem_rdata_latched[9, 7]).ext()
                                    decoded_rs2 = u0()
                                }
                                u(0b111) -> { // C.BNEZ
                                    is_beq_bne_blt_bge_bltu_bgeu = true
                                    decoded_rs1 = (u(8) + mem_rdata_latched[9, 7]).ext()
                                    decoded_rs2 = u0()
                                }
                            }
                        }
                        u(0b10) -> {  // Quadrant 2
                            when (mem_rdata_latched[15, 13]) {
                                u(0b000) -> { // C.SLLI
                                    if (!mem_rdata_latched[12]) {
                                        is_alu_reg_imm = true
                                        decoded_rd = mem_rdata_latched[11, 7].res()
                                        decoded_rs1 = mem_rdata_latched[11, 7].res()
                                        decoded_rs2 = cat(mem_rdata_latched[12], mem_rdata_latched[6, 2]).tru()
                                    }
                                }
                                u(0b010) -> { // C.LWSP
                                    if (mem_rdata_latched[11, 7].orRed()) {
                                        is_lb_lh_lw_lbu_lhu = true
                                        decoded_rd = mem_rdata_latched[11, 7].res()
                                        decoded_rs1 = u(2).ext()
                                    }
                                }
                                u(0b100) -> {
                                    when {
                                        mem_rdata_latched[12] && mem_rdata_latched[11, 7].neqz() && mem_rdata_latched[6, 2].eqz() -> { // C.JR
                                            instr_jalr = true
                                            decoded_rd = u0()
                                            decoded_rs1 = mem_rdata_latched[11, 7].res()
                                        }
                                        mem_rdata_latched[12] && mem_rdata_latched[6, 2].neqz() -> { // C.MV
                                            is_alu_reg_reg = true
                                            decoded_rd = mem_rdata_latched[11, 7].res()
                                            decoded_rs1 = u0()
                                            decoded_rs2 = mem_rdata_latched[6, 2].res()
                                        }
                                        !mem_rdata_latched[12] && mem_rdata_latched[11, 7].neqz() && mem_rdata_latched[6, 2].eqz() -> { // C.JALR
                                            instr_jalr = true
                                            decoded_rd = u(1).ext()
                                            decoded_rs1 = mem_rdata_latched[11, 7].res()
                                        }
                                        !mem_rdata_latched[12] && mem_rdata_latched[6, 2].neqz() -> { // C.ADD
                                            is_alu_reg_reg = true
                                            decoded_rd = mem_rdata_latched[11, 7].res()
                                            decoded_rs1 = mem_rdata_latched[11, 7].res()
                                            decoded_rs2 = mem_rdata_latched[6, 2].res()
                                        }
                                    }
                                }
                                u(0b110) -> { // C.SWSP
                                    is_sb_sh_sw = true
                                    decoded_rs1 = u(2).ext()
                                    decoded_rs2 = mem_rdata_latched[6, 2].res()
                                }
                            }
                        }
                    }
                }
            }

            if (decoder_trigger && !decoder_pseudo_trigger) {
                pcpi_insn = if (WITH_PCPI) mem_rdata_q else ux()

                instr_beq  = is_beq_bne_blt_bge_bltu_bgeu && mem_rdata_q[14, 12] == u(0b000)
                instr_bne  = is_beq_bne_blt_bge_bltu_bgeu && mem_rdata_q[14, 12] == u(0b001)
                instr_blt  = is_beq_bne_blt_bge_bltu_bgeu && mem_rdata_q[14, 12] == u(0b100)
                instr_bge  = is_beq_bne_blt_bge_bltu_bgeu && mem_rdata_q[14, 12] == u(0b101)
                instr_bltu = is_beq_bne_blt_bge_bltu_bgeu && mem_rdata_q[14, 12] == u(0b110)
                instr_bgeu = is_beq_bne_blt_bge_bltu_bgeu && mem_rdata_q[14, 12] == u(0b111)

                instr_lb  = is_lb_lh_lw_lbu_lhu && mem_rdata_q[14, 12] == u(0b000)
                instr_lh  = is_lb_lh_lw_lbu_lhu && mem_rdata_q[14, 12] == u(0b001)
                instr_lw  = is_lb_lh_lw_lbu_lhu && mem_rdata_q[14, 12] == u(0b010)
                instr_lbu = is_lb_lh_lw_lbu_lhu && mem_rdata_q[14, 12] == u(0b100)
                instr_lhu = is_lb_lh_lw_lbu_lhu && mem_rdata_q[14, 12] == u(0b101)

                instr_sb = is_sb_sh_sw && mem_rdata_q[14, 12] == u(0b000)
                instr_sh = is_sb_sh_sw && mem_rdata_q[14, 12] == u(0b001)
                instr_sw = is_sb_sh_sw && mem_rdata_q[14, 12] == u(0b010)

                instr_addi  = is_alu_reg_imm && mem_rdata_q[14, 12] == u(0b000)
                instr_slti  = is_alu_reg_imm && mem_rdata_q[14, 12] == u(0b010)
                instr_sltiu = is_alu_reg_imm && mem_rdata_q[14, 12] == u(0b011)
                instr_xori  = is_alu_reg_imm && mem_rdata_q[14, 12] == u(0b100)
                instr_ori   = is_alu_reg_imm && mem_rdata_q[14, 12] == u(0b110)
                instr_andi  = is_alu_reg_imm && mem_rdata_q[14, 12] == u(0b111)

                instr_slli = is_alu_reg_imm && mem_rdata_q[14, 12] == u(0b001) && mem_rdata_q[31, 25] == u(0b0000000)
                instr_srli = is_alu_reg_imm && mem_rdata_q[14, 12] == u(0b101) && mem_rdata_q[31, 25] == u(0b0000000)
                instr_srai = is_alu_reg_imm && mem_rdata_q[14, 12] == u(0b101) && mem_rdata_q[31, 25] == u(0b0100000)

                instr_add  = is_alu_reg_imm && mem_rdata_q[14, 12] == u(0b000) && mem_rdata_q[31, 25] == u(0b0000000)
                instr_sub  = is_alu_reg_imm && mem_rdata_q[14, 12] == u(0b000) && mem_rdata_q[31, 25] == u(0b0100000)
                instr_sll  = is_alu_reg_imm && mem_rdata_q[14, 12] == u(0b001) && mem_rdata_q[31, 25] == u(0b0000000)
                instr_slt  = is_alu_reg_imm && mem_rdata_q[14, 12] == u(0b010) && mem_rdata_q[31, 25] == u(0b0000000)
                instr_sltu = is_alu_reg_imm && mem_rdata_q[14, 12] == u(0b011) && mem_rdata_q[31, 25] == u(0b0000000)
                instr_xor  = is_alu_reg_imm && mem_rdata_q[14, 12] == u(0b100) && mem_rdata_q[31, 25] == u(0b0000000)
                instr_srl  = is_alu_reg_imm && mem_rdata_q[14, 12] == u(0b101) && mem_rdata_q[31, 25] == u(0b0000000)
                instr_sra  = is_alu_reg_imm && mem_rdata_q[14, 12] == u(0b101) && mem_rdata_q[31, 25] == u(0b0100000)
                instr_or   = is_alu_reg_imm && mem_rdata_q[14, 12] == u(0b110) && mem_rdata_q[31, 25] == u(0b0000000)
                instr_and  = is_alu_reg_imm && mem_rdata_q[14, 12] == u(0b111) && mem_rdata_q[31, 25] == u(0b0000000)

                instr_rdcycle  = ((mem_rdata_q[6, 0] == u(0b1110011) && mem_rdata_q[31, 12] == u(0b11000000000000000010)) ||
                    (mem_rdata_q[6, 0] == u(0b1110011) && mem_rdata_q[31, 12] == u(0b11000000000100000010))) && ENABLE_COUNTERS
                instr_rdcycleh = ((mem_rdata_q[6, 0] == u(0b1110011) && mem_rdata_q[31, 12] == u(0b11000000000000000010)) ||
                    (mem_rdata_q[6, 0] == u(0b1110011) && mem_rdata_q[31, 12] == u(0b11000000000100000010))) && ENABLE_COUNTERS && ENABLE_COUNTERS64
                instr_rdinstr  = (mem_rdata_q[6, 0] == u(0b1110011) && mem_rdata_q[31, 12] == u(0b11000000001000000010)) && ENABLE_COUNTERS
                instr_rdinstrh = (mem_rdata_q[6, 0] == u(0b1110011) && mem_rdata_q[31, 12] == u(0b11000000001000000010)) && ENABLE_COUNTERS && ENABLE_COUNTERS64

                instr_ecall_ebreak = (mem_rdata_q[6, 0] == u(0b1110011) && mem_rdata_q[31, 21].eqz() && mem_rdata_q[19, 7].eqz()) ||
                    (COMPRESSED_ISA && mem_rdata_q[15, 0] == u(0x9002))

                instr_getq    = mem_rdata_q[6, 0] == u(0b0001011) && mem_rdata_q[31, 12] == u(0b0000000) && ENABLE_IRQ && ENABLE_IRQ_QREGS
                instr_setq    = mem_rdata_q[6, 0] == u(0b0001011) && mem_rdata_q[31, 12] == u(0b0000001) && ENABLE_IRQ && ENABLE_IRQ_QREGS
                instr_maskirq = mem_rdata_q[6, 0] == u(0b0001011) && mem_rdata_q[31, 12] == u(0b0000011) && ENABLE_IRQ
                instr_timer   = mem_rdata_q[6, 0] == u(0b0001011) && mem_rdata_q[31, 12] == u(0b0000101) && ENABLE_IRQ && ENABLE_IRQ_TIMER

                is_slli_srli_srai = is_alu_reg_imm && cat(
                    mem_rdata_q[14, 12] == u(0b001) && mem_rdata_q[31, 25] == u(0b0000000),
                    mem_rdata_q[14, 12] == u(0b101) && mem_rdata_q[31, 25] == u(0b0000000),
                    mem_rdata_q[14, 12] == u(0b101) && mem_rdata_q[31, 25] == u(0b0100000)
                ).orRed()

                is_jalr_addi_slti_sltiu_xori_ori_andi = instr_jalr || (is_alu_reg_imm && cat(
                    mem_rdata_q[14, 12] == u(0b000),
                    mem_rdata_q[14, 12] == u(0b010),
                    mem_rdata_q[14, 12] == u(0b011),
                    mem_rdata_q[14, 12] == u(0b100),
                    mem_rdata_q[14, 12] == u(0b110),
                    mem_rdata_q[14, 12] == u(0b111)
                ).orRed())

                is_sll_srl_sra = is_alu_reg_reg && cat(
                    mem_rdata_q[14, 12] == u(0b001) && mem_rdata_q[31, 25] == u(0b0000000),
                    mem_rdata_q[14, 12] == u(0b101) && mem_rdata_q[31, 25] == u(0b0000000),
                    mem_rdata_q[14, 12] == u(0b101) && mem_rdata_q[31, 25] == u(0b0100000)
                ).orRed()

                is_lui_auipc_jal_jalr_addi_add_sub = false
                is_compare = false

                decoded_imm = when {
                    instr_jalr -> decoded_imm_j
                    cat(instr_lui, instr_auipc).orRed() -> mem_rdata_q[31, 12].ext<`32`>() shl 12
                    cat(instr_jalr, is_lb_lh_lw_lbu_lhu, is_alu_reg_imm).orRed() -> mem_rdata_q[31, 20].sext()
                    is_beq_bne_blt_bge_bltu_bgeu -> cat(mem_rdata_q[31], mem_rdata_q[7], mem_rdata_q[30, 25], mem_rdata_q[11, 8], false).sext()
                    is_sb_sh_sw -> cat(mem_rdata_q[31, 25], mem_rdata_q[11, 7]).sext()
                    else -> ux()
                }
            }

            if (!resetn) {
                is_beq_bne_blt_bge_bltu_bgeu = false
                is_compare = false

                instr_beq = false
                instr_bne = false
                instr_blt = false
                instr_bge = false
                instr_bltu = false
                instr_bgeu = false

                instr_addi = false
                instr_slti = false
                instr_sltiu = false
                instr_xori = false
                instr_ori = false
                instr_andi = false

                instr_add = false
                instr_sub = false
                instr_sll = false
                instr_slt = false
                instr_sltu = false
                instr_xor = false
                instr_srl = false
                instr_sra = false
                instr_or = false
                instr_and = false
            }
        }
    }

    // Main State Machine

    var cpu_state: CpuState = nc()
    var irq_state: Ubit<`2`> = nc()

    var set_mem_do_rinst: Boolean = nc()
    var set_mem_do_rdata: Boolean = nc()
    var set_mem_do_wdata: Boolean = nc()

    var latched_store: Boolean = nc()
    var latched_stalu: Boolean = nc()
    var latched_branch: Boolean = nc()
    var latched_compr: Boolean = nc()
    var latched_trace: Boolean = nc()
    var latched_is_lu: Boolean = nc()
    var latched_is_lh: Boolean = nc()
    var latched_is_lb: Boolean = nc()
    var latched_rd: Ubit<REGINDEX_BITS<ENABLE_REGS_16_31, ENABLE_IRQ, ENABLE_IRQ_QREGS>> = nc()

    var current_pc: Ubit<`32`> = nc()

    @Com
    fun comNextPc() {
        next_pc = if (latched_store && latched_branch) reg_out and u(1).ext<`32`>().inv() else reg_next_pc
    }

    var pcpi_timeout_counter: Ubit<`4`> = nc()
    var pcpi_timeout: Boolean = nc()

    var next_irq_pending: Ubit<`32`> = nc()
    var do_waitirq: Boolean = nc()

    var alu_out: Ubit<`32`> = nc()
    var alu_out_q: Ubit<`32`> = nc()
    var alu_out_0: Boolean = nc()
    var alu_out_0_q: Boolean = nc()
    var alu_wait: Boolean = nc()
    var alu_wait_2: Boolean = nc()

    var alu_add_sub: Ubit<`32`> = nc()
    var alu_shl: Ubit<`32`> = nc()
    var alu_shr: Ubit<`32`> = nc()
    var alu_eq: Boolean = nc()
    var alu_ltu: Boolean = nc()
    var alu_lts: Boolean = nc()

    @Seq
    fun seqAlu() {
        if (TWO_CYCLE_ALU) {
            on(posedge(clk)) {
                alu_add_sub = if (instr_sub) reg_op1 - reg_op2 else reg_op1 + reg_op2
                alu_eq = reg_op1 == reg_op2
                alu_lts = reg_op1.toSbit() < reg_op2.toSbit()
                alu_shl = reg_op1 shl reg_op2.tru<`5`>()
                alu_shr = if (instr_sra || instr_srai) reg_op1 sshr reg_op2.tru<`5`>() else reg_op1 shr reg_op2.tru<`5`>()
            }
        }
    }

    @Com
    fun comAlu() {
        if (!TWO_CYCLE_ALU) {
            alu_add_sub = if (instr_sub) reg_op1 - reg_op2 else reg_op1 + reg_op2
            alu_eq = reg_op1 == reg_op2
            alu_lts = reg_op1.toSbit() < reg_op2.toSbit()
            alu_shl = reg_op1 shl reg_op2.tru<`5`>()
            alu_shr = if (instr_sra || instr_srai) reg_op1 sshr reg_op2.tru<`5`>() else reg_op1 shr reg_op2.tru<`5`>()
        }
    }

    @Com
    fun comAluOut() {
        alu_out_0 = when {
            instr_beq -> alu_eq
            instr_bne -> !alu_eq
            instr_bge -> !alu_lts
            instr_bgeu -> !alu_ltu
            is_slti_blt_slt && (!TWO_CYCLE_COMPARE || cat(instr_beq, instr_bne, instr_bge, instr_bgeu).eqz()) -> alu_lts
            is_sltiu_bltu_sltu && (!TWO_CYCLE_COMPARE || cat(instr_beq, instr_bne, instr_bge, instr_bgeu).eqz()) -> alu_ltu
            else -> unknown
        }

        alu_out = when {
            is_lui_auipc_jal_jalr_addi_add_sub -> alu_add_sub
            is_compare -> alu_out_0.toUbit()
            instr_xori || instr_xor -> reg_op1 xor reg_op2
            instr_ori || instr_or -> reg_op1 or reg_op2
            instr_andi || instr_and -> reg_op1 and reg_op2
            BARREL_SHIFTER && (instr_sll || instr_slli) -> alu_shl
            BARREL_SHIFTER && (instr_srl || instr_srli || instr_sra || instr_srai) -> alu_shr
            else -> ux()
        }
    }

    @Seq
    var clear_prefetched_high_word_q = oni(posedge(clk)) { clear_prefetched_high_word }

    @Com
    fun comClearPrefetchedHighWord() {
        clear_prefetched_high_word = clear_prefetched_high_word_q
        if (!prefetched_high_word)
            clear_prefetched_high_word = false
        if (latched_branch || irq_state.orRed() || !resetn)
            clear_prefetched_high_word = COMPRESSED_ISA
    }

    var cpuregs_write: Boolean = nc()
    var cpuregs_wrdata: Ubit<`32`> = nc()
    var cpuregs_rs1: Ubit<`32`> = nc()
    var cpuregs_rs2: Ubit<`32`> = nc()
    var decoded_rs: Ubit<REGINDEX_BITS<ENABLE_REGS_16_31, ENABLE_IRQ, ENABLE_IRQ_QREGS>> = nc()

    @Com
    fun comCpuregsWrite() {
        cpuregs_write = false
        cpuregs_wrdata = ux()
        if (cpu_state == CPU_STATE_FETCH) {
            when {
                latched_branch -> {
                    cpuregs_wrdata = reg_pc + if (latched_compr) u(0x2) else u(0x4)
                    cpuregs_write = true
                }
                latched_store && !latched_branch -> {
                    cpuregs_wrdata = if (latched_stalu) alu_out_q else reg_out
                    cpuregs_write = true
                }
                ENABLE_IRQ && irq_state[0] -> {
                    cpuregs_wrdata = reg_next_pc or latched_compr.toUbit()
                    cpuregs_write = true
                }
                ENABLE_IRQ && irq_state[1] -> {
                    cpuregs_wrdata = irq_pending and irq_mask.inv()
                    cpuregs_write = true
                }
            }
        }
    }

    var cpuregs_rdata1: Ubit<`32`> = nc()
    var cpuregs_rdata2: Ubit<`32`> = nc()

    @Com var cpuregs_waddr: Ubit<`6`> = latched_rd.ext()
    @Com var cpuregs_raddr1: Ubit<`6`> = if (ENABLE_REGS_DUALPORT) decoded_rs1.ext() else decoded_rs.ext()
    @Com var cpuregs_raddr2: Ubit<`6`> = if (ENABLE_REGS_DUALPORT) decoded_rs2.ext() else u0()

    @Make
    val cpuregs = RV32Regs(
        clk = clk,
        wen = resetn && cpuregs_write && latched_rd.neqz(),
        waddr = cpuregs_waddr,
        raddr1 = cpuregs_raddr1,
        raddr2 = cpuregs_raddr2,
        wdata = cpuregs_wrdata,
        rdata1 = cpuregs_rdata1,
        rdata2 = cpuregs_rdata2
    )

    @Com
    fun comCpuregs() {
        decoded_rs = ux()
        if (ENABLE_REGS_DUALPORT) {
            cpuregs_rs1 = if (decoded_rs1.orRed()) cpuregs_rdata1 else u0()
            cpuregs_rs2 = if (decoded_rs2.orRed()) cpuregs_rdata2 else u0()
        } else {
            decoded_rs = if (cpu_state == CPU_STATE_LD_RS2) decoded_rs2 else decoded_rs1
            cpuregs_rs1 = if (decoded_rs.orRed()) cpuregs_rdata1 else u0()
            cpuregs_rs2 = cpuregs_rs1
        }
    }

    @Com
    fun comLaunchNextInsn() {
        launch_next_insn = (cpu_state == CPU_STATE_FETCH) && decoder_trigger &&
            (!ENABLE_IRQ || irq_delay || irq_active || !(irq_pending and irq_mask.inv()))
    }

    @Seq
    fun seqCpuState() {
        on(posedge(clk)) {
            trap = false
            reg_sh = ux()
            reg_out = ux()
            set_mem_do_rinst = false
            set_mem_do_rdata = false
            set_mem_do_wdata = false

            alu_out_0_q = alu_out_0
            alu_out_q = alu_out

            alu_wait = false
            alu_wait_2 = false

            if (launch_next_insn) {
                dbg_rs1val = ux()
                dbg_rs2val = ux()
                dbg_rs1val_valid = false
                dbg_rs2val_valid = false
            }

            if (WITH_PCPI && CATCH_ILLINSN) {
                if (resetn && pcpi_valid && !pcpi_int_wait) {
                    if (pcpi_timeout_counter.neqz())
                        pcpi_timeout_counter -= u(1)
                } else {
                    pcpi_timeout_counter = u1()
                }
                pcpi_timeout = !pcpi_timeout_counter
            }

            if (ENABLE_COUNTERS) {
                count_cycle = if (resetn) count_cycle + u(1) else u0()
                if (!ENABLE_COUNTERS64) count_cycle[63, 32] = u0<`32`>()
            } else {
                count_cycle = ux()
                count_instr = ux()
            }

            next_irq_pending = if (ENABLE_IRQ) irq_pending and LATCHED_IRQ else ux()

            if (ENABLE_IRQ && ENABLE_IRQ_TIMER && timer.neqz()) {
                timer -= u(1)
            }

            decoder_trigger = mem_do_rinst && mem_done
            decoder_trigger_q = decoder_trigger
            decoder_pseudo_trigger = false
            decoder_pseudo_trigger_q = decoder_pseudo_trigger
            do_waitirq = false

            trace_valid = false
            if (!ENABLE_TRACE)
                trace_data = ux()

            if (!resetn) {
                reg_pc = PROGADDR_RESET
                reg_next_pc = PROGADDR_RESET
                if (ENABLE_TRACE)
                    count_instr = u0()
                latched_store = false
                latched_stalu = false
                latched_branch = false
                latched_trace = false
                latched_is_lu = false
                latched_is_lh = false
                latched_is_lb = false
                pcpi_valid = false
                pcpi_timeout = false
                irq_active = false
                irq_delay = false
                irq_mask = u1()
                next_irq_pending = u0()
                irq_state = u0()
                eoi = u0()
                timer = u0()
                if (STACKADDR.inv().neqz()) {
                    latched_store = true
                    latched_rd = u(2).ext()
                    reg_out = STACKADDR
                }
                cpu_state = CPU_STATE_FETCH
            } else when (cpu_state) {
                CPU_STATE_TRAP -> {
                    trap = true
                }

                CPU_STATE_FETCH -> {
                    mem_do_rinst = !decoder_trigger && !do_waitirq
                    mem_wordsize = u0()
                    current_pc = reg_next_pc

                    when {
                        latched_branch -> {
                            current_pc = if (latched_store) {
                                (if (latched_stalu) alu_out_q else reg_out) and u(1).ext<`32`>().inv()
                            } else reg_next_pc
                            println("ST_RD: ${latched_rd.toDecString()} 0x${reg_pc + if (latched_compr) u(0x2) else u(0x4)}, BRANCH 0x${current_pc}")
                        }
                        latched_store -> {
                            println("ST_RD: ${latched_rd.toDecString()} 0x${if (latched_stalu) alu_out_q else reg_out}")
                        }
                        ENABLE_IRQ && irq_state[0] -> {
                            current_pc = PROGADDR_IRQ
                            irq_active = true
                            mem_do_rinst = true
                        }
                        ENABLE_IRQ && irq_state[1] -> {
                            eoi = irq_pending and irq_mask.inv()
                            next_irq_pending = next_irq_pending and irq_mask
                        }
                    }

                    if (ENABLE_TRACE && latched_trace) {
                        latched_trace = false
                        trace_valid = true
                        trace_data = if (latched_branch) {
                            (if (irq_active) TRACE_IRQ else u0()) or TRACE_BRANCH or (current_pc and u("32'hfffffffe")).ext()
                        } else  {
                            (if (irq_active) TRACE_IRQ else u0()) or (if (latched_stalu) alu_out_q else reg_out).ext()
                        }
                    }

                    reg_pc = current_pc
                    reg_next_pc = current_pc

                    latched_store = false
                    latched_stalu = false
                    latched_branch = false
                    latched_is_lu = false
                    latched_is_lh = false
                    latched_is_lb = false
                    latched_rd = decoded_rd
                    latched_compr = compressed_instr

                    if (ENABLE_IRQ && ((decoder_trigger && !irq_active && !irq_delay && (irq_pending and irq_mask.inv()).orRed()) || irq_state.neqz())) {
                        irq_state = when (irq_state) {
                            u(0x00) -> u(0x01)
                            u(0x01) -> u(0x10)
                            else -> u(0x00)
                        }
                        latched_compr = latched_compr
                        latched_rd = if (ENABLE_IRQ_QREGS) {
                            IRQREGS_OFFSET or irq_state[0].toUbit()
                        } else {
                            if (irq_state[0]) u(0x4).ext() else u(0x3).ext()
                        }
                    } else if (ENABLE_IRQ && (decoder_trigger || do_waitirq) && instr_waitirq) {
                        if (irq_pending.neqz()) {
                            latched_store = true
                            reg_out = irq_pending
                            reg_next_pc = current_pc + if (compressed_instr) u(0x2) else u(0x4)
                            mem_do_rinst = true
                        } else {
                            do_waitirq = true
                        }
                    } else if (decoder_trigger) {
                        println("-- ${time()}")
                        irq_delay = irq_active
                        reg_next_pc = current_pc + if (compressed_instr) u(0x2) else u(0x4)
                        if (ENABLE_TRACE)
                            latched_trace = true
                        if (ENABLE_COUNTERS) {
                            count_instr += u(1)
                            if (!ENABLE_COUNTERS64) count_instr[63, 32] = u0<`32`>()
                        }
                        if (instr_jal) {
                            mem_do_rinst = true
                            reg_next_pc = current_pc + decoded_imm_j
                            latched_branch = true
                        } else {
                            mem_do_rinst = false
                            mem_do_prefetch = !instr_jalr && !instr_retirq
                            cpu_state = CPU_STATE_LD_RS1
                        }
                    }
                }

                CPU_STATE_LD_RS1 -> {
                    reg_op1 = ux()
                    reg_op2 = ux()

                    when {
                        (CATCH_ILLINSN || WITH_PCPI) && instr_trap -> {
                            if (WITH_PCPI) {
                                println("LD_RS1: ${decoded_rs1.toDecString()} 0x$cpuregs_rs1")
                                reg_op1 = cpuregs_rs1
                                dbg_rs1val = cpuregs_rs1
                                dbg_rs1val_valid = true
                                if (ENABLE_REGS_DUALPORT) {
                                    pcpi_valid = true
                                    println("LD_RS2: ${decoded_rs2.toDecString()} 0x$cpuregs_rs2")
                                    reg_sh = cpuregs_rs2.tru()
                                    reg_op2 = cpuregs_rs2
                                    dbg_rs2val = cpuregs_rs2
                                    dbg_rs2val_valid = true
                                    if (pcpi_int_ready) {
                                        mem_do_rinst = true
                                        pcpi_valid = false
                                        reg_out = pcpi_int_rd
                                        latched_store = pcpi_int_wr
                                        cpu_state = CPU_STATE_FETCH
                                    } else if (CATCH_ILLINSN && (pcpi_timeout || instr_ecall_ebreak)) {
                                        pcpi_valid = false
                                        println("EBREAK OR UNSUPPORTED INSN AT 0x$reg_pc")
                                        if (ENABLE_IRQ && !irq_mask[IRQ_EBREAK] && !irq_active) {
                                            next_irq_pending[IRQ_EBREAK] = true
                                            cpu_state = CPU_STATE_FETCH
                                        } else {
                                            cpu_state = CPU_STATE_TRAP
                                        }
                                    }
                                } else {
                                    cpu_state = CPU_STATE_LD_RS2
                                }
                            } else {
                                println("EBREAK OR UNSUPPORTED INSN AT 0x$reg_pc")
                                if (ENABLE_IRQ && !irq_mask[IRQ_EBREAK] && !irq_active) {
                                    next_irq_pending[IRQ_EBREAK] = true
                                    cpu_state = CPU_STATE_FETCH
                                } else {
                                    cpu_state = CPU_STATE_TRAP
                                }
                            }
                        }
                        ENABLE_COUNTERS && is_rdcycle_rdcycleh_rdinstr_rdinstrh -> {
                            reg_out = when {
                                instr_rdcycle -> count_cycle.tru()
                                instr_rdcycleh && ENABLE_COUNTERS64 -> count_cycle[63, 32]
                                instr_rdinstr -> count_instr.tru()
                                instr_rdinstrh && ENABLE_COUNTERS64 -> count_instr[63, 32]
                                else -> ux()
                            }
                            latched_store = true
                            cpu_state = CPU_STATE_FETCH
                        }
                        is_lui_auipc_jal -> {
                            reg_op1 = if (instr_lui) u0() else reg_pc
                            reg_op2 = decoded_imm
                            if (TWO_CYCLE_ALU)
                                alu_wait = true
                            else
                                mem_do_rinst = mem_do_prefetch
                            cpu_state = CPU_STATE_EXEC
                        }
                        ENABLE_IRQ && ENABLE_IRQ_QREGS && instr_getq -> {
                            println("LD_RS1: ${decoded_rs1.toDecString()} 0x$cpuregs_rs1")
                            reg_out = cpuregs_rs1
                            dbg_rs1val = cpuregs_rs1
                            dbg_rs1val_valid = true
                            latched_store = true
                            cpu_state = CPU_STATE_FETCH
                        }
                        ENABLE_IRQ && ENABLE_IRQ_QREGS && instr_setq -> {
                            println("LD_RS1: ${decoded_rs1.toDecString()} 0x$cpuregs_rs1")
                            reg_out = cpuregs_rs1
                            dbg_rs1val = cpuregs_rs1
                            dbg_rs1val_valid = true
                            latched_rd = latched_rd or IRQREGS_OFFSET
                            latched_store = true
                            cpu_state = CPU_STATE_FETCH
                        }
                        ENABLE_IRQ && instr_retirq -> {
                            eoi = u0()
                            irq_active = false
                            latched_branch = true
                            latched_store = true
                            println("LD_RS1: ${decoded_rs1.toDecString()} 0x$cpuregs_rs1")
                            reg_out = if (CATCH_MISALIGN) cpuregs_rs1 and u("32'hfffffffe") else cpuregs_rs1
                            dbg_rs1val = cpuregs_rs1
                            dbg_rs1val_valid = true
                            cpu_state = CPU_STATE_FETCH
                        }
                        ENABLE_IRQ && instr_maskirq -> {
                            latched_store = true
                            reg_out = irq_mask
                            println("LD_RS1: ${decoded_rs1.toDecString()} 0x$cpuregs_rs1")
                            irq_mask = cpuregs_rs1 or MASKED_IRQ
                            dbg_rs1val = cpuregs_rs1
                            dbg_rs1val_valid = true
                            cpu_state = CPU_STATE_FETCH
                        }
                        ENABLE_IRQ && ENABLE_IRQ_TIMER && instr_timer -> {
                            latched_store = true
                            reg_out = timer
                            println("LD_RS1: ${decoded_rs1.toDecString()} 0x$cpuregs_rs1")
                            timer = cpuregs_rs1
                            dbg_rs1val = cpuregs_rs1
                            dbg_rs1val_valid = true
                            cpu_state = CPU_STATE_FETCH
                        }
                        is_lb_lh_lw_lbu_lhu && !instr_trap -> {
                            println("LD_RS1: ${decoded_rs1.toDecString()} 0x$cpuregs_rs1")
                            reg_op1 = cpuregs_rs1
                            dbg_rs1val = cpuregs_rs1
                            dbg_rs1val_valid = true
                            cpu_state = CPU_STATE_LDMEM
                            mem_do_rinst = true
                        }
                        is_slli_srli_srai && !BARREL_SHIFTER -> {
                            println("LD_RS1: ${decoded_rs1.toDecString()} 0x$cpuregs_rs1")
                            reg_op1 = cpuregs_rs1
                            dbg_rs1val = cpuregs_rs1
                            dbg_rs1val_valid = true
                            reg_sh = decoded_rs2
                            cpu_state = CPU_STATE_SHIFT
                        }
                        is_jalr_addi_slti_sltiu_xori_ori_andi || (is_slli_srli_srai && BARREL_SHIFTER) -> {
                            println("LD_RS1: ${decoded_rs1.toDecString()} 0x$cpuregs_rs1")
                            reg_op1 = cpuregs_rs1
                            dbg_rs1val = cpuregs_rs1
                            dbg_rs1val_valid = true
                            reg_op2 = if (is_slli_srli_srai && BARREL_SHIFTER) decoded_rs2.ext() else decoded_imm
                            if (TWO_CYCLE_ALU)
                                alu_wait = true
                            else
                                mem_do_rinst = mem_do_prefetch
                            cpu_state = CPU_STATE_EXEC
                        }
                        else -> {
                            println("LD_RS1: ${decoded_rs1.toDecString()} 0x$cpuregs_rs1")
                            reg_op1 = cpuregs_rs1
                            dbg_rs1val = cpuregs_rs1
                            dbg_rs1val_valid = true
                            if (ENABLE_REGS_DUALPORT) {
                                println("LD_RS2: ${decoded_rs2.toDecString()} 0x$cpuregs_rs2")
                                reg_sh = cpuregs_rs2.tru()
                                reg_op2 = cpuregs_rs2
                                dbg_rs2val = cpuregs_rs2
                                dbg_rs2val_valid = true
                                when {
                                    is_sb_sh_sw -> {
                                        cpu_state = CPU_STATE_STMEM
                                        mem_do_rinst = true
                                    }
                                    is_sll_srl_sra && !BARREL_SHIFTER -> {
                                        cpu_state = CPU_STATE_SHIFT
                                    }
                                    else -> {
                                        if (TWO_CYCLE_ALU || (TWO_CYCLE_COMPARE && is_beq_bne_blt_bge_bltu_bgeu)) {
                                            alu_wait_2 = TWO_CYCLE_ALU && (TWO_CYCLE_COMPARE && is_beq_bne_blt_bge_bltu_bgeu)
                                            alu_wait = true
                                        } else {
                                            mem_do_rinst = mem_do_prefetch
                                        }
                                        cpu_state = CPU_STATE_EXEC
                                    }
                                }
                            } else {
                                cpu_state = CPU_STATE_LD_RS2
                            }
                        }
                    }
                }

                CPU_STATE_LD_RS2 -> {
                    println("LD_RS2: ${decoded_rs2.toDecString()} $cpuregs_rs2")
                    reg_sh = cpuregs_rs2.tru()
                    reg_op2 = cpuregs_rs2
                    dbg_rs2val = cpuregs_rs2
                    dbg_rs2val_valid = true

                    when {
                        WITH_PCPI && instr_trap -> {
                            pcpi_valid = true
                            if (pcpi_int_ready) {
                                mem_do_rinst = true
                                pcpi_valid = false
                                reg_out = pcpi_int_rd
                                latched_store = pcpi_int_wr
                                cpu_state = CPU_STATE_FETCH
                            } else if (CATCH_ILLINSN && (pcpi_timeout || instr_ecall_ebreak)) {
                                pcpi_valid = false
                                println("EBREAK OR UNSUPPORTED INSN AT 0x$reg_pc")
                                if (ENABLE_IRQ && !irq_mask[IRQ_EBREAK] && !irq_active) {
                                    next_irq_pending[IRQ_EBREAK] = true
                                    cpu_state = CPU_STATE_FETCH
                                } else {
                                    cpu_state = CPU_STATE_TRAP
                                }
                            }
                        }
                        is_sb_sh_sw -> {
                            cpu_state = CPU_STATE_STMEM
                            mem_do_rinst = true
                        }
                        is_sll_srl_sra && !BARREL_SHIFTER -> {
                            cpu_state = CPU_STATE_SHIFT
                        }
                        else -> {
                            if (TWO_CYCLE_ALU || (TWO_CYCLE_COMPARE && is_beq_bne_blt_bge_bltu_bgeu)) {
                                alu_wait_2 = TWO_CYCLE_ALU && (TWO_CYCLE_COMPARE && is_beq_bne_blt_bge_bltu_bgeu)
                                alu_wait = true
                            } else {
                                mem_do_rinst = mem_do_prefetch
                            }
                            cpu_state = CPU_STATE_EXEC
                        }
                    }
                }

                CPU_STATE_EXEC -> {
                    reg_out = reg_pc + decoded_imm
                    if ((TWO_CYCLE_ALU || TWO_CYCLE_COMPARE) && (alu_wait || alu_wait_2)) {
                        mem_do_rinst = mem_do_prefetch && !alu_wait_2
                        alu_wait = alu_wait_2
                    } else if (is_beq_bne_blt_bge_bltu_bgeu) {
                        latched_rd = u0()
                        latched_store = if (TWO_CYCLE_COMPARE) alu_out_0_q else alu_out_0
                        latched_branch = if (TWO_CYCLE_COMPARE) alu_out_0_q else alu_out_0
                        if (mem_done)
                            cpu_state = CPU_STATE_FETCH
                        if (if (TWO_CYCLE_COMPARE) alu_out_0_q else alu_out_0) {
                            decoder_trigger = false
                            set_mem_do_rinst = true
                        }
                    } else {
                        latched_branch = instr_jalr
                        latched_store = true
                        latched_stalu = true
                        cpu_state = CPU_STATE_FETCH
                    }
                }

                CPU_STATE_SHIFT -> {
                    latched_store = true
                    if (reg_sh.eqz()) {
                        reg_out = reg_op1
                        mem_do_rinst = mem_do_prefetch
                        cpu_state = CPU_STATE_FETCH
                    } else if (TWO_STAGE_SHIFT && reg_sh >= u(4).ext()) {
                        reg_op1 = when {
                            instr_slli || instr_sll -> reg_op1 shl 4
                            instr_srli || instr_srl -> reg_op1 shr 4
                            instr_srai || instr_sra -> reg_op1 sshr 4
                            else -> ux()
                        }
                        reg_sh -= u(4)
                    } else {
                        reg_op1 = when {
                            instr_slli || instr_sll -> reg_op1 shl 1
                            instr_srli || instr_srl -> reg_op1 shr 1
                            instr_srai || instr_sra -> reg_op1 sshr 1
                            else -> ux()
                        }
                        reg_sh -= u(1)
                    }
                }

                CPU_STATE_STMEM -> {
                    if (ENABLE_TRACE)
                        reg_out = reg_op2
                    if (!mem_do_prefetch || mem_done) {
                        if (!mem_do_wdata) {
                            mem_wordsize = when {
                                instr_sb -> u(0b10)
                                instr_sh -> u(0b01)
                                instr_sw -> u(0b00)
                                else -> ux()
                            }
                            if (ENABLE_TRACE) {
                                trace_valid = true
                                trace_data = (if (irq_active) TRACE_IRQ else u0()) or TRACE_ADDR or ((reg_op1 + decoded_imm) and u1<`32`>()).ext()
                            }
                            reg_op1 += decoded_imm
                            set_mem_do_wdata = true
                        }
                        if (!mem_do_prefetch && mem_done) {
                            cpu_state = CPU_STATE_FETCH
                            decoder_trigger = true
                            decoder_pseudo_trigger = true
                        }
                    }
                }

                CPU_STATE_LDMEM -> {
                    latched_store = true
                    if (!mem_do_prefetch || mem_done) {
                        if (!mem_do_rdata) {
                            mem_wordsize = when {
                                instr_lb || instr_lbu -> u(0b10)
                                instr_lh || instr_lhu -> u(0b01)
                                instr_lw -> u(0b00)
                                else -> ux()
                            }
                            latched_is_lu = is_lbu_lhu_lw
                            latched_is_lh = instr_lh
                            latched_is_lb = instr_lb
                            if (ENABLE_TRACE) {
                                trace_valid = true
                                trace_data = (if (irq_active) TRACE_IRQ else u0()) or TRACE_ADDR or ((reg_op1 + decoded_imm) and u1<`32`>()).ext()
                            }
                            reg_op1 += decoded_imm
                            set_mem_do_rdata = true
                        }
                        if (!mem_do_prefetch && mem_done) {
                            reg_out = when {
                                latched_is_lu -> mem_rdata_word
                                latched_is_lh -> mem_rdata_word.tru<`16`>().sext()
                                latched_is_lb -> mem_rdata_word.tru<`8`>().sext()
                                else -> ux()
                            }
                            decoder_trigger = true
                            decoder_pseudo_trigger = true
                            cpu_state = CPU_STATE_FETCH
                        }
                    }
                }
            }

            if (ENABLE_IRQ) {
                next_irq_pending = next_irq_pending or irq
                if (ENABLE_IRQ_TIMER && timer.neqz()) {
                    if (timer - u(1) == u0<`*`>()) {
                        next_irq_pending[IRQ_TIMER] = true
                    }
                }
            }

            if (CATCH_MISALIGN && resetn && (mem_do_rdata || mem_do_wdata)) {
                if (mem_wordsize == u(0b00) && reg_op1[1, 0].neqz()) {
                    println("MISALIGNED WORD: 0x$reg_op1")
                    if (ENABLE_IRQ && !irq_mask[IRQ_BUSERROR] && !irq_active)
                        next_irq_pending[IRQ_BUSERROR] = true
                    else
                        cpu_state = CPU_STATE_TRAP
                }
                if (mem_wordsize == u(0b01) && reg_op1[0]) {
                    println("MISALIGNED HALFWORD: 0x$reg_op1")
                    if (ENABLE_IRQ && !irq_mask[IRQ_BUSERROR] && !irq_active)
                        next_irq_pending[IRQ_BUSERROR] = true
                    else
                        cpu_state = CPU_STATE_TRAP
                }
            }
            if (CATCH_MISALIGN && resetn && mem_do_rinst && (if (COMPRESSED_ISA) reg_pc[0] else reg_pc[1, 0].orRed())) {
                println("MISALIGNED INSTRUCTION: 0x$reg_pc")
                if (ENABLE_IRQ && !irq_mask[IRQ_BUSERROR] && !irq_active)
                    next_irq_pending[IRQ_BUSERROR] = true
                else
                    cpu_state = CPU_STATE_TRAP
            }
            if (!CATCH_ILLINSN && decoder_trigger_q && !decoder_pseudo_trigger_q && instr_ecall_ebreak) {
                cpu_state = CPU_STATE_TRAP
            }

            if (!resetn || mem_done) {
                mem_do_prefetch = false
                mem_do_rinst = false
                mem_do_rdata = false
                mem_do_wdata = false
            }

            if (set_mem_do_rinst)
                mem_do_rinst = true
            if (set_mem_do_rdata)
                mem_do_rdata = true
            if (set_mem_do_wdata)
                mem_do_wdata = true

            irq_pending = next_irq_pending and MASKED_IRQ.inv()

            if (!CATCH_MISALIGN) {
                if (COMPRESSED_ISA) {
                    reg_pc[0] = false
                    reg_next_pc[0] = false
                } else {
                    reg_pc[1, 0] = u(0b00)
                    reg_next_pc[1, 0] = u(0b00)
                }
            }
            current_pc = ux()
        }
    }
}

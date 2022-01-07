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

class RV32<
    ENABLE_REGS_16_31 : `*`,
    ENABLE_MUL: `*`,
    ENABLE_FAST_MUL: `*`,
    ENABLE_DIV: `*`,
    ENABLE_IRQ : `*`,
    ENABLE_IRQ_QREGS : `*`
>(
    val ENABLE_COUNTERS: Boolean,
    val ENABLE_COUNTERS64: Boolean,
    val ENABLE_REGS_DUALPORT: Boolean,
    val LATCHED_MEM_RDATA: Boolean,
    val TWO_STAGE_SHIFT: Boolean,
    val BARREL_SHIFTER: Boolean,
    val TWO_CYCLE_COMPARE: Boolean,
    val TWO_CYCLE_ALU: Boolean,
    val COMPRESSED_ISA: Boolean,
    val CATCH_MISALIGN: Boolean,
    val CATCH_ILLINSN: Boolean,
    val ENABLE_PCPI: Boolean,
    val ENABLE_IRQ_TIMER: Boolean,
    val ENABLE_TRACE: Boolean,
    val REGS_INIT_ZERO: Boolean,
    val MASKED_IRQ: Ubit<`32`>,
    val LATCHED_IRQ: Ubit<`32`>,
    val PROGADDR_RESET: Ubit<`32`>,
    val PROGADDR_IRQ: Ubit<`32`>,
    val STACKADDR: Ubit<`32`>,

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

    val ENABLE_MUL = b<ENABLE_MUL>()
    val ENABLE_FAST_MUL = b<ENABLE_FAST_MUL>()
    val ENABLE_DIV = b<ENABLE_DIV>()

    val IRQ_TIMER = 0
    val IRQ_EBREAK = 1
    val IRQ_BUSERROR = 2

    val WITH_PCPI = ENABLE_PCPI || ENABLE_MUL || ENABLE_FAST_MUL || ENABLE_DIV

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

    var dbg_mem_valid: Boolean = nc()
    var dbg_mem_instr: Boolean = nc()
    var dbg_mem_ready: Boolean = nc()
    var dbg_mem_addr: Ubit<`32`> = nc()
    var dbg_mem_wdata: Ubit<`32`> = nc()
    var dbg_mem_wstrb: Ubit<`4`> = nc()
    var dbg_mem_rdata: Ubit<`32`> = nc()

    var next_pc: Ubit<`32`> = nc()

    var irq_delay: Boolean = nc()
    var irq_active: Boolean = nc()
    var irq_mask: Ubit<`32`> = nc()
    var irq_pending: Ubit<`32`> = nc()
    var irq_timer: Ubit<`32`> = nc()

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
        pcpi_int_wait = cat(
            ENABLE_PCPI && pcpi_wait,
            (ENABLE_MUL || ENABLE_FAST_MUL) && pcpi_mul_wait,
            ENABLE_DIV && pcpi_div_wait
        ).reduceOr()
        pcpi_int_ready = cat(
            ENABLE_PCPI && pcpi_ready,
            (ENABLE_MUL || ENABLE_FAST_MUL) && pcpi_mul_ready,
            ENABLE_DIV && pcpi_div_ready
        ).reduceOr()
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
    var mem_la_use_prefetched_high_word =
        COMPRESSED_ISA && mem_la_firstword && prefetched_high_word && !clear_prefetched_high_word

    @Com
    var mem_busy = cat(mem_do_prefetch, mem_do_rinst, mem_do_rdata, mem_do_wdata).reduceOr()
    @Com
    var mem_done = resetn &&
        ((mem_xfer && mem_state.reduceOr() && (mem_do_rinst || mem_do_rdata || mem_do_wdata)) ||
            (mem_state.reduceAnd() && mem_do_rinst)) &&
        (!mem_la_firstword || (!mem_rdata_latched.tru<`2`>().reduceAnd() && mem_xfer))

    @Com
    fun comMem() {
        mem_xfer = (mem_valid && mem_ready) || (mem_la_use_prefetched_high_word && mem_do_rinst)
        mem_la_write = resetn && !mem_state && mem_do_wdata
        mem_la_read = resetn &&
            ((!mem_la_use_prefetched_high_word && !mem_state && (mem_do_rinst || mem_do_prefetch || mem_do_rdata)) ||
                (COMPRESSED_ISA && mem_xfer && (if (!last_mem_valid) mem_la_firstword else mem_la_firstword_reg) &&
                    !mem_la_secondword && mem_rdata_latched.tru<`2`>().reduceAnd()))
        mem_la_addr =
            if (mem_do_prefetch || mem_do_rinst) cat(next_pc.slice<`30`, `2`>() + u(mem_la_firstword_xfer), u(0b00))
            else cat(reg_op1.slice<`30`, `2`>(), u(0b00))
        mem_rdata_latched_noshuffle = if (mem_xfer || LATCHED_MEM_RDATA) mem_rdata else mem_rdata_q
        mem_rdata_latched = when {
            COMPRESSED_ISA && mem_la_use_prefetched_high_word -> cat(u("16'bx"), mem_16bit_buffer)
            COMPRESSED_ISA && mem_la_secondword -> cat(mem_rdata_latched_noshuffle.tru<`16`>(), mem_16bit_buffer)
            COMPRESSED_ISA && mem_la_firstword -> cat(u("16'bx"), mem_rdata_latched_noshuffle.slice<`16`, `16`>())
            else -> mem_rdata_latched_noshuffle
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
    var instr_bgq: Boolean = nc()
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

    var is_rdcycle_rdcycleh_rdinstr_rdinstrh: Boolean = nc()

    // Main State Machine

    val CPU_STATE_TRAP = u("8'b10000000")
    val CPU_STATE_FETCH = u("8'b01000000")
    val CPU_STATE_LD_RS1 = u("8'b00100000")
    val CPU_STATE_LD_RS2 = u("8'b00010000")
    val CPU_STATE_EXEC = u("8'b00001000")
    val CPU_STATE_SHIFT = u("8'b00000100")
    val CPU_STATE_STMEM = u("8'b00000010")
    val CPU_STATE_LDMEM = u("8'b00000001")

    var cpu_state: Ubit<`8`> = nc()
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

    var clear_prefetched_high_word_q: Boolean = nc()

    var cpuregs_write: Boolean = nc()
    var cpuregs_wrdata: Ubit<`32`> = nc()
    var cpuregs_rs1: Ubit<`32`> = nc()
    var cpuregs_rs2: Ubit<`32`> = nc()
    var decoded_rs: Ubit<REGINDEX_BITS<ENABLE_REGS_16_31, ENABLE_IRQ, ENABLE_IRQ_QREGS>> = nc()

    var cpuregs_rdata1: Ubit<`32`> = nc()
    var cpuregs_rdata2: Ubit<`32`> = nc()

    var cpuregs_waddr: Ubit<`6`> = nc()
    var cpuregs_raddr1: Ubit<`6`> = nc()
    var cpuregs_raddr2: Ubit<`6`> = nc()

    @Make
    val cpuregs = RV32Regs(
        clk = clk,
        wen = false,
        waddr = u0(),
        raddr1 = u0(),
        raddr2 = u0(),
        wdata = u0(),
        rdata1 = nc(),
        rdata2 = nc()
    )
}

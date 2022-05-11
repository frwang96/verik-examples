/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

package fft

import imported.*
import io.verik.core.*

@Entry
class FpgaTop(
    @In var clk_100mhz: Boolean,
    @In var sw: Ubit<`16`>,
    @Out var led: Ubit<`16`>,
    @In var vauxn3: Boolean,
    @In var vauxp3: Boolean,
    @Out var vga_r: Ubit<`4`>,
    @Out var vga_g: Ubit<`4`>,
    @Out var vga_b: Ubit<`4`>,
    @Out var vga_hs: Boolean,
    @Out var vga_vs: Boolean
) : Module() {

    var pixel_clk: Boolean = nc()

    @Make
    val clk_gen = VgaClkGen(
        clk_out1 = pixel_clk,
        reset = false,
        locked = nc(),
        clk_in1 = clk_100mhz
    )

    var adc_data: Ubit<`16`> = nc()

    @Make
    val fft_adc = FftAdc(
        di_in = u0(),
        daddr_in = u("7'h13"),
        den_in = true,
        dwe_in = false,
        drdy_out = nc(),
        do_out = adc_data,
        dclk_in = clk_100mhz,
        vp_in = true,
        vn_in = true,
        vauxp3 = vauxp3,
        vauxn3 = vauxn3,
        channel_out = nc(),
        eoc_out = nc(),
        alarm_out = nc(),
        eos_out = nc(),
        busy_out = nc()
    )

    val SAMPLE_COUNT = u(4164)

    var sample_counter: Ubit<`13`> = nc()

    @Com
    var sample_trigger = sample_counter == SAMPLE_COUNT

    var scaled_adc_data: Ubit<`16`> = nc()
    var scaled_signed_adc_data: Ubit<`16`> = nc()

    var fft_data: Ubit<`16`> = nc()
    var fft_data_counter: Ubit<`10`> = nc()
    var fft_valid: Boolean = nc()
    var fft_last: Boolean = nc()
    var fft_ready: Boolean = nc()

    @Seq
    fun seqFft() {
        on(posedge(clk_100mhz)) {
            if (sample_trigger) sample_counter = u0()
            else sample_counter += u(1)

            if (sample_trigger) {
                scaled_adc_data = adc_data shl 4
                scaled_signed_adc_data = cat(!scaled_adc_data[15], scaled_adc_data.tru<`15`>())
                if (fft_ready) {
                    fft_data_counter += u(1)
                    fft_last = fft_data_counter == u(1023)
                    fft_valid = true
                    fft_data = scaled_signed_adc_data
                }
            } else {
                fft_data = u0()
                fft_last = false
                fft_valid = false
            }
        }
    }

    var fft_out_data: Ubit<`32`> = nc()
    var fft_out_valid: Boolean = nc()
    var fft_out_last: Boolean = nc()
    var fft_out_ready: Boolean = nc()

    @Make
    val fft_core = FftCore(
        aclk = clk_100mhz,
        s_axis_config_tdata = u0(),
        s_axis_config_tvalid = false,
        s_axis_config_tready = nc(),
        s_axis_data_tdata = cat(fft_data, u0<`16`>()),
        s_axis_data_tvalid = fft_valid,
        s_axis_data_tlast = fft_last,
        s_axis_data_tready = fft_ready,
        m_axis_data_tdata = fft_out_data,
        m_axis_data_tvalid = fft_out_valid,
        m_axis_data_tready = fft_out_ready,
        m_axis_data_tlast = fft_out_last,
        event_frame_started = nc(),
        event_tlast_unexpected = nc(),
        event_tlast_missing = nc(),
        event_status_channel_halt = nc(),
        event_data_in_channel_halt = nc(),
        event_data_out_channel_halt = nc()
    )

    var sqsum_data: Ubit<`32`> = nc()
    var sqsum_valid: Boolean = nc()
    var sqsum_last: Boolean = nc()
    var sqsum_ready: Boolean = nc()

    @Make
    val square_and_sum = SquareAndSum<`32`>(
        s00_axis_aclk = clk_100mhz,
        s00_axis_aresetn = true,
        s00_axis_tready = fft_out_ready,
        s00_axis_tdata = fft_out_data,
        s00_axis_tlast = fft_out_last,
        s00_axis_tvalid = fft_out_valid,
        m00_axis_aclk = clk_100mhz,
        m00_axis_aresetn = true,
        m00_axis_tvalid = sqsum_valid,
        m00_axis_tdata = sqsum_data,
        m00_axis_tlast = sqsum_last,
        m00_axis_tready = sqsum_ready
    )

    var fifo_data: Ubit<`32`> = nc()
    var fifo_valid: Boolean = nc()
    var fifo_last: Boolean = nc()
    var fifo_ready: Boolean = nc()

    @Make
    val fft_fifo = FftFifo(
        s_axis_aresetn = true,
        s_axis_aclk = clk_100mhz,
        s_axis_tvalid = sqsum_valid,
        s_axis_tready = sqsum_ready,
        s_axis_tdata = sqsum_data,
        s_axis_tlast = sqsum_last,
        m_axis_tvalid = fifo_valid,
        m_axis_tready = fifo_ready,
        m_axis_tdata = fifo_data,
        m_axis_tlast = fifo_last
    )

    var sqrt_data: Ubit<`24`> = nc()
    var sqrt_valid: Boolean = nc()
    var sqrt_last: Boolean = nc()

    @Make
    val fft_sqrt = FftSqrt(
        aclk = clk_100mhz,
        s_axis_cartesian_tvalid = fifo_valid,
        s_axis_cartesian_tready = fifo_ready,
        s_axis_cartesian_tlast = fifo_last,
        s_axis_cartesian_tdata = fifo_data,
        m_axis_dout_tvalid = sqrt_valid,
        m_axis_dout_tlast = sqrt_last,
        m_axis_dout_tdata = sqrt_data
    )

    var addr_count: Ubit<`10`> = nc()
    var draw_addr: Ubit<`10`> = nc()
    var amp_out: Ubit<`32`> = nc()

    @Seq
    fun seqAddrCount() {
        on(posedge(clk_100mhz)) {
            if (sqrt_valid) {
                if (sqrt_last) addr_count = u(1023)
                else addr_count += u(1)
            }
        }
    }

    @Make
    val fft_blk_mem = FftBlkMem(
        clka = clk_100mhz,
        ena = true,
        wea = sqrt_valid.toUbit(),
        addra = addr_count + u(3),
        dina = cat(u0<`8`>(), sqrt_data),
        douta = nc(),
        clkb = pixel_clk,
        enb = true,
        web = u(0b0),
        addrb = draw_addr,
        dinb = u0(),
        doutb = amp_out
    )

    var hcount: Ubit<`11`> = nc()
    var vcount: Ubit<`10`> = nc()
    var hsync: Boolean = nc()
    var vsync: Boolean = nc()
    var blanking: Boolean = nc()

    @Make
    val vga_signal_gen = VgaSignalGen(
        vclock_in = pixel_clk,
        hcount_out = hcount,
        vcount_out = vcount,
        hsync_out = hsync,
        vsync_out = vsync,
        blank_out = blanking
    )

    var rgb: Ubit<`12`> = nc()

    @Seq
    fun seqRgb() {
        on(posedge(pixel_clk)) {
            draw_addr = (hcount shr 1).tru()
            rgb = if (amp_out shr sw.tru<`4`>() >= (u(768) - vcount).ext()) sw[15, 4] else u0()
        }
    }

    @Com
    fun comOutput() {
        led = sw
        vga_r = if (!blanking) rgb[11, 8] else u0()
        vga_g = if (!blanking) rgb[7, 4] else u0()
        vga_b = if (!blanking) rgb[3, 0] else u0()
        vga_hs = !hsync
        vga_vs = !vsync
    }
}

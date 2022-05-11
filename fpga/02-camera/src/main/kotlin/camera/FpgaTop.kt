/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

package camera

import imported.CameraBlkMem
import imported.VgaClkGen
import io.verik.core.*

@Entry
class FpgaTop(
    @In var clk_100mhz: Boolean,
    @In var ja: Ubit<`8`>,
    @In var jb: Ubit<`3`>,
    @Out var jbclk: Boolean,
    @Out var vga_r: Ubit<`4`>,
    @Out var vga_b: Ubit<`4`>,
    @Out var vga_g: Ubit<`4`>,
    @Out var vga_hs: Boolean,
    @Out var vga_vs: Boolean
) : Module() {

    var clk_65mhz: Boolean = nc()

    @Make
    val clk_gen = VgaClkGen(
        clk_out1 = clk_65mhz,
        reset = false,
        locked = nc(),
        clk_in1 = clk_100mhz
    )

    var hcount: Ubit<`11`> = nc()
    var vcount: Ubit<`10`> = nc()
    var hsync: Boolean = nc()
    var vsync: Boolean = nc()
    var blank: Boolean = nc()

    @Make
    val vga_signal_gen = VgaSignalGen(
        vclock_in = clk_65mhz,
        hcount_out = hcount,
        vcount_out = vcount,
        hsync_out = hsync,
        vsync_out = vsync,
        blank_out = blank
    )

    var pclk_buff: Boolean = nc()
    var pclk_in: Boolean = nc()
    var vsync_buff: Boolean = nc()
    var vsync_in: Boolean = nc()
    var href_buff: Boolean = nc()
    var href_in: Boolean = nc()
    var pixel_buff: Ubit<`8`> = nc()
    var pixel_in: Ubit<`8`> = nc()

    var xclk_count: Ubit<`2`> = nc()

    @Seq
    fun seqBufferInput() {
        on(posedge(clk_65mhz)) {
            pclk_buff = jb[0]
            vsync_buff = jb[1]
            href_buff = jb[2]
            pixel_buff = ja

            pclk_in = pclk_buff
            vsync_in = vsync_buff
            href_in = href_buff
            pixel_in = pixel_buff

            xclk_count += u(1)
        }
    }

    @Com
    fun comJbclk() {
        jbclk = xclk_count > u(0b01)
    }

    var output_pixels: Ubit<`16`> = nc()
    var valid_pixel: Boolean = nc()
    var frame_done_out: Boolean = nc()

    @Make
    val camera_read = CameraRead(
        p_clk_in = pclk_in,
        vsync_in = vsync_in,
        href_in = href_in,
        p_data_in = pixel_in,
        pixel_data_out = output_pixels,
        pixel_valid_out = valid_pixel,
        frame_done_out = frame_done_out
    )

    var pixel_addr_in: Ubit<`17`> = nc()

    @Seq
    fun seqPixelAddr() {
        on(posedge(pclk_in)) {
            pixel_addr_in = when {
                frame_done_out -> u0()
                valid_pixel -> pixel_addr_in + u(1)
                else -> pixel_addr_in
            }
        }
    }

    @Com
    var pixel_addr_out: Ubit<`17`> = (hcount shr 1) + ((vcount shr 1) mul u(320)).tru<`17`>()

    @Com
    var processed_pixels: Ubit<`12`> = cat(
        output_pixels[15, 12],
        output_pixels[10, 7],
        output_pixels[4, 1]
    )

    var frame_buff_out: Ubit<`12`> = nc()

    @Make
    val blk_mem = CameraBlkMem(
        addra = pixel_addr_in,
        clka = pclk_in,
        dina = processed_pixels,
        wea = valid_pixel.toUbit(),
        addrb = pixel_addr_out,
        clkb = clk_65mhz,
        doutb = frame_buff_out
    )

    var rgb_out: Ubit<`12`> = nc()
    var blank_out: Boolean = nc()
    var hsync_out: Boolean = nc()
    var vsync_out: Boolean = nc()

    @Seq
    fun seqOutput() {
        on(posedge(clk_65mhz)) {
            hsync_out = hsync
            vsync_out = vsync
            blank_out = blank
            rgb_out = if (hcount < u("11'd640") && vcount < u("10'd480")) frame_buff_out else u0()
        }
    }

    @Com
    fun comOutput() {
        vga_r = if (!blank_out) rgb_out[11, 8] else u0()
        vga_g = if (!blank_out) rgb_out[7, 4] else u0()
        vga_b = if (!blank_out) rgb_out[3, 0] else u0()
        vga_hs = !hsync_out
        vga_vs = !vsync_out
    }
}

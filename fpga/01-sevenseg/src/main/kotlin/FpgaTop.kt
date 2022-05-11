/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

@Entry
class FpgaTop(
    @In var clk_100mhz: Boolean,
    @In var sw: Ubit<`16`>,
    @Out var led: Ubit<`16`>,
    @Out var led16_b: Boolean,
    @Out var led16_g: Boolean,
    @Out var led16_r: Boolean,
    @Out var ca: Boolean,
    @Out var cb: Boolean,
    @Out var cc: Boolean,
    @Out var cd: Boolean,
    @Out var ce: Boolean,
    @Out var cf: Boolean,
    @Out var cg: Boolean,
    @Out var dp: Boolean,
    @Out var an: Ubit<`8`>
) : Module() {

    var seg: Ubit<`7`> = nc()

    @Make
    val seven_segment = SevenSegment(
        digit = sw.tru(),
        seg = seg
    )

    @Com
    fun comOutput() {
        led = sw
        led16_b = false
        led16_g = false
        led16_r = false
        ca = seg[0]
        cb = seg[1]
        cc = seg[2]
        cd = seg[3]
        ce = seg[4]
        cf = seg[5]
        cg = seg[6]
        dp = true
        an = u0()
    }
}

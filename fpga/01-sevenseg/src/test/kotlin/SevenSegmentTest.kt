/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

@Entry
object SevenSegmentTest : Module() {

    var digit: Ubit<`4`> = nc()
    var seg: Ubit<`7`> = nc()

    @Make
    val seven_segment = SevenSegment(
        digit = digit,
        seg = seg
    )

    @Run
    fun test() {
        var expected: Unpacked<`16`, Ubit<`7`>> = nc()
        expected[0x0] = u(0b1000000)
        expected[0x1] = u(0b1111001)
        expected[0x2] = u(0b0100100)
        expected[0x3] = u(0b0110000)
        expected[0x4] = u(0b0011001)
        expected[0x5] = u(0b0010010)
        expected[0x6] = u(0b0000010)
        expected[0x7] = u(0b1111000)
        expected[0x8] = u(0b0000000)
        expected[0x9] = u(0b0010000)
        expected[0xa] = u(0b0001000)
        expected[0xb] = u(0b0000011)
        expected[0xc] = u(0b1000110)
        expected[0xd] = u(0b0100001)
        expected[0xe] = u(0b0000110)
        expected[0xf] = u(0b0001110)

        digit = u0()
        for (i in 0 until 16) {
            delay(10)
            println("digit=$digit seg=$seg")
            assert(seg == expected[digit]) {
                error("ERROR: incorrect output for $digit: expected ${expected[digit]} actual $seg")
            }
            digit += u(1)
        }
        finish()
    }
}

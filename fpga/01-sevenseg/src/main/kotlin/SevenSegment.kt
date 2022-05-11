/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

class SevenSegment(
    @In var digit: Ubit<`4`>,
    @Out var seg: Ubit<`7`>
) : Module() {

    @Com
    fun comSeg() {
        seg = when(digit) {
            u(0x0) -> u(0b1000000)
            u(0x1) -> u(0b1111001)
            u(0x2) -> u(0b0100100)
            u(0x3) -> u(0b0110000)
            u(0x4) -> u(0b0011001)
            u(0x5) -> u(0b0010010)
            u(0x6) -> u(0b0000010)
            u(0x7) -> u(0b1111000)
            u(0x8) -> u(0b0000000)
            u(0x9) -> u(0b0010000)
            u(0xa) -> u(0b0001000)
            u(0xb) -> u(0b0000011)
            u(0xc) -> u(0b1000110)
            u(0xd) -> u(0b0100001)
            u(0xe) -> u(0b0000110)
            else -> u(0b0001110)
        }
    }
}

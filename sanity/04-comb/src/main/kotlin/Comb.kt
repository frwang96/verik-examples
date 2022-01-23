/*
 * Copyright (c) 2021 Francis Wang
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

import io.verik.core.*

fun bitScanReverse(a: Ubit<`4`>): Ubit<`2`> {
    var ret = u(0b00)
    ret[0] = or1(a[3], and1(not1(a[2]), a[1]))
    ret[1] = or1(a[3], a[2])
    return ret
}

fun isPowerOfTwo(a: Ubit<`4`>): Boolean {
    return or1(
        and1(xor1(a[0], a[1]), and1(not1(a[2]), not1(a[3]))),
        and1(xor1(a[2], a[3]), and1(not1(a[0]), not1(a[1])))
    )
}

fun mux2(sel: Boolean, a: Ubit<`2`>, b:Ubit<`2`>): Ubit<`2`> {
    return cat(mux1(sel, a[1], b[1]), mux1(sel, a[0], b[0]))
}

fun logPowerOfTwo(a: Ubit<`4`>): Ubit<`2`> {
    return mux2(isPowerOfTwo(a), u0(), bitScanReverse(a))
}

fun eq1(a: Boolean, b: Boolean): Boolean {
    return not1(xor1(a, b))
}

fun eq2(a: Ubit<`2`>, b: Ubit<`2`>): Boolean {
    return and1(eq1(a[0], b[0]), eq1(a[1], b[1]))
}

fun equal(a: Ubit<`4`>, b: Ubit<`4`>): Boolean {
    return and1(eq2(a[1, 0], b[1, 0]), eq2(a[3, 2], b[3, 2]))
}

fun vectorEqual(a: Ubit<`16`>, b: Ubit<`16`>): Ubit<`4`> {
    return cat(
        equal(a[15, 12], b[15, 12]),
        equal(a[11, 8], b[11, 8]),
        equal(a[7, 4], b[7, 4]),
        equal(a[3, 0], b[3, 0])
    )
}

fun sevenSegmentDecoder(a: Ubit<`4`>): Ubit<`7`> {
    return when (a) {
        u(0x0) -> u(0b1111110)
        u(0x1) -> u(0b0110000)
        u(0x2) -> u(0b1101101)
        u(0x3) -> u(0b1111001)
        u(0x4) -> u(0b0110011)
        u(0x5) -> u(0b1011011)
        u(0x6) -> u(0b1011111)
        u(0x7) -> u(0b1110000)
        u(0x8) -> u(0b1111111)
        u(0x9) -> u(0b1111011)
        else -> u(0b1001111)
    }
}

fun ha(a: Boolean, b: Boolean): Ubit<`2`> {
    return cat(and1(a, b), xor1(a, b))
}

fun fa(a: Boolean, b: Boolean, c: Boolean): Ubit<`2`> {
    val out1 = ha(a, b)
    val out2 = ha(out1[0], c)
    return cat(or1(out1[1], out2[1]), out2[0])
}

fun populationCount(a: Ubit<`4`>): Ubit<`3`> {
    val out1 = fa(a[0], a[1], a[2])
    val out2 = ha(out1[0], a[3])
    val out3 = ha(out1[1], out2[1])
    return cat(out3, out2[0])
}

fun isGeq(a: Ubit<`4`>, b: Ubit<`4`>): Boolean {
    var ret = true
    ret = mux1(eq1(a[0], b[0]), a[0], ret)
    ret = mux1(eq1(a[1], b[1]), a[1], ret)
    ret = mux1(eq1(a[2], b[2]), a[2], ret)
    ret = mux1(eq1(a[3], b[3]), a[3], ret)
    return ret
}

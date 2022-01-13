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

enum class ShiftType {
    LEFT_LOGICAL,
    RIGHT_LOGICAL,
    RIGHT_ARITHMETIC
}

enum class AluFunc {
    ADD,
    SUB,
    AND,
    OR,
    XOR,
    SLT,
    SLTU,
    SLL,
    SRL,
    SRA
}

fun barrelRShift(a: Ubit<`32`>, b: Ubit<`5`>, sftOnes: Boolean): Ubit<`32`> {
    var x = a
    if (b[0]) x = cat(sftOnes.sext<`1`>(), x.sli<`31`>(1))
    if (b[1]) x = cat(sftOnes.sext<`2`>(), x.sli<`30`>(2))
    if (b[2]) x = cat(sftOnes.sext<`4`>(), x.sli<`28`>(4))
    if (b[3]) x = cat(sftOnes.sext<`8`>(), x.sli<`24`>(8))
    if (b[4]) x = cat(sftOnes.sext<`16`>(), x.sli<`16`>(16))
    return x
}

fun sr32(a: Ubit<`32`>, b: Ubit<`5`>, arith: Boolean): Ubit<`32`> {
    return barrelRShift(a, b, if(arith) a[31] else false)
}

fun sll32(a: Ubit<`32`>, b: Ubit<`5`>): Ubit<`32`> {
    return barrelRShift(a.rev(), b, false).rev()
}

fun sft32(a: Ubit<`32`>, b: Ubit<`5`>, shiftType: ShiftType): Ubit<`32`> {
    val x = barrelRShift(
        if (shiftType == ShiftType.LEFT_LOGICAL) a.rev() else a,
        b,
        if (shiftType == ShiftType.RIGHT_ARITHMETIC) a[31] else false
    )
    return if (shiftType == ShiftType.LEFT_LOGICAL) x.rev() else x
}

fun cmp(a: Boolean, b: Boolean, eq: Boolean, lt: Boolean): Ubit<`2`> {
    val ret: Ubit<`2`> = u0()
    ret[0] = (!a && b) || ((a == b) && lt)
    ret[1] = (a == b) && eq
    return ret
}

fun ltu32(a: Ubit<`32`>, b: Ubit<`32`>): Boolean {
    var cmpResult = u(0b10)
    for (i in 0 until 32) {
        cmpResult = cmp(a[i], b[i], cmpResult[1], cmpResult[0])
    }
    return cmpResult[0]
}

fun lt32(a: Ubit<`32`>, b: Ubit<`32`>, isSigned: Boolean): Boolean {
    val aIn = if (isSigned) cat(!a[31], a.tru<`31`>()) else a
    val bIn = if (isSigned) cat(!b[31], b.tru<`31`>()) else b
    return ltu32(aIn, bIn)
}

fun fa(a: Boolean, b: Boolean, c: Boolean): Ubit<`2`> {
    val ret: Ubit<`2`> = u0()
    ret[0] = a xor b xor c
    ret[1] = (a && b) || (a && c) || (b && c)
    return ret
}

fun <N : `*`> rca(a: Ubit<N>, b: Ubit<N>, c: Boolean): Ubit<N> {
    var carry = c
    val ret: Ubit<N> = u0()
    for (i in 0 until i<N>()) {
        val faResult = fa(a[i], b[i], carry)
        carry = faResult[1]
        ret[i] = faResult[0]
    }
    return ret
}

fun addSub(a: Ubit<`32`>, b: Ubit<`32`>, isSub: Boolean): Ubit<`32`> {
    return rca<`32`>(a, if (isSub) b.inv() else b, isSub)
}

fun alu(a: Ubit<`32`>, b: Ubit<`32`>, func: AluFunc): Ubit<`32`> {
    return when (func) {
        AluFunc.ADD, AluFunc.SUB -> addSub(a, b, func == AluFunc.SUB)
        AluFunc.AND -> a and b
        AluFunc.OR -> a or b
        AluFunc.XOR -> a xor b
        AluFunc.SLT, AluFunc.SLTU -> cat(u("31'h0"), lt32(a, b, func == AluFunc.SLT))
        else -> {
            val shiftType = when (func) {
                AluFunc.SLL -> ShiftType.LEFT_LOGICAL
                AluFunc.SRL -> ShiftType.RIGHT_LOGICAL
                else -> ShiftType.RIGHT_ARITHMETIC
            }
            sft32(a, b.tru(), shiftType)
        }
    }
}

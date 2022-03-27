/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

class MultiplierInput(
    val a: Ubit<`32`>,
    val b: Ubit<`32`>
) : Struct()

fun multiplyByAdding(a: Ubit<`32`>, b: Ubit<`32`>): Ubit<`64`> {
    var res: Ubit<`64`> = u0()
    for (i in 0 until 32) {
        res = res shl 1
        if (a[31-i]) res += b
    }
    return res
}

class FoldedMultiplier(
    @In var clk: Boolean,
    @In var mul_in: MultiplierInput,
    @In var mul_in_valid: Boolean,
    @Out var res: Ubit<`64`>,
    @Out var res_valid: Boolean
) : Module() {

    var a: Ubit<`32`> = nc()
    var b: Ubit<`32`> = nc()
    var prod: Ubit<`32`> = nc()
    var tp: Ubit<`32`> = nc()
    var i: Ubit<WIDTH<`32`>> = nc()

    @Seq
    fun compute() {
        on(posedge(clk)) {
            if (mul_in_valid) {
                a = mul_in.a
                b = mul_in.b
                prod = u0()
                tp = u0()
                i = u(32)
            } else {
                val m = prod add (if (a[0]) b else u0())
                a = a shr 1
                tp = cat(m[0], tp[31, 1])
                prod = m[32, 1]
                i -= u(1)
            }
        }
    }

    @Com
    fun comRes() {
        res = cat(prod, tp)
        res_valid = (i == u0<`*`>())
    }
}

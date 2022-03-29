/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

class Adder<N : `*`>(
    @In var a: Ubit<N>,
    @In var b: Ubit<N>,
    @Out var x: Ubit<N>
) : Module() {

    val N = i<N>()

    fun fullAdder(a: Boolean, b: Boolean, c: Boolean): Ubit<`2`> {
        var x: Ubit<`2`> = u0()
        x[0] = a xor b xor c
        x[1] = (a && b) || (a && c) || (b && c)
        return x
    }

    @Com
    fun comOutput() {
        var c = false
        for (i in 0 until N) {
            val fa = fullAdder(a[i], b[i], c)
            x[i] = fa[0]
            c = fa[1]
        }
    }
}

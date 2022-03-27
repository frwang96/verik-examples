/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

class Adder<X : `*`>(
    @In var a: Ubit<X>,
    @In var b: Ubit<X>,
    @Out var x: Ubit<X>
) : Module() {

    fun fullAdder(a: Boolean, b: Boolean, c: Boolean): Ubit<`2`> {
        var x: Ubit<`2`> = u0()
        x[0] = a xor b xor c
        x[1] = (a && b) || (a && c) || (b && c)
        return x
    }

    @Com
    fun comOutput() {
        var c = false
        for (i in 0 until i<X>()) {
            val fa = fullAdder(a[i], b[i], c)
            x[i] = fa[0]
            c = fa[1]
        }
    }
}

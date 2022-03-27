/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

class RandRange(val max: Ubit<`8`>) : Class() {

    @Randc
    var value: Ubit<`8`> = u0()

    @Cons
    val c_max = c(value < max)
}

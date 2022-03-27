/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik

import io.verik.core.*

class LookupTable<SIZE: `*`, TYPE> : ModuleInterface() {

    val mem: Unpacked<EXP<SIZE>, TYPE> = nc()

    fun write(addr: Ubit<OF<SIZE>>, data: TYPE) {
        mem[addr] = data
    }

    fun read(addr: Ubit<OF<SIZE>>): TYPE {
        return mem[addr]
    }
}

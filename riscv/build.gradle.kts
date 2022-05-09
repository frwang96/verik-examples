/*
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    kotlin("jvm")
    id("io.verik.verik-plugin")
}

verik {
    dsim {
        compileTops = listOf("RV32BasicTest")
        sim {
            name = "sim"
        }
    }
}

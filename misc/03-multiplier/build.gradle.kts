/*
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    kotlin("jvm")
    id("io.verik.verik-plugin")
}

verik {
    dsim {
        compileTops = listOf("MultiplyFunctionTest", "MultiplyModuleTest")
        sim {
            name = "simFunction"
            simTop = "MultiplyFunctionTest"
        }
        sim {
            name = "simModule"
            simTop = "MultiplyModuleTest"
        }
    }
}

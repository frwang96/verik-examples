/*
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    kotlin("jvm")
    id("io.verik.verik-plugin")
}

verik {
    vivado {
        part = "xc7a100tcsg324-3"
        simTop = "SevenSegmentTest"
        synthTop = "FpgaTop"
        constraintsFile = projectDir.toPath().resolve("constraints.xdc")
    }
}

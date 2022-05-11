/*
 * SPDX-License-Identifier: Apache-2.0
 */

val ipNames = listOf(
    "FftAdc",
    "FftBlkMem",
    "FftCore",
    "FftFifo",
    "FftSqrt",
    "VgaClkGen"
)

plugins {
    kotlin("jvm")
    id("io.verik.verik-plugin")
}

verik {
    import {
        importedFiles = ipNames.map { projectDir.toPath().resolve("ip/$it.v") }
    }
    vivado {
        part = "xc7a100tcsg324-3"
        synthTop = "FpgaTop"
        constraintsFile = projectDir.toPath().resolve("constraints.xdc")
        ipConfigFiles = ipNames.map { projectDir.toPath().resolve("ip/$it.xci") }
    }
}

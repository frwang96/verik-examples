/*
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    kotlin("jvm")
    id("io.verik.verik-plugin")
}

verik {
    import {
        importedFiles = listOf(
            projectDir.toPath().resolve("ip/CameraBlkMem.v"),
            projectDir.toPath().resolve("ip/VgaClkGen.v")
        )
    }
    vivado {
        part = "xc7a100tcsg324-3"
        synthTop = "FpgaTop"
        constraintsFile = projectDir.toPath().resolve("constraints.xdc")
        ipConfigFiles = listOf(
            projectDir.toPath().resolve("ip/CameraBlkMem.xci"),
            projectDir.toPath().resolve("ip/VgaClkGen.xci")
        )
    }
}

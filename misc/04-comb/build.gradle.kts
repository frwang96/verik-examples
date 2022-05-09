/*
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    kotlin("jvm")
    id("io.verik.verik-plugin")
}

val names = listOf(
    "BitScanReverse",
    "IsPowerOfTwo",
    "LogPowerOfTwo",
    "Equal",
    "VectorEqual",
    "SevenSegmentDecoder",
    "PopulationCount",
    "IsGeq"
)

verik {
    dsim {
        compileTops = names.map { "${it}Test" }
        names.forEach {
            sim {
                name = "sim$it"
                simTop = "${it}Test"
            }
        }
    }
}

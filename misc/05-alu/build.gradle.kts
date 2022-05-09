/*
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    kotlin("jvm")
    id("io.verik.verik-plugin")
}

val names = listOf(
    "Sr32",
    "Sll32",
    "Sft32",
    "Cmp",
    "Ltu32",
    "Lt32",
    "Fa",
    "Rca",
    "AddSub",
    "Alu"
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

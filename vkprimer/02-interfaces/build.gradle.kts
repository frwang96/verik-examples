/*
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    kotlin("jvm")
    id("io.verik.verik-plugin")
}

dependencies {
    implementation(project(":vkprimer:00-common"))
}

verik {
    dsim {
        compileTops = listOf("Top")
        sim {
            name = "sim"
        }
    }
}

/*
 * SPDX-License-Identifier: Apache-2.0
 */

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "io.verik.verik-plugin")
    repositories {
        mavenLocal()
        mavenCentral()
    }
}

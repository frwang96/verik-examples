/*
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    kotlin("jvm") version "1.5.31" apply false
    id("io.verik.verik-plugin") version "local-SNAPSHOT" apply false
}

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}
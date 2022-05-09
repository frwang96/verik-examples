/*
 * SPDX-License-Identifier: Apache-2.0
 */

import java.nio.file.Paths

plugins {
    kotlin("jvm")
    id("io.verik.verik-plugin")
}

dependencies {
    implementation(project(":uvmprimer:00-common"))
}

verik {
    import {
        val uvmHome = Paths.get(System.getenv("UVM_HOME"))
        importedFiles = listOf(uvmHome.resolve("src/uvm.sv"))
        includeDirs = listOf(uvmHome.resolve("src"))
    }
    dsim {
        val uvmHome = Paths.get("\$(UVM_HOME)")
        compileTops = listOf("top")
        extraFiles = listOf(uvmHome.resolve("src/uvm.sv"))
        extraIncludeDirs = listOf(uvmHome.resolve("src"))
        dpiLibs = listOf(uvmHome.resolve("src/dpi/libuvm_dpi.so"))
        sim {
            name = "simFibonacci"
            plusArgs["UVM_TESTNAME"] = "fibonacci_test"
        }
        sim {
            name = "simFull"
            plusArgs["UVM_TESTNAME"] = "full_test"
        }
        sim {
            name = "simParallel"
            plusArgs["UVM_TESTNAME"] = "parallel_test"
        }
    }
}

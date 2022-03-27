/*
 * SPDX-License-Identifier: Apache-2.0
 */

import java.nio.file.Paths

dependencies {
    implementation(project(":uvmprimer:00-common"))
}

verikImport {
    importedFiles = listOf(Paths.get("${System.getenv("UVM_HOME")}/uvm.sv"))
    includeDirs = listOf(Paths.get(System.getenv("UVM_HOME")))
}

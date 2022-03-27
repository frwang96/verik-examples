/*
 * SPDX-License-Identifier: Apache-2.0
 */

import java.nio.file.Paths

verikImport {
    importedFiles = listOf(Paths.get("${System.getenv("UVM_HOME")}/uvm.sv"))
    includeDirs = listOf(Paths.get(System.getenv("UVM_HOME")))
}

/*
 * SPDX-License-Identifier: Apache-2.0
 */

import io.verik.plugin.VerikImporterPluginExtension
import java.nio.file.Files

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "io.verik.verik-plugin")
    val verikImporterPluginExtension = extensions.getByType(VerikImporterPluginExtension::class)
    val verilogSrcDir = projectDir.resolve("src/main/verilog").toPath()
    if (Files.exists(verilogSrcDir)) {
        verikImporterPluginExtension.importedFiles = Files.walk(verilogSrcDir).toList()
            .filter { it.toFile().extension in listOf("v", "sv") }
    }
    repositories {
        mavenLocal()
        mavenCentral()
    }
}

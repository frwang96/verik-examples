/*
 * Copyright (c) 2021 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
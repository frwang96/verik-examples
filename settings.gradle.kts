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

rootProject.name = "verik-examples"

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}

include("riscv")

include("uvmprimer:00-common")
include("uvmprimer:01-conventional-test")
include("uvmprimer:02-interfaces")
include("uvmprimer:03-classes")
include("uvmprimer:04-polymorphism")
include("uvmprimer:05-static-methods")
include("uvmprimer:06-type-parameters")
include("uvmprimer:07-factory-pattern")
include("uvmprimer:08-oop-test")
include("uvmprimer:09-uvm-test")
include("uvmprimer:10-uvm-components")
include("uvmprimer:11-uvm-environments")
include("uvmprimer:12-analysis-ports")
include("uvmprimer:13-analysis-ports-test")
include("uvmprimer:14-communication")
include("uvmprimer:15-communication-test")
include("uvmprimer:16-uvm-reporting")
include("uvmprimer:17-deep-operations")
include("uvmprimer:18-uvm-transactions")
include("uvmprimer:19-uvm-agents")
include("uvmprimer:20-uvm-sequences")

include("vkprimer:00-common")
include("vkprimer:01-conventional-test")
include("vkprimer:02-interfaces")
include("vkprimer:03-classes")
include("vkprimer:04-polymorphism")
include("vkprimer:05-static-methods")
include("vkprimer:06-type-parameters")
include("vkprimer:07-factory-pattern")
include("vkprimer:08-oop-test")
include("vkprimer:17-deep-operations")

include("svverif:01-arbiter")

include("misc:01-count")
include("misc:02-adder")
include("misc:03-multiplier")
include("misc:04-comb")
include("misc:05-alu")
include("misc:06-cache")

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

include("sanity:01-count")
include("sanity:02-adder")
include("sanity:03-multiplier")
include("sanity:04-comb")
include("sanity:05-alu")
include("sanity:06-cache")
include("sanity:07-import-div")

include("uvmprimer:02-tests")
include("uvmprimer:03-interfaces")
include("uvmprimer:05-classes")
include("uvmprimer:06-polymorphism")
include("uvmprimer:07-objects")
include("uvmprimer:08-type-parameters")
include("uvmprimer:09-factory-pattern")
include("uvmprimer:10-oop-test")
include("uvmprimer:11-uvm-test")

include("riscv")

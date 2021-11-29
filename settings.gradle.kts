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

include("sanity01-count")
include("sanity02-adder")
include("sanity03-multiplier")
include("sanity04-cache")

include("tutorial01-overview")
include("tutorial03-data-types")

include("uvmprimer02-tests")
include("uvmprimer03-interfaces")
include("uvmprimer05-classes")
include("uvmprimer06-polymorphism")
include("uvmprimer07-objects")
include("uvmprimer08-type-parameters")
include("uvmprimer09-factory-pattern")
include("uvmprimer10-oop-tests")

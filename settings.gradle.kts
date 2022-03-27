/*
 * SPDX-License-Identifier: Apache-2.0
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
include("svverif:02-unique-array")
include("svverif:03-atm-switch")
include("svverif:04-utopia-atm-switch")

include("misc:01-count")
include("misc:02-adder")
include("misc:03-multiplier")
include("misc:04-comb")
include("misc:05-alu")
include("misc:06-cache")

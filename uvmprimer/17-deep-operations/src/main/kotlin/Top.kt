/*
 * Copyright (c) 2022 Francis Wang
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

@file:Verik

import io.verik.core.*

@Entry
class Top : Module() {

    @Run
    fun run() {
        val circus_lion1 = CircusLion(2, true, "Agnes", 2)
        val circus_lion2 = CircusLion(3, false, "Simba", 0)
        println("Lion 1: $circus_lion1")
        println("Lion 2 before copy: $circus_lion2")
        circus_lion2.copyFrom(circus_lion1)
        println("Lion 2 after copy: $circus_lion2")
    }
}

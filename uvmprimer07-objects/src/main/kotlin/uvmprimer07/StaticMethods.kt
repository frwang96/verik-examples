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

package uvmprimer07

import io.verik.core.*

@SimTop
class StaticMethodsTop : Module() {

    @Run
    fun run() {
        LionCage.cageLion(Lion("Kimba"))
        LionCage.cageLion(Lion("Simba"))
        LionCage.cageLion(Lion("Mustafa"))
        LionCage.listLions()
    }
}

abstract class Animal

class Lion(val name: String) : Animal()

object LionCage {

    val cage = ArrayList<Lion>()

    fun cageLion(lion: Lion) {
        cage.add(lion)
    }

    fun listLions() {
        cage.forEach { println(it.name) }
    }
}
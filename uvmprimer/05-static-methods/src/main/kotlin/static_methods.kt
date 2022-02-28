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

@file:Verik

import io.verik.core.*

abstract class animal : Class {

    var age = -1

    constructor(a: Int) {
        age = a
    }

    fun set_age(a: Int) {
        age = a
    }

    fun get_age(): Int {
        if (age == -1) {
            fatal("You didn't set the age")
        } else {
            return age
        }
    }

    abstract fun make_sound()
}

class lion : animal {

    val name: String

    constructor(age: Int, n: String) : super(age) {
        name = n
    }

    override fun make_sound() {
        println("$name says ROAR")
    }

    fun get_name(): String {
        return name
    }
}

object lion_cage : Class() {

    val cage: Queue<lion> = nc()

    fun cage_lion(lion: lion) {
        cage.add(lion)
    }

    fun list_lions() {
        println("Lions in cage")
        cage.forEach { println(it.get_name()) }
    }
}

@Entry
object top : Module() {

    @Run
    fun run() {
        var lion_h = lion(2, "Kimba")
        lion_cage.cage_lion(lion_h)
        lion_h = lion(3, "Simba")
        lion_cage.cage_lion(lion_h)
        lion_h = lion(15, "Mustafa")
        lion_cage.cage_lion(lion_h)
        lion_cage.list_lions()
    }
}

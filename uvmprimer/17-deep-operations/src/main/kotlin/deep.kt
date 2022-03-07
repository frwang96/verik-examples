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

abstract class animal : Class {

    var age: Int

    constructor(a: Int = 0) : super() {
        age = a
    }

    open fun convert2string(): String {
        return "Age: $age"
    }

    open fun do_copy(copied_animal: animal) {
        age = copied_animal.age
    }
}

open class mammal : animal {

    var babies_in_litter: Int

    constructor(a: Int = 0, b: Int = 0) : super(a) {
        babies_in_litter = b
    }

    override fun convert2string(): String {
        return "${super.convert2string()}\nbabies in litter: $babies_in_litter"
    }

    override fun do_copy(copied_animal: animal) {
        val copied_mammal: mammal
        super.do_copy(copied_animal)
        copied_mammal = copied_animal as mammal
        babies_in_litter = copied_mammal.babies_in_litter
    }
}

open class lion : mammal {

    var is_female: Boolean

    constructor(
        age: Int = 0,
        babies_in_litter: Int = 0,
        is_female: Boolean = false
    ) : super(age, babies_in_litter) {
        this.is_female = is_female
    }

    override fun convert2string(): String {
        val gender_s: String
        gender_s = if (is_female) "Female" else "Male"
        return "${super.convert2string()}\nGender: $gender_s"
    }

    override fun do_copy(copied_animal: animal) {
        val copied_lion: lion
        super.do_copy(copied_animal)
        copied_lion = copied_animal as lion
        this.is_female = copied_lion.is_female
    }
}

open class captive_lion : lion {

    var name: String

    constructor(
        age: Int = 0,
        babies_in_litter: Int = 0,
        is_female: Boolean = false,
        name: String = ""
    ) : super(age, babies_in_litter, is_female) {
        this.name = name
    }

    override fun convert2string(): String {
        return "${super.convert2string()}\nName: $name"
    }

    override fun do_copy(copied_animal: animal) {
        val copied_captive_lion: captive_lion
        super.do_copy(copied_animal)
        copied_captive_lion = copied_animal as captive_lion
        this.name = copied_captive_lion.name
    }
}

class circus_lion : captive_lion {

    var numb_tricks: Int

    constructor(
        age: Int = 0,
        is_female: Boolean = false,
        babies_in_litter: Int = 0,
        name: String = "",
        numb_tricks: Int = 0
    ) : super(age, babies_in_litter, is_female, name) {
        this.numb_tricks = numb_tricks
    }

    override fun convert2string(): String {
        return "${super.convert2string()}\nnumb_tricks: $numb_tricks"
    }

    override fun do_copy(copied_animal: animal) {
        val copied_circus_lion: circus_lion
        super.do_copy(copied_animal)
        copied_circus_lion = copied_animal as circus_lion
        this.numb_tricks = copied_circus_lion.numb_tricks
    }
}

@Entry
class top : Module() {

    lateinit var circus_lion1_h: circus_lion
    lateinit var circus_lion2_h: circus_lion

    @Run
    fun run() {
        circus_lion1_h = circus_lion(
            age = 2,
            is_female = true,
            babies_in_litter = 2,
            name = "Agnes",
            numb_tricks = 2
        )
        println("\n--- Lion 1 ---\n${circus_lion1_h.convert2string()}")
        circus_lion2_h = circus_lion()
        println("\n--- Lion 2 before copy ---\n${circus_lion2_h.convert2string()}")
        circus_lion2_h.do_copy(circus_lion1_h)
        println("\n--- Lion 2 after copy ---\n${circus_lion2_h.convert2string()}")
    }
}

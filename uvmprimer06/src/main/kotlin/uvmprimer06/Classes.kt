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

package uvmprimer06

import io.verik.core.*

@SimTop
class ClassesTop : Module() {

    @Run
    fun run() {
        val lion = Lion(15)
        lion.makeSound()
        println("The Lion is ${lion.age} years old")
        val chicken = Chicken(1)
        chicken.makeSound()
        println("The Chicken is ${chicken.age} years old")

        var animal: Animal = lion
        animal.makeSound()
        println("The animal is ${animal.age} years old")
        animal = chicken
        animal.makeSound()
        println("The animal is ${animal.age} years old")
    }
}

abstract class Animal(val age: Int) {

    abstract fun makeSound()
}

class Lion(age: Int) : Animal(age) {

    override fun makeSound() {
        println("The Lion says ROAR")
    }
}

class Chicken(age: Int) : Animal(age) {

    override fun makeSound() {
        println("The Chicken says BECAWW")
    }
}

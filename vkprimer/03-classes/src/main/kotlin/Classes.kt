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

open class Rectangle(val length: Int, val width: Int) : Class() {

    fun area(): Int {
        return length * width
    }
}

class Square(side: Int) : Rectangle(side, side)

@Entry
object Top : Module() {

    @Run
    fun run() {
        val rectangle = Rectangle(50, 20)
        println("rectangle area: ${rectangle.area()}")
        val square = Square(50)
        println("square area: ${square.area()}")
    }
}

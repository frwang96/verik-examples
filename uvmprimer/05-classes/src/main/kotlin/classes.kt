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

open class rectangle : Class {

    val length: Int
    val width: Int

    constructor(length: Int, width: Int) : super() {
        this.length = length
        this.width = width
    }

    fun area(): Int {
        return length * width
    }
}

class square : rectangle {

    constructor(side: Int) : super(side, side)
}

@Entry
object top : Module() {

    lateinit var rectangle_h: rectangle
    lateinit var square_h: square

    @Run
    fun run() {
        rectangle_h = rectangle(50, 20)
        println("rectangle area: ${rectangle_h.area()}")
        square_h = square(50)
        println("square area: ${square_h.area()}")
    }
}

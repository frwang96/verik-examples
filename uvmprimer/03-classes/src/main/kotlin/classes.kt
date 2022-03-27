/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary")

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

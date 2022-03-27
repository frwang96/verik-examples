/*
 * SPDX-License-Identifier: Apache-2.0
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

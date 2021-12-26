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

fun and1(a: Boolean, b: Boolean): Boolean {
    return a && b
}

fun or1(a: Boolean, b: Boolean): Boolean {
    return a || b
}

fun xor1(a: Boolean, b: Boolean): Boolean {
    return a xor b
}

fun not1(a: Boolean): Boolean {
    return !a
}

fun mux1(sel: Boolean, a: Boolean, b: Boolean): Boolean {
    return if (sel) b else a
}

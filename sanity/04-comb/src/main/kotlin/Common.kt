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

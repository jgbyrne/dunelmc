package utils

import java.lang.Math.cos
import java.lang.Math.sin

data class Vec2(val x: Double, val y: Double) {

    constructor(x: Number, y: Number) : this(x.toDouble(), y.toDouble())
    constructor(t: Number) : this(t.toDouble())
    constructor(t: Double) : this(cos(t), sin(t))

    operator fun plus(other: Vec2) = Vec2(x + other.x, y + other.y)
    operator fun minus(other: Vec2) = Vec2(x - other.x, y - other.y)
    operator fun times(other: Double) = Vec2(x * other, y * other)
    operator fun div(other: Double) = Vec2(x / other, y / other)

}
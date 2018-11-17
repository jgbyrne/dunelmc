package core

import utils.Vec2
import utils.with
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.geom.AffineTransform
import java.awt.geom.RoundRectangle2D
import java.lang.Math.ceil
import java.lang.Math.floor
import javax.swing.JTextField
import kotlin.math.roundToInt

data class MailBox(val lineNo: Int, val boxNo: Int, val instruction: Instruction) {

    val color = instruction.operation.color

    val boxValueField = JTextField()

    init {

    }

    fun getLocation(viewMode: Double, size: Vec2): Vec2 {
        return if (viewMode % 1.0 == 0.0) {
            when (viewMode.toInt()) {
                0 -> {
                    Vec2(boxNo % 10 * size.x / 10, boxNo / 10 * size.y / 10)
                }
                1 -> {
                    Vec2(boxNo % 5 * size.x / 5, boxNo / 5 * size.y / 10)
                }
                2 -> {
                    Vec2(0, lineNo * size.y / 10)
                }
                else -> throw Exception("This is illegal")
            }
        } else {
            val along = viewMode % 1.0
            getLocation(ceil(viewMode), size) * along + getLocation(floor(viewMode), size) * (1 - along)
        }
    }

    fun getSize(viewMode: Double, size: Vec2): Vec2 {
        return if (viewMode % 1.0 == 0.0) {
            when (viewMode.toInt()) {
                0 -> {
                    Vec2(size.x / 10, size.y / 10)
                }
                1 -> {
                    Vec2(size.x / 5, size.y / 10)
                }
                2 -> {
                    Vec2(size.x, size.y / 10)
                }
                else -> throw Exception("This is illegal")
            }
        } else {
            val along = viewMode % 1.0
            getSize(ceil(viewMode), size) * along + getSize(floor(viewMode), size) * (1 - along)
        }
    }

    fun draw(g: Graphics2D, viewMode: Double, size: Vec2) {
        val location = getLocation(viewMode, size)
        val size = getSize(viewMode, size)
        g.with(
                deltaTransform = AffineTransform.getTranslateInstance(location.x, location.y)
        ) {
            val label = (if (viewMode.roundToInt() == 2) lineNo else boxNo).toString()
            g.drawString(label, 4, 4 + 12)
            g.with(color = Color(color.red, color.green, color.blue, 100)) {
                g.fill(RoundRectangle2D.Double(
                        0.0,
                        0.0,
                        size.x,
                        size.y,
                        12.0,
                        12.0
                ))
            }
            g.with(color = color) {
                g.fill(RoundRectangle2D.Double(
                        4.0,
                        size.y * .5 - 4,
                        size.x - 8,
                        size.y * .5,
                        12.0,
                        12.0
                ))
                boxValueField.bounds = Rectangle(
                        (location.x + 4).toInt(),
                        (location.y + size.y * .5 - 4).toInt(),
                        (size.x - 8).toInt(),
                        (size.y * .5).toInt()
                )
            }
        }
    }

}
package core

import utils.Vec2
import utils.with
import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.geom.Rectangle2D
import java.lang.Math.ceil
import java.lang.Math.floor

data class MailBox(val lineNo: Int, val boxNo: Int, val instruction: Instruction) {

    val color = instruction.operation.color

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
                else -> Vec2(0, 0)
            }
        } else {
            val along = viewMode % 1.0
            getLocation(ceil(viewMode), size) * along + getLocation(floor(viewMode), size) * (1 - along)
        }
    }

    fun draw(g: Graphics2D, viewMode: Double, size: Vec2) {
        val location = getLocation(viewMode, size)
        g.with(
                deltaTransform = AffineTransform.getTranslateInstance(location.x, location.y),
                color = this.color
        ) {
            g.fill(Rectangle2D.Double(0.0, 0.0, 10.0, 10.0))
        }
    }

}
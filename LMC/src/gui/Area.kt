package gui

import utils.Vec2
import java.awt.Color
import java.awt.Color.BLACK
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.geom.Rectangle2D
import javax.swing.JPanel

open class Area(val start: Vec2, val size: Vec2) : JPanel() {

    override fun paint(g: Graphics) {
        super.paint(g)
        g.color = Color.GRAY
        (g as Graphics2D).fill(Rectangle2D.Double(
                100.0,
                100.0,
                size.x - 200,
                size.y - 200
        ))
        g.color = BLACK
        g.draw(Rectangle2D.Double(
                0.0,
                0.0,
                size.x,
                size.y
        ))
    }

}
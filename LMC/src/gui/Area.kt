package gui

import core.Session
import utils.Vec2
import java.awt.Color
import java.awt.Color.BLACK
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.geom.Rectangle2D
import javax.swing.BorderFactory
import javax.swing.JPanel

open class Area(val session:Session, val start: Vec2, val size: Vec2) : JPanel() {

    init {
        border = BorderFactory.createEtchedBorder()
    }

    override fun paint(g: Graphics) {
        super.paint(g)
        g.color = Color.GRAY
        (g as Graphics2D).fill(Rectangle2D.Double(
                20.0,
                20.0,
                size.x - 20,
                size.y - 20
        ))
        g.color = BLACK
        g.draw(Rectangle2D.Double(
                0.0,
                0.0,
                size.x,
                size.y
        ))
        g.drawString("This is some stuff ${javaClass.name}", 12, 20)
    }

}
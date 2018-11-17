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

open class Area(val session:Session) : JPanel() {

    init {
        border = BorderFactory.createEtchedBorder()
    }

}
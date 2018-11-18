package gui

import core.Session
import utils.Vec2
import java.awt.Dimension
import javax.swing.JPanel

class ConsoleArea(val session: Session) : JPanel() {
    init {
        preferredSize = Dimension(400,200)
    }
}
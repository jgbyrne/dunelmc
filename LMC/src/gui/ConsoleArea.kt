package gui

import core.Session
import utils.Vec2
import java.awt.Dimension
import javax.swing.JPanel
import javax.swing.JTextArea

class ConsoleArea(val session: Session) : JTextArea("This is some output") {
    init {
        preferredSize = Dimension(400,200)
    }
}
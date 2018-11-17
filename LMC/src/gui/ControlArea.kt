package gui

import core.Session
import utils.Vec2
import java.awt.Dimension

class ControlArea(session: Session) : Area(session) {
    init {
        preferredSize = Dimension(400, 200)
    }
}
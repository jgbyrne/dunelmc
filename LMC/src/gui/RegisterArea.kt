package gui

import core.Session
import utils.Vec2
import java.awt.Dimension

class RegisterArea(session: Session, start: Vec2, size: Vec2) : Area(session, start, size) {
    init {
        preferredSize = Dimension(400,200)
    }
}
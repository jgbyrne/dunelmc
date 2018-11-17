package gui

import core.Session
import utils.Vec2
import java.awt.*
import javax.swing.JPanel

class ContentPane(val session: Session) : JPanel() {
    init {

        val mailBoxArea = BoxArea(session)
        val registerArea = RegisterArea(session)
        val controlArea = ControlArea(session)
        val consoleArea = ConsoleArea(session)

        val rightSide = JPanel(GridLayout(3, 1, 10, 10))
        rightSide.add(consoleArea)
        rightSide.add(registerArea)
        rightSide.add(controlArea)

        layout = BorderLayout()
        add(rightSide, BorderLayout.EAST)
        add(mailBoxArea, BorderLayout.CENTER)

    }

    override fun paint(g2: Graphics?) {
        val g = g2 as Graphics2D
        val rh = RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        rh[RenderingHints.KEY_TEXT_ANTIALIASING] = RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        g.setRenderingHints(rh)

        super.paint(g)
    }

    companion object {
        const val FRAME_PADDING = 50
        const val PADDING = 20
    }
}
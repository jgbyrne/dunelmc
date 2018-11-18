package gui

import core.Session
import utils.Vec2
import java.awt.*
import javax.swing.BorderFactory
import javax.swing.JPanel
import javax.swing.JScrollPane

class ContentPane(val session: Session) : JPanel() {
    init {

        border = BorderFactory.createEmptyBorder(10,10,10,10)

        val mailBoxArea = BoxArea(session)
        val registerArea = RegisterArea(session)
        val controlArea = ControlArea(session)
        val consoleArea = ConsoleArea(session)

        val rightSide = JPanel(BorderLayout())
        rightSide.add(consoleArea, BorderLayout.NORTH)
        rightSide.add(registerArea, BorderLayout.CENTER)
        rightSide.add(controlArea, BorderLayout.SOUTH)

        layout = BorderLayout()
        add(rightSide, BorderLayout.EAST)
        val scrollArea: JScrollPane
        scrollArea = JScrollPane(mailBoxArea)
        scrollArea.preferredSize = Dimension(630 + 20, 630 + 20)
        scrollArea.horizontalScrollBar.isEnabled = false
        scrollArea.horizontalScrollBar.isVisible = false
        add(scrollArea, BorderLayout.CENTER)

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
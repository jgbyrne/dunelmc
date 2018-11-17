package gui

import core.Session
import utils.Vec2
import java.awt.*
import javax.swing.JPanel

class ContentPane(val session: Session) : JPanel() {
    init {

        val mailBoxArea = BoxArea(session, Vec2(FRAME_PADDING, FRAME_PADDING),
                Vec2(height - FRAME_PADDING * 2, height - FRAME_PADDING * 2)
        )
        val registerAreaPreferedHeight = (height - FRAME_PADDING * 2) * 0.3
        val registerArea = RegisterArea(session, mailBoxArea.start + Vec2(mailBoxArea.size.x, 0) + Vec2(PADDING, 0),
                Vec2(width - mailBoxArea.size.x - FRAME_PADDING * 2 - PADDING, registerAreaPreferedHeight)
        )

        val controlAreaPreferedHeight = (height - FRAME_PADDING * 2) * 0.3
        val controlArea = ControlArea(session, mailBoxArea.start + Vec2(
                mailBoxArea.size.x + PADDING,
                height - controlAreaPreferedHeight - FRAME_PADDING * 2
        ), Vec2(width - mailBoxArea.size.x - PADDING - 2 * FRAME_PADDING,
                controlAreaPreferedHeight
        ))

        val consoleArea = ConsoleArea(session, registerArea.start + Vec2(0, registerArea.size.y + PADDING),
                Vec2(width - controlArea.start.x - FRAME_PADDING, controlArea.start.y - registerArea.start.y - registerArea.size.y - PADDING * 2))

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
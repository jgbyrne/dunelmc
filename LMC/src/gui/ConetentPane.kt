package gui

import core.Session
import utils.Vec2
import utils.with
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.geom.AffineTransform
import java.awt.geom.Rectangle2D
import javax.swing.JPanel

class ConetentPane(val session: Session) : JPanel() {
    init {
        preferredSize = Dimension(1280, 720)
    }

    override fun paint(g2: Graphics) {
        super.paint(g2)
        val g = g2 as Graphics2D
        val rh = RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        rh[RenderingHints.KEY_TEXT_ANTIALIASING] = RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        g.setRenderingHints(rh)

        val mailBoxArea = MailBoxArea(
                Vec2(FRAME_PADDING, FRAME_PADDING),
                Vec2(height - FRAME_PADDING * 2, height - FRAME_PADDING * 2)
        )
        val registerAreaPreferedHeight = (height - FRAME_PADDING * 2) * 0.3
        val registerArea = RegisterArea(
                mailBoxArea.start + Vec2(mailBoxArea.size.x, 0) + Vec2(PADDING, 0),
                Vec2(width - mailBoxArea.size.x - FRAME_PADDING * 2 - PADDING, registerAreaPreferedHeight)
        )


        val controlAreaPreferedHeight = (height - FRAME_PADDING * 2) * 0.3
        val controlArea = ControlArea(mailBoxArea.start + Vec2(
                mailBoxArea.size.x + PADDING,
                height - controlAreaPreferedHeight - FRAME_PADDING * 2
        ), Vec2(
                width - mailBoxArea.size.x + PADDING - FRAME_PADDING,
                controlAreaPreferedHeight
        ))

        val areas = listOf(controlArea, registerArea, mailBoxArea)
        areas.forEach {
            g.with(deltaTransform = AffineTransform.getTranslateInstance(it.start.x, it.start.y)) {
                g.draw(Rectangle2D.Double(0.0, 0.0, it.size.x, it.size.y))

            }
        }

//        val consoleStart = registerAreaStart + Vec2(0, registerAreaSize.y + PADDING)
//        val consoleSize = Vec2(
//                width - controlStart.x - FRAME_PADDING,
//                controlStart.y - registerAreaStart.y - registerAreaSize.y - PADDING * 2
//        )

    }

    companion object {
        const val FRAME_PADDING = 50
        const val PADDING = 20
    }
}
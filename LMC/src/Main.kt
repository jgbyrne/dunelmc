import utils.Vec2
import utils.with
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.geom.AffineTransform
import java.awt.geom.Line2D
import java.awt.geom.Rectangle2D
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.WindowConstants.EXIT_ON_CLOSE

fun main(args: Array<String>) {

    val frame = JFrame("LMC")
    frame.contentPane = ConetentPane()
    frame.defaultCloseOperation = EXIT_ON_CLOSE
    frame.pack()
    frame.setLocationRelativeTo(null)
    frame.isVisible = true

}

class ConetentPane : JPanel() {
    init {
        preferredSize = Dimension(1280, 720)
    }

    override fun paint(g2: Graphics) {
        super.paint(g2)
        val g = g2 as Graphics2D
        val rh = RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        rh[RenderingHints.KEY_TEXT_ANTIALIASING] = RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        g.setRenderingHints(rh)

        val boxAreaStart = Vec2(FRAME_PADDING, FRAME_PADDING)
        val boxAreaSize = Vec2(height - FRAME_PADDING * 2, height - FRAME_PADDING * 2)
        g.with(deltaTransform = AffineTransform.getTranslateInstance(
                boxAreaStart.x,
                boxAreaStart.y
        )) {
            g.draw(Rectangle2D.Double(
                    0.0,
                    0.0,
                    boxAreaSize.x,
                    boxAreaSize.y
            ))
            g.drawString("Mailbox area", 20, 20)
        }

        val registerAreaStart = boxAreaStart + Vec2(boxAreaSize.x, 0) + Vec2(PADDING, 0)
        val registerAreaPreferedHeight = (height - FRAME_PADDING * 2) * 0.3
        val registerAreaSize = Vec2(
                width - boxAreaSize.x - FRAME_PADDING * 2 - PADDING,
                registerAreaPreferedHeight
        )
        g.with(deltaTransform = AffineTransform.getTranslateInstance(
                registerAreaStart.x,
                registerAreaStart.y
        )) {
            g.draw(Rectangle2D.Double(
                    0.0,
                    0.0,
                    registerAreaSize.x,
                    registerAreaSize.y
            ))
            g.drawString("IO and Registers Area", 20, 20)
        }

        val controlAreaPreferedHeight = (height - FRAME_PADDING * 2) * 0.3
        val controlStart = boxAreaStart + Vec2(
                boxAreaSize.x + PADDING,
                height - controlAreaPreferedHeight - FRAME_PADDING * 2
        )
        val controlSize = Vec2(
                width - controlStart.x - FRAME_PADDING, 
                controlAreaPreferedHeight
        )
        g.with(deltaTransform = AffineTransform.getTranslateInstance(
                controlStart.x,
                controlStart.y
        )) {
            g.draw(Rectangle2D.Double(
                    0.0,
                    0.0,
                    controlSize.x,
                    controlSize.y
            ))
            g.drawString("Control Buttons Area", 20, 20)
        }

        val consoleStart = boxAreaStart + Vec2(
                boxAreaSize.x + PADDING,
                height - controlAreaPreferedHeight - FRAME_PADDING * 2
        )
        val consoleSize = Vec2(
                width - controlStart.x - FRAME_PADDING,
                controlAreaPreferedHeight
        )
        g.with(deltaTransform = AffineTransform.getTranslateInstance(
                consoleStart.x,
                consoleStart.y
        )) {
            g.draw(Rectangle2D.Double(
                    0.0,
                    0.0,
                    consoleSize.x,
                    consoleSize.y
            ))
            g.drawString("Control Buttons Area", 20, 20)
        }

    }

    companion object {
        const val FRAME_PADDING = 50
        const val PADDING = 20
    }
}

class Program(val sourceCode: String, val mailboxes: List<MailBox>)

data class MailBox(val string: String)
package gui

import core.MailBox.Companion.ROUNDNESS
import core.Session
import utils.with
import java.awt.*
import java.awt.geom.RoundRectangle2D
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JPanel

class ControlArea(val session: Session) : JPanel() {
    init {
        border = BorderFactory.createEmptyBorder(10, 10, 0, 0)

        val ignoreBreakpointButton = CustomButton("Ø", Color.decode("#2196F3"))
        val stopButton = CustomButton("□", Color.decode("#F44336"))
        val stepBackwardButton = CustomButton("<-", Color.decode("#4CAF50"))
        val playPauseButton = CustomButton(">", Color.decode("#8BC34A"))
        val stepForwardButton = CustomButton("->", Color.decode("#4CAF50"))
        val fastForwardButton = CustomButton("⏩", Color.decode("#2196F3"))

        val resetButton = CustomButton("RESTE", Color.decode("#D50000"))

        val main = JPanel(FlowLayout())
        main.add(ignoreBreakpointButton)
        main.add(stopButton)
        main.add(stepBackwardButton)
        main.add(playPauseButton)
        main.add(stepForwardButton)
        main.add(fastForwardButton)

        layout = BorderLayout()
        add(main, BorderLayout.CENTER)
        add(resetButton, BorderLayout.EAST)

    }
}

class CustomButton(text: String, val color: Color) : JButton(text) {
    init {
        border = BorderFactory.createEmptyBorder()
        background = Color(0, 0, 0, 0)
        preferredSize = Dimension(50, 50)
    }

    override fun paintComponent(g: Graphics) {
        g as Graphics2D
        val color = if (model.isPressed) { // pressed backround
            color.darker()
        } else if (model.isRollover) { // hover background
            color.brighter()
        } else { // nothing
            color
        }
        g.with(color = color) {
            g.fill(RoundRectangle2D.Double(
                    0.0,
                    0.0,
                    width.toDouble(),
                    height.toDouble(),
                    ROUNDNESS,
                    ROUNDNESS
            ))
        }
        super.paintComponent(g)
    }
}

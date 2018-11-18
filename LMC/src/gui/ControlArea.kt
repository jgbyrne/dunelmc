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
        ignoreBreakpointButton.addActionListener {
            session.ignoreBreakpoints = !session.ignoreBreakpoints
            ignoreBreakpointButton.text = if (session.ignoreBreakpoints)
                "O" else "Ø"
            this@ControlArea.parent.parent.parent.repaint()
            session.toggleBreakpoints()
        }
        val playPauseButton = CustomButton("▶️", Color.decode("#8BC34A")) //⏸️⏸️⏸️ ⏸/⏸️
        playPauseButton.addActionListener {
            session.playing = !session.playing
            playPauseButton.text = if (session.playing) "||" else "▶️"
            session.startedPlaying {
                parent.parent.repaint()
            }

        }
        val stepForwardButton = CustomButton("↷", Color.decode("#4CAF50"))
        stepForwardButton.addActionListener {
            if (!session.playing)
                session.stepForward()
            parent.parent.repaint()
        }
//        val fastForwardButton = CustomButton("⏩⏭▶▶", Color.decode("#2196F3"))
//        fastForwardButton.addActionListener {
//            session.fastForward()
//        }
//        val resetButton = CustomButton("RESET", Color.decode("#D50000"))
//        resetButton.addActionListener {
//            session.reset()
//        }

        val main = JPanel(FlowLayout())
        main.add(ignoreBreakpointButton)
        main.add(playPauseButton)
        main.add(stepForwardButton)
//        main.add(fastForwardButton)

        layout = BorderLayout()
        add(main, BorderLayout.CENTER)
//        add(resetButton, BorderLayout.EAST)

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

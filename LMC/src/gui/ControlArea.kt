package gui

import core.Session
import utils.Vec2
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.GridLayout
import javax.swing.BorderFactory
import javax.swing.JButton

class ControlArea(session: Session) : Area(session) {
    init {
        preferredSize = Dimension(400, 200)

        val ignoreBreakpointButton = CustomButton("0")
        val stopButton = CustomButton("[]")
        val fastForwardButton = CustomButton("->>")
        val stepBackwardButton = CustomButton("<-")
        val playPauseButton = CustomButton(">")
        val stepForwardButton = CustomButton("->")

        listOf(
                ignoreBreakpointButton,
                stopButton,
                fastForwardButton,
                stepBackwardButton,
                playPauseButton,
                stepForwardButton
        ).forEach {
            it.background = Color(0, 0, 0, 0)
            it.border = BorderFactory.createEmptyBorder()
        }

        layout = GridLayout(2, 3)
        add(ignoreBreakpointButton)
        add(stopButton)
        add(fastForwardButton)
        add(stepBackwardButton)
        add(playPauseButton)
        add(stepForwardButton)

    }
}

class CustomButton(text: String) : JButton(text) {
    init {
        border = BorderFactory.createEmptyBorder()

    }

    override fun paintComponent(g: Graphics?) {
        if (model.isPressed) { // pressed backround

        } else if (model.isRollover) { // hover background

        } else { // nothing

        }
    }
}
package gui

import core.Operations
import core.Session
import utils.Vec2
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import javax.swing.*
import javax.swing.border.Border

class RegisterArea(val session: Session) : JPanel(BorderLayout()) {
    init {
        val inputPanel = JPanel(BorderLayout())
        inputPanel.background = Operations.IN.color.let { Color(it.red, it.green, it.blue, 100) }
        inputPanel.preferredSize = Dimension(80, 200)
        inputPanel.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
        inputPanel.add(JLabel("Input", JLabel.CENTER).also {
            it.foreground = Operations.IN.fontColor
        }, BorderLayout.NORTH)
        val inputField = JTextField()
        inputPanel.add(inputField, BorderLayout.SOUTH)

        val outputPanel = JPanel(BorderLayout())
        outputPanel.background = Operations.OUT.color.let { Color(it.red, it.green, it.blue, 100) }
        outputPanel.preferredSize = Dimension(80, 200)
        outputPanel.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
        outputPanel.add(JLabel("Output", JLabel.CENTER).also {
            it.foreground = Operations.OUT.fontColor
        }, BorderLayout.NORTH)

        val left = JPanel()
        left.add(inputPanel)
        left.add(outputPanel)

        add(left, BorderLayout.WEST)

    }
}
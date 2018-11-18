package gui

import core.MailBox.Companion.ROUNDNESS
import core.Operations
import core.Session
import utils.with
import java.awt.*
import java.awt.geom.RoundRectangle2D
import javax.swing.*

class RegisterArea(val session: Session) : JPanel(BorderLayout()) {

    val inList: JList<String>
    val outList: JList<String>

    val accLabel: JLabel
    val pcLabel: JLabel
    val piLabel: JLabel
    val nfLabel: JLabel

    init {

        val inputPanel = object : JPanel(BorderLayout()) {
            override fun paintComponent(g: Graphics?) {
                g as Graphics2D
                g.with(color = Operations.IN.color.let {
                    Color(it.red, it.green, it.blue, 100)
                }) {
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
        inputPanel.background = Color(0, 0, 0, 0)
        inputPanel.preferredSize = Dimension(80, 130)
        inputPanel.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
        inputPanel.add(JLabel("Input", JLabel.CENTER).also {
            it.foreground = Operations.IN.fontColor
        }, BorderLayout.NORTH)
        val inputField = object : JTextField() {
            override fun paintComponent(g: Graphics?) {
                g as Graphics2D
                g.with(color = Operations.IN.color) {
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
        inputField.addActionListener {

        }
        inputField.horizontalAlignment = JTextField.CENTER
        inputField.background = Color(0, 0, 0, 0)
        inputField.border = BorderFactory.createEmptyBorder(2, 2, 2, 2)
        inputPanel.add(inputField, BorderLayout.SOUTH)

        inList = JList(DefaultListModel<String>().also { it.addAll(listOf("123", "123")) })
        inList.background = Color(0, 0, 0, 0)
        (inList.cellRenderer as DefaultListCellRenderer).horizontalAlignment = SwingConstants.CENTER
        inputPanel.add(inList, BorderLayout.CENTER)


        val outputPanel = object : JPanel(BorderLayout()) {
            override fun paintComponent(g: Graphics?) {
                g as Graphics2D
                g.with(color = Operations.OUT.color.let {
                    Color(it.red, it.green, it.blue, 100)
                }) {
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
        outputPanel.background = Color(0, 0, 0, 0)
        outputPanel.preferredSize = Dimension(80, 130)
        outputPanel.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
        outputPanel.add(JLabel("Output", JLabel.CENTER).also {
            it.foreground = Operations.OUT.fontColor
        }, BorderLayout.NORTH)


        outList = JList(DefaultListModel<String>().also { it.addAll(listOf("123", "123")) })
        outList.background = Color(0, 0, 0, 0)
        (outList.cellRenderer as DefaultListCellRenderer).horizontalAlignment = SwingConstants.CENTER
        outputPanel.add(outList, BorderLayout.CENTER)

        val left = JPanel()
        left.add(inputPanel)
        left.add(outputPanel)

        accLabel = JLabel("000")
        pcLabel = JLabel("000")
        piLabel = JLabel("000")
        nfLabel = JLabel("NF").also { it.foreground = Color.RED }

        val accPanel = object : JPanel(GridLayout(1, 2)) {
            override fun paintComponent(g: Graphics?) {
                g as Graphics2D
                g.with(color = Color.decode("#4CAF50")) {
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
        accPanel.background = Color(0, 0, 0, 0)
        accPanel.border = BorderFactory.createEmptyBorder(6, 6, 6, 6)
        accPanel.add(JLabel("ACC:", JLabel.RIGHT), BorderLayout.WEST)
        val labelGroup = JPanel(GridLayout(1, 2))
        labelGroup.add(accLabel)
        labelGroup.add(nfLabel)
        labelGroup.background = Color(0, 0, 0, 0)
        accPanel.add(labelGroup, BorderLayout.EAST)

        val pcPanel = object : JPanel(GridLayout(1, 2)) {
            override fun paintComponent(g: Graphics?) {
                g as Graphics2D
                g.with(color = Color.decode("#2196F3")) {
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
        pcPanel.background = Color(0, 0, 0, 0)
        pcPanel.border = BorderFactory.createEmptyBorder(6, 6, 6, 6)
        pcPanel.add(JLabel("PC:", JLabel.RIGHT), BorderLayout.WEST)
        pcPanel.add(pcLabel, BorderLayout.EAST)

        val piPanel = object : JPanel(GridLayout(1, 2)) {
            override fun paintComponent(g: Graphics?) {
                g as Graphics2D
                g.with(color = Color.decode("#F44336")) {
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
        piPanel.background = Color(0, 0, 0, 0)
        piPanel.border = BorderFactory.createEmptyBorder(6, 6, 6, 6)
        piPanel.add(JLabel("PI:", JLabel.RIGHT), BorderLayout.WEST)
        piPanel.add(piLabel, BorderLayout.EAST)

        val right = JPanel(GridLayout(3, 1, 5, 5))
        right.preferredSize = Dimension(100, 130)

        right.add(accPanel)
        right.add(pcPanel)
        right.add(piPanel)

        layout = GridLayout(1, 2)
        add(left)
        add(right)

    }
}
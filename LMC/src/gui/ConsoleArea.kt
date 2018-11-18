package gui

import core.Session
import utils.Vec2
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*

class ConsoleArea(val session: Session) : JPanel(GridLayout(1, 1)) {
    init {
        border = BorderFactory.createEmptyBorder(0, 10, 10, 0)
        val textArea = object : JTextArea("") {
            override fun setBounds(x: Int, y: Int, width: Int, height: Int) {
                super.setBounds(x, y, width, height)
                this.maximumSize = this.preferredSize
            }
        }
        add(JScrollPane(textArea).also { it.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS })
    }
}
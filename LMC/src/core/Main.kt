package core

import gui.ConetentPane
import javax.swing.JFrame
import javax.swing.WindowConstants.EXIT_ON_CLOSE

fun main(args: Array<String>) {

    val session = Session()

    val frame = JFrame("LMC")
    frame.contentPane = ConetentPane(session)
    frame.defaultCloseOperation = EXIT_ON_CLOSE
    frame.pack()
    frame.setLocationRelativeTo(null)
    frame.isResizable = false
    frame.isVisible = true

}


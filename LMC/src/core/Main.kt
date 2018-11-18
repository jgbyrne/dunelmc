package core

import gui.ContentPane
import java.io.FileReader
import javax.swing.*
import javax.swing.WindowConstants.EXIT_ON_CLOSE

fun main(args: Array<String>) {

    val session = Session("")

    val frame = JFrame("DuneLMC")
    frame.jMenuBar = JMenuBar()
    frame.jMenuBar.add(JMenu("File").also {
        it.add(JMenuItem("Open").also {
            it.addActionListener {
                val chooser = JFileChooser()

                val returnVal = chooser.showOpenDialog(frame)
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    val file = chooser.selectedFile
                    val code = FileReader(file).readText()
                    frame.contentPane = ContentPane(Session(code))
                }
            }
        })
    })
    frame.contentPane = ContentPane(session)
    frame.defaultCloseOperation = EXIT_ON_CLOSE
    frame.pack()
    frame.setLocationRelativeTo(null)
    frame.isResizable = false
    frame.isVisible = true

}
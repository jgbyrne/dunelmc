package core

import gui.ContentPane
import java.awt.Dimension
import java.awt.GridLayout
import java.io.FileReader
import javax.swing.*
import javax.swing.WindowConstants.EXIT_ON_CLOSE

fun main(args: Array<String>) {

    val frame = JFrame("DuneLMC")
    frame.jMenuBar = JMenuBar()
    frame.jMenuBar.add(JMenu("File").also {
        it.add(JMenuItem("Open").also {
            it.addActionListener {
                loadFile(frame)
            }
        })
    })

    val emptyPane = JPanel(GridLayout())
    emptyPane.preferredSize = Dimension(300, 300)
    emptyPane.add(JButton("Load File").also {
        it.addActionListener {
            loadFile(frame)
        }
    })

    frame.contentPane = emptyPane
    frame.defaultCloseOperation = EXIT_ON_CLOSE
    frame.pack()
    frame.setLocationRelativeTo(null)
    frame.isResizable = false
    frame.isVisible = true

}

private fun loadFile(frame: JFrame) {
    val chooser = JFileChooser()

    val returnVal = chooser.showOpenDialog(frame)
    if (returnVal == JFileChooser.APPROVE_OPTION) {
        val file = chooser.selectedFile
        val code = FileReader(file).readText()
        frame.contentPane = ContentPane(Session(code))
        frame.pack()
        frame.setLocationRelativeTo(null)
        frame.repaint()
    }
}
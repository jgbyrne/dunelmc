package gui

import core.Session
import utils.Vec2
import utils.with
import java.awt.*
import java.awt.Color.WHITE
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseWheelEvent
import java.awt.event.MouseWheelListener
import java.awt.geom.AffineTransform
import java.awt.geom.Rectangle2D
import java.lang.Math.*
import javax.swing.JPanel
import javax.swing.JTextArea
import kotlin.math.max
import kotlin.math.roundToInt

class BoxArea(val session: Session) : JPanel(), MouseWheelListener, MouseListener {

    var viewMode: Double = 0.0

    private var animationThread: AnimationThread? = null
    val editor = object : JTextArea(session.code) {
        init {
            isEditable = false
        }

        override fun repaint() {
            parent?.repaint()
        }
    }
    val lineHeight: Int
        get() = editor.let {
            it.getFontMetrics(it.font)
        }.height

    var scrollOffset: Double = 0.0
    val trueHeight
        get() = (630 * getHeightMultiplier()).toInt()

    init {
        preferredSize = Dimension(630, 630)

        addMouseWheelListener(this)
        addMouseListener(this)

        editor.setBounds(MARGIN, 0, width - MARGIN, height)
        editor.background = Color(0, 0, 0, 0)
        editor.font = Font(Font.MONOSPACED, 0, 20)
        updateEditor(viewMode)

        layout = null
        session.boxes.forEach {
            add(it.boxValueField)
            add(it.mnemonicLabel)
        }

        add(editor)
        println(editor.text)

    }

    override fun mouseClicked(e: MouseEvent) {
        when (viewMode.roundToInt()) {
            0 -> {
                val x = (e.x / width.toDouble() * 10).toInt()
                val y = (e.y / height.toDouble() * 10).toInt()
                val index = x + 10 * y
                session.boxes.getOrNull(index)?.isBreakPoint = !session.boxes[index].isBreakPoint
                repaint()
            }
            1 -> {

                val x = (e.x / width.toDouble() * 10).toInt()
                val y = (e.y / height.toDouble() * 10 / 1.5).toInt()
                val index = x + 10 * y
                session.boxes.getOrNull(index)?.isBreakPoint = !session.boxes[index].isBreakPoint
                repaint()
            }
            2 -> {
                val index = (e.y / this.lineHeight).toInt()
                session.boxes.getOrNull(index)?.isBreakPoint = !session.boxes[index].isBreakPoint
                repaint()
            }
        }
    }


    fun updateEditor(viewMode: Double) {
        editor.foreground = editor.foreground.run {
            Color(red, green, blue, (getEditorVisibility(viewMode) * 255).toInt())
        }
        editor.isVisible = getEditorVisibility(viewMode).roundToInt() != 0
        editor.isEnabled = getEditorVisibility(viewMode).roundToInt() != 0

    }

    private fun getHeightMultiplier(): Double {
        return if (viewMode % 1.0 == 0.0) {
            when (viewMode.toInt()) {
                0 -> 1.0 * ceil(session.boxes.size / 10 / 10.0)
                1 -> 1.5 * ceil(session.boxes.size / 10 / 10.0)
                2 -> ceil(630.0 / lineHeight / 10 * session.boxes.size / 100)
                else -> throw Exception("This is illegal")
            }
        } else {
            val along = viewMode % 1.0
            getEditorVisibility(ceil(viewMode)) * along + getEditorVisibility(floor(viewMode)) * (1 - along)
        }
    }

    private fun getEditorVisibility(viewMode: Double): Double {
        return if (viewMode % 1.0 == 0.0) {
            when (viewMode.toInt()) {
                0 -> 0.0
                1 -> 0.0
                2 -> 1.0
                else -> throw Exception("This is illegal")
            }
        } else {
            val along = viewMode % 1.0
            getEditorVisibility(ceil(viewMode)) * along + getEditorVisibility(floor(viewMode)) * (1 - along)
        }
    }

    override fun setBounds(x: Int, y: Int, width: Int, height: Int) {
        super.setBounds(x, y, width, height)


        val size = Vec2(width, height)
        session.boxes.forEach {
            it.update(viewMode, size, this)
        }
        parent?.repaint()

    }

    override fun setBounds(r: Rectangle?) {

        super.setBounds(r)
        val size = Vec2(width, height)
        session.boxes.forEach {
            it.update(viewMode, size, this)
        }
        parent.repaint()
    }

    override fun paint(g: Graphics) {
        g as? Graphics2D ?: throw  Exception("Cast Failed")
        g.clearRect(0, 0, width, height)
        g.with(color = WHITE) {
            g.fillRect(0, 0, width, height)
        }

        g.with(deltaTransform = AffineTransform.getTranslateInstance(0.0, scrollOffset)) {

            if (viewMode.roundToInt() == 2) {
                (0 until session.lines.size).forEach {
                    g.with(color = if (it % 2 == 0)
                        Color(180, 180, 180, max(min(((viewMode - 1.5) * 2 * 255).toInt(), 255), 0))
                    else
                        Color(210, 210, 210, max(min(((viewMode - 1.5) * 2 * 255).toInt(), 255), 0))) {
                        if (session.lineNumberMap.containsKey(it)) {
                            g.fill(Rectangle2D.Double(MARGIN.toDouble(), it * lineHeight.toDouble(), width.toDouble(), lineHeight.toDouble()))
                        } else {
                            g.fill(Rectangle2D.Double(0.0, it * lineHeight.toDouble(), width.toDouble(), lineHeight.toDouble()))
                        }
                    }
                    g.drawString(session.lines[it]?: "", MARGIN.toFloat() + 5f, (it + .85f) * lineHeight.toFloat())

                }
            }

            val size = Vec2(width, height)
            session.boxes.forEach {
                it.draw(g, viewMode, size, this)
            }
        }
        super.paintChildren(g)

    }

    override fun mouseWheelMoved(e: MouseWheelEvent) {

        if (e.isControlDown) {

            animationThread?.interrupt()
            val currentTime = System.currentTimeMillis()
            val nextViewMode = max(0.0, min(2.0, viewMode.roundToInt().toDouble() - e.wheelRotation))
            animationThread = AnimationThread(
                    this,
                    currentTime,
                    currentTime + 400,
                    viewMode,
                    nextViewMode
            )
            animationThread?.start()

        } else {
            scrollOffset += 40 * e.wheelRotation
            val size = utils.Vec2(width, height)
            session.boxes.forEach {
                it.update(viewMode, size, this)
            }
            updateEditor(viewMode)
            parent.parent.repaint()
        }

    }

    private class AnimationThread(val mailBoxArea: BoxArea,
                                  val startTime: Long,
                                  val endTime: Long,
                                  val fromValue: Double,
                                  val toValue: Double) : Thread("Animation Thread") {

        override fun run() {
            val duration = (endTime - startTime).toDouble()

            do {
                val currentTime = System.currentTimeMillis()
                val t = (currentTime - startTime) / duration
                val swing = 3 * pow(t, 2.0) - 2 * pow(t, 3.0)

                mailBoxArea.viewMode = swing * toValue + (1 - swing) * fromValue
                with(mailBoxArea) {
                    val size = Vec2(width, height)
                    session.boxes.forEach {
                        it.update(viewMode, size, this)
                    }
                    updateEditor(viewMode)
                }
                mailBoxArea.repaint()

                try {
                    sleep(5)
                } catch (e: InterruptedException) {
                    break
                }
            } while (currentTime < endTime && !isInterrupted)

            mailBoxArea.viewMode = toValue
            with(mailBoxArea) {
                val size = Vec2(width, height)
                session.boxes.forEach {
                    it.update(viewMode, size, this)
                }
            }

            mailBoxArea.repaint()
        }
    }

    companion object {
        val MARGIN = 60
    }

    override fun mouseReleased(e: MouseEvent?) {}
    override fun mouseEntered(e: MouseEvent?) {}
    override fun mouseExited(e: MouseEvent?) {}
    override fun mousePressed(e: MouseEvent?) {}

}

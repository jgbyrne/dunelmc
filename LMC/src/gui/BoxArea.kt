package gui

import core.Session
import utils.Vec2
import java.awt.*
import java.awt.event.MouseWheelEvent
import java.awt.event.MouseWheelListener
import java.lang.Math.*
import javax.swing.JTextArea
import kotlin.math.max
import kotlin.math.roundToInt

class BoxArea(session: Session) : Area(session), MouseWheelListener {

    var viewMode: Double = 0.0
    var scrollOffset: Double = 0.0

    private var animationThread: AnimationThread? = null
    val editor = JTextArea(session.code)

    init {
        preferredSize = Dimension(PREF_WIDTH, MIN_HEIGHT)
        addMouseWheelListener(this)

        add(editor)
        editor.setBounds(0, 0, width, height)

        layout = null
        session.boxes.forEach {
            add(it.boxValueField)
            add(it.mnemonicLabel)
        }

    }

    fun findCurrentHieght(): Double {
        val size = Vec2(width, height)
        println(size)
        return session.boxes.last().let {
            it.getLocation(viewMode, size, this) + it.getSize(viewMode, size)
        }.y

    }

    override fun setBounds(x: Int, y: Int, width: Int, height: Int) {
        super.setBounds(x, y, width, height)


        val size = Vec2(width, height)
        session.boxes.forEach {
            it.update(viewMode, size, this)
        }
        parent.repaint()

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
        super.paint(g)
        g as? Graphics2D ?: throw  Exception("Cast Failed")



        val size = Vec2(width, height)
        session.boxes.forEach {
            it.draw(g, viewMode, size, this)
        }

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
        val PREF_WIDTH = 630
        val MIN_HEIGHT = 630
    }

}

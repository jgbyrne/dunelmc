package gui

import core.Session
import utils.Vec2
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.MouseWheelEvent
import java.awt.event.MouseWheelListener
import java.awt.geom.Rectangle2D
import java.lang.Math.*
import kotlin.math.roundToInt

class MailBoxArea(session: Session, start: Vec2, size: Vec2) : Area(session, start, size), MouseWheelListener {
    var viewMode: Double = 0.0
    private var animationThread: AnimationThread? = null

    init {
        preferredSize = Dimension(630,630)
        addMouseWheelListener(this)
    }

    override fun paint(g: Graphics) {
        g as? Graphics2D ?: throw  Exception("Cast Failed")
        g.draw(Rectangle2D.Double(viewMode * 100, viewMode * 100, 100.0, 100.0))

    }

    override fun mouseWheelMoved(e: MouseWheelEvent) {
        print("yolo")
        animationThread?.interrupt()
        val currentTime = System.currentTimeMillis()
        val nextViewMode = max(0.0, min(2.0, viewMode.roundToInt().toDouble() - e.scrollAmount))
        animationThread = AnimationThread(
                this,
                currentTime,
                currentTime + 400,
                viewMode,
                nextViewMode
        )
        animationThread?.start()
    }

    private class AnimationThread(val mailBoxArea: MailBoxArea,
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
                mailBoxArea.repaint()
                try {
                    sleep(5)
                } catch (e: InterruptedException) {
                    break
                }
            } while (currentTime < endTime && !isInterrupted)

        }
    }

}

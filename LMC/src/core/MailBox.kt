package core

import gui.BoxArea
import utils.Vec2
import utils.drawStringCentred
import utils.with
import java.awt.*
import java.awt.Color.RED
import java.awt.geom.AffineTransform
import java.awt.geom.RoundRectangle2D
import java.lang.Math.ceil
import java.lang.Math.floor
import javax.swing.BorderFactory
import javax.swing.JLabel
import javax.swing.JTextField

data class MailBox(val lineNo: Int, val boxNo: Int, val instruction: Instruction) {

    val color = instruction.operation.color
    val fontColor = instruction.operation.fontColor

    val boxValueField = object : JTextField(instruction.toString()) {
        init {
            border = BorderFactory.createEmptyBorder()
            foreground = fontColor
            background = Color(0, 0, 0, 0)
            font = font.deriveFont(Font.BOLD, font.size + 2f)
            horizontalAlignment = JTextField.CENTER
        }

        override fun paint(g: Graphics?) {
            g as Graphics2D

            g.with(color = color) {
                g.fill(RoundRectangle2D.Double(
                        0.0,
                        0.0,
                        width.toDouble(),
                        height.toDouble(),
                        ROUNDNESS,
                        ROUNDNESS
                ))
            }
            super.paint(g)
        }
    }
    val mnemonicLabel = JLabel(instruction.operation.mnemonic, JLabel.CENTER)

    init {

    }

    fun getLocation(viewMode: Double, size: Vec2, area: BoxArea): Vec2 {
        return if (viewMode % 1.0 == 0.0) {
            when (viewMode.toInt()) {
                0 -> {
                    Vec2(boxNo % 10 * size.x / 10, boxNo / 10 * size.y / 10)
                }
                1 -> {
                    Vec2(boxNo % 10 * size.x / 10, boxNo / 10 * size.y / 10 * 1.5)
                }
                2 -> {
                    Vec2(0, lineNo * size.y / 10)
                }
                else -> throw Exception("This is illegal")
            }
        } else {
            val along = viewMode % 1.0
            getLocation(ceil(viewMode), size, area) * along + getLocation(floor(viewMode), size, area) * (1 - along)
        } + Vec2(0, area.scrollOffset * 40)
    }

    fun getSize(viewMode: Double, size: Vec2): Vec2 {
        return if (viewMode % 1.0 == 0.0) {
            when (viewMode.toInt()) {
                0 -> {
                    Vec2(size.x / 10, size.y / 10)
                }
                1 -> {
                    Vec2(size.x / 10, size.y / 10 * 1.5)
                }
                2 -> {
                    Vec2(size.x, size.y / 10)
                }
                else -> throw Exception("This is illegal")
            }
        } else {
            val along = viewMode % 1.0
            getSize(ceil(viewMode), size) * along + getSize(floor(viewMode), size) * (1 - along)
        }
    }


    fun getMnemonicVisibility(viewMode: Double): Double {
        return if (viewMode % 1.0 == 0.0) {
            when (viewMode.toInt()) {
                0 -> 0.0
                1 -> 1.0
                2 -> 0.0
                else -> throw Exception("This is illegal")
            }
        } else {
            val along = viewMode % 1.0
            getMnemonicVisibility(ceil(viewMode)) * along + getMnemonicVisibility(floor(viewMode)) * (1 - along)
        }
    }


    fun draw(g: Graphics2D, viewMode: Double, size: Vec2, boxArea: BoxArea) {
        val location = getLocation(viewMode, size, boxArea)
        val cellSize = getSize(viewMode, size)

        g.with(
                deltaTransform = AffineTransform.getTranslateInstance(location.x, location.y)
        ) {
            g.drawStringCentred(boxNo.toString(), Vec2(cellSize.x / 2, size.y / 10 * .2))
            g.with(color = Color(color.red, color.green, color.blue, 100)) {
                g.fill(RoundRectangle2D.Double(
                        0.0,
                        0.0,
                        cellSize.x,
                        cellSize.y,
                        12.0,
                        12.0
                ))
            }

            if (boxNo == boxArea.session.PC) {
                g.with(color = RED, stroke = BasicStroke(4f)) {
                    g.draw(RoundRectangle2D.Double(
                            0.0,
                            0.0,
                            cellSize.x,
                            cellSize.y,
                            12.0,
                            12.0
                    ))
                }
            }


        }
    }

    fun update(viewMode: Double, size: Vec2, boxArea: BoxArea) {
        val location = getLocation(viewMode, size, boxArea)
        val cellSize = getSize(viewMode, size)

        boxValueField.bounds = Rectangle(
                (location.x + 4).toInt(),
                (location.y + cellSize.y - size.y / 10 * .6 - 4).toInt(),
                (cellSize.x - 8).toInt(),
                (size.y / 10 * .6).toInt())

        mnemonicLabel.bounds = Rectangle(
                (location.x).toInt(),
                (location.y).toInt(),
                (cellSize.x).toInt(),
                (cellSize.y * .8).toInt()
        )
        mnemonicLabel.foreground = Color(
                mnemonicLabel.foreground.red,
                mnemonicLabel.foreground.green,
                mnemonicLabel.foreground.blue,
                (getMnemonicVisibility(viewMode) * 255).toInt()
        )
    }

    companion object {
        public const val ROUNDNESS = 12.0
    }

}
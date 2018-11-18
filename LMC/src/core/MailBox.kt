package core

import gui.BoxArea
import gui.BoxArea.Companion.MARGIN
import utils.Vec2
import utils.drawStringCentred
import utils.with
import java.awt.*
import java.awt.Color.RED
import java.awt.geom.AffineTransform
import java.awt.geom.Ellipse2D
import java.awt.geom.RoundRectangle2D
import java.lang.Math.ceil
import java.lang.Math.floor
import javax.swing.BorderFactory
import javax.swing.JLabel
import javax.swing.JTextField
import kotlin.math.roundToInt

data class MailBox(val lineNo: Int, val boxNo: Int, val instruction: Instruction, var isBreakPoint: Boolean) {

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

    private fun getLocation(viewMode: Double, size: Vec2, area: BoxArea): Vec2 {
        return if (viewMode % 1.0 == 0.0) {
            when (viewMode.toInt()) {
                0 -> Vec2(boxNo % 10 * size.x / 10, boxNo / 10 * size.y / 10)
                1 -> Vec2(boxNo % 10 * size.x / 10, boxNo / 10 * size.y / 10 * 1.5)
                2 -> Vec2(0, area.lineHeight * lineNo)
                else -> throw Exception("This is illegal")
            }
        } else {
            val along = viewMode % 1.0
            getLocation(ceil(viewMode), size, area) * along + getLocation(floor(viewMode), size, area) * (1 - along)
        }
    }

    private fun getSize(viewMode: Double, size: Vec2, area: BoxArea): Vec2 {
        return if (viewMode % 1.0 == 0.0) {
            when (viewMode.toInt()) {
                0 -> Vec2(size.x / 10, size.y / 10)
                1 -> Vec2(size.x / 10, size.y / 10 * 1.5)
                2 -> Vec2(MARGIN, area.lineHeight)
                else -> throw Exception("This is illegal")
            }
        } else {
            val along = viewMode % 1.0
            getSize(ceil(viewMode), size, area) * along + getSize(floor(viewMode), size, area) * (1 - along)
        }
    }

    private fun getMnemonicVisibility(viewMode: Double): Double {
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

    private fun getValueFieldSize(viewMode: Double, size: Vec2, boxArea: BoxArea): Vec2 {
        return if (viewMode % 1.0 == 0.0) {
            when (viewMode.toInt()) {
                0 -> Vec2(getSize(viewMode, size, boxArea).x - 8, size.y / 10 * .6)
                1 -> Vec2(getSize(viewMode, size, boxArea).x - 8, size.y / 10 * .6)
                2 -> Vec2(40, boxArea.lineHeight)
                else -> throw Exception("This is illegal")
            }
        } else {
            val along = viewMode % 1.0
            getValueFieldSize(ceil(viewMode), size, boxArea) * along + getValueFieldSize(floor(viewMode), size, boxArea) * (1 - along)
        }
    }

    private fun getValueFieldLocation(viewMode: Double, size: Vec2, boxArea: BoxArea): Vec2 {
        val location = getLocation(viewMode, size, boxArea)
        val cellSize = getSize(viewMode, size, boxArea)

        val valueFieldSize = getValueFieldSize(viewMode, size, boxArea)

        return if (viewMode % 1.0 == 0.0) {
            when (viewMode.toInt()) {
                0 -> Vec2(location.x + 4, location.y + cellSize.y - valueFieldSize.y - 4)
                1 -> Vec2(location.x + 4, location.y + cellSize.y - valueFieldSize.y - 4)
                2 -> Vec2(location.x + 20, location.y + cellSize.y - valueFieldSize.y)
                else -> throw Exception("This is illegal")
            }
        } else {
            val along = viewMode % 1.0
            getValueFieldLocation(ceil(viewMode), size, boxArea) * along + getValueFieldLocation(floor(viewMode), size, boxArea) * (1 - along)
        }
    }

    private fun getLineLabelVisibility(viewMode: Double): Double {
        return if (viewMode % 1.0 == 0.0) {
            when (viewMode.toInt()) {
                0 -> 1.0
                1 -> 1.0
                2 -> 0.0
                else -> throw Exception("This is illegal")
            }
        } else {
            val along = viewMode % 1.0
            getLineLabelVisibility(ceil(viewMode)) * along + getLineLabelVisibility(floor(viewMode)) * (1 - along)
        }
    }

    fun draw(g: Graphics2D, viewMode: Double, size: Vec2, boxArea: BoxArea) {
        val location = getLocation(viewMode, size, boxArea)
        val cellSize = getSize(viewMode, size, boxArea)

        g.with(
                deltaTransform = AffineTransform.getTranslateInstance(location.x, location.y)
        ) {
            g.with(color = g.color.run {
                Color(red, green, blue, (getLineLabelVisibility(viewMode) * 255).toInt())
            }) {
                g.drawStringCentred(boxNo.toString(), Vec2(cellSize.x / 2, size.y / 10 * .2))
            }
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

            if (isBreakPoint && viewMode.roundToInt() == 2) {
                g.with(color = Color(255, 0, 0, ((viewMode - 1.5) * 2 * 255).toInt())) {

                    g.fill(Ellipse2D.Double(0.0, 0.0, 20.0, 20.0))
                }
            }

            if (boxNo == boxArea.session.PC) {
                g.with(color = RED, stroke = BasicStroke(
                        4f,
                        BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_BEVEL,
                        1f,
                        floatArrayOf(7f),
                        0f
                )) {
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
            if (boxNo == boxArea.session.IR) {
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
        val cellSize = getSize(viewMode, size, boxArea)

        val valueFieldSize = getValueFieldSize(viewMode, size, boxArea)
        val valueFieldLocation = getValueFieldLocation(viewMode, size, boxArea)
        boxValueField.bounds = Rectangle(
                valueFieldLocation.x.toInt(),
                valueFieldLocation.y.toInt(),
                valueFieldSize.x.toInt(), //(cellSize.x - 8).toInt(),
                valueFieldSize.y.toInt())//(size.y / 10 * .6).toInt())

        boxValueField.isEditable = viewMode.roundToInt() != 2
        boxValueField.text = if (viewMode.roundToInt() != 2) instruction.toString() else lineNo.toString()
        mnemonicLabel.bounds = Rectangle(
                (location.x).toInt(),
                (location.y).toInt(),
                (cellSize.x).toInt(),
                (cellSize.y * .8).toInt()
        )
        mnemonicLabel.foreground = with(mnemonicLabel.foreground) {
            Color(red, green, blue, (getMnemonicVisibility(viewMode) * 255).toInt())
        }
    }

    companion object {
        const val ROUNDNESS = 12.0
    }

}
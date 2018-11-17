package utils

import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.Stroke
import java.awt.geom.AffineTransform

fun Graphics2D.with(
        stroke: Stroke = this.stroke,
        font: Font = this.font,
        deltaTransform: AffineTransform = AffineTransform(),
        color: Color = this.color,
        block: () -> Unit
) {

    val strokeBefore = this.stroke
    val fontBefore = this.font
    val transformBefore = this.transform
    val colorBefore = this.color

    this.stroke = stroke
    this.font = font
    transform(deltaTransform)
    this.color = color

    block.invoke()

    this.stroke = strokeBefore
    this.font = fontBefore
    this.transform = transformBefore
    this.color = colorBefore

}
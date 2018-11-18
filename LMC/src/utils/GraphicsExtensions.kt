package utils

import java.awt.*
import java.awt.geom.AffineTransform
import java.awt.geom.Line2D

fun Graphics2D.with(
        stroke: Stroke = this.stroke,
        font: Font = this.font,
        deltaTransform: AffineTransform = AffineTransform(),
        color: Color = this.color,
        paint:Paint = this.paint,
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

fun Graphics2D.drawStringCentred(text: String, centre: Vec2) {
    with(deltaTransform = AffineTransform.getTranslateInstance(
            centre.x - fontMetrics.stringWidth(text) / 2.0,
            centre.y + fontMetrics.ascent / 2.0
    )) {
        drawString(text, 0f, 0f)
    }
}

fun Graphics2D.drawLine(from: Vec2, to: Vec2) {
    draw(Line2D.Double(from.x, from.y, to.x, to.y))
}
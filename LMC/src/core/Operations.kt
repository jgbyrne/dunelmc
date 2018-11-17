package core

import java.awt.Color

enum class Operations(opcode: Int, color: Color) {
    ADD(100, Color.decode("#FFEB3B")),
    SUB(200, Color.decode("#FF9800")),
    STO(300, Color.decode("#8BC34A")),
    LDA(500, Color.decode("#4CAF50")),
    BR(600, Color.decode("#03A9F4")),
    BRZ(700, Color.decode("#03A9F4")),
    BRP(800, Color.decode("#03A9F4")),
    IN(901, Color.decode("#9C27B0")),
    OUT(902, Color.decode("#9C27B0")),
    DAT(902, Color.decode("#F44336"))


}
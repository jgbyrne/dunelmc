package core

import java.awt.Color
import java.awt.Color.BLACK
import java.awt.Color.WHITE

enum class Operations(val mnemonic: String, val opcode: Int, val color: Color, val fontColor: Color = BLACK) {
    ADD("ADD", 100, Color.decode("#FFEB3B")),
    SUB("SUB", 200, Color.decode("#FF9800")),
    STO("STO", 300, Color.decode("#8BC34A")),
    LDA("LDA", 500, Color.decode("#4CAF50")),
    BR("BR", 600, Color.decode("#03A9F4")),
    BRZ("BRZ", 700, Color.decode("#03A9F4")),
    BRP("BRP", 800, Color.decode("#03A9F4")),
    IN("IN", 901, Color.decode("#9C27B0"), WHITE),
    OUT("OUT", 902, Color.decode("#9C27B0"), WHITE),
    DAT("DAT", 902, Color.decode("#F44336"), WHITE)


}
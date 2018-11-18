package core

data class Instruction(val opcode: Int) {
    val operation: Operations

    init {
        val perfect = Operations.values().find { it.opcode == opcode }
        operation = perfect ?: Operations.values().find { it.opcode / 100 == opcode / 100 }
                ?: throw Error("Invalid Instruction $opcode")
    }

    override fun toString(): String = opcode.toString()
}
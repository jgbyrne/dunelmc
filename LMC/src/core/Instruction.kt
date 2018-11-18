package core

data class Instruction(val opcode: Int) {
    val operation: Operations
        get() {
            val perfect = Operations.values().find { it.opcode == opcode }
            return perfect ?: Operations.values().find { it.opcode % 100 == opcode % 100 }
            ?: throw Error("Invalid Instruction")
        }
}
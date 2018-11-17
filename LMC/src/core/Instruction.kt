package core

data class Instruction(val label: String?, val operation: Operations, val operand: String?){
    override fun toString(): String {
        return (operation.opcode / 100).toString() + operand
    }
}
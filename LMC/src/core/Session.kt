package core

class Session(val code: String) {

    val inputQueue = listOf<String>("123", "5", "7")
    val outputList = listOf<String>("bad")

    var IR: Int = 2
    var PC: Int = 3
    var ACC: Int = 1
    var NF: Boolean = true

    val lines = code.split("\n").mapIndexed { index, s ->
        index to s
    }.associate { it }

    val boxes = (0 until 105).map {
        MailBox(it , it, Instruction(
                "name,",
                Operations.values()[it % Operations.values().size],
                "54"
        ))
    }
}

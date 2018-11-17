package core

class Session(val code: String) {
    val lines = code.split("\n").mapIndexed { index, s ->
        index to s
    }.associate { it }

    val boxes = (0 until 105).map {
        MailBox(it * 2, it, Instruction(
                "name,",
                Operations.values()[it % Operations.values().size],
                "54"
        ))
    }
}

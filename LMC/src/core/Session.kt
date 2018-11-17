package core

class Session {
    val boxes = (0 until 105).map {
        MailBox(it * 2, Instruction(
                "name,",
                Operations.values()[it % Operations.values().size],
                "54"
        ))
    }
}

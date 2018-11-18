package core

class Session(val code: String) {

    val inputQueue = mutableListOf<String>()
    val outputList = mutableListOf<String>()

    var IR: Int = 0
    var PC: Int = 0
    var ACC: Int = 0
    var NF: Boolean = false

    val lines = code.split("\n").mapIndexed { index, s ->
        index to s
    }.associate { it }

    val boxes = listOf<MailBox>()
    val lineNumberMap = boxes.associate { it.lineNo to it }

}

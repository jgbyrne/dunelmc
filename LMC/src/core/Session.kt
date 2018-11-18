package core

import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

class Session(val code: String) {

    val sessionID:Int

    init {

        val compileURL = URL("http", "localhost", 10122, "/compile")
        val connection = compileURL.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true

        val out = DataOutputStream(connection.outputStream)
        out.write(code.toByteArray())
        out.flush()
        out.close()

        val resp = connection.responseCode
        if (resp == 418) Error("Cancer is happening")

        val input = DataInputStream(connection.inputStream)
        val result = input.readLine()
        println(result)

    }

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

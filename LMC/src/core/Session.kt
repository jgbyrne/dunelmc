package core

import org.json.JSONObject
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

class Session(val code: String) {

    val sessionID: Int

    val lines = code.split("\n").mapIndexed { index, s ->
        index to s
    }.associate { it }

    val inputQueue = mutableListOf<String>()
    val outputList = mutableListOf<String>()

    var PC: Int = 0
    var IP: Int = 0
    var ACC: String = ""
    var NF: Boolean = false
    val boxes = listOf<MailBox>()

    val lineNumberMap = boxes.associate { it.lineNo to it }

    init {

        val compileURL = URL("http", "localhost", 10122, "/compile")
        val connection = compileURL.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true

        val out = DataOutputStream(connection.outputStream)
        out.write(code.toByteArray())
        out.flush()
        out.close()

        val code = connection.responseCode
        if (code == 418) Error("Cancer is happening")

        val input = DataInputStream(connection.inputStream)
        val result = JSONObject(input.readLine())
        sessionID = result.getInt("exec_id")

        val registers = result.getJSONObject("registers")
        PC = registers.getInt("pc")
        IP = registers.getInt("ip")
        ACC = registers.getString("acc")
        NF = registers.getBoolean("neg")

        val boxes = result.getJSONArray("asm")
        (0 until boxes.length()).map { boxes.getJSONObject(it) }.forEach {
            
            MailBox(
                    it.getInt("lno"),
                    it.getInt("addr"),
                    Instruction(it.getInt("data")),
                    it.getBoolean("brk"))
        }

        /*
        ams
            addr should be int
            data should be int
            lno should be int
        registers
            ip should be int
            pc should be int


         */
    }

}

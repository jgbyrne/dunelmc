package core

import org.json.JSONObject
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

class Session(val code: String) {

    var playingThread: PlayingThread? = null

    fun startedPlaying() {
        if (playing) {
            playingThread?.interrupt()
            playingThread = PlayingThread(this)
            playingThread?.start()
        }
    }

    fun stepForward() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun toggleBreakpoints() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun fastForward() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun reset() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    val sessionID: Int

    var ignoreBreakpoints: Boolean = false
    var playing: Boolean = false

    val lines = code.split("\n").mapIndexed { index, s ->
        index to s
    }.associate { it }

    val inputQueue = mutableListOf<String>()
    val outputList = mutableListOf<String>()

    var PC: Int = 0
    var IP: Int = 0
    var ACC: String = ""
    var NF: Boolean = false
    val boxes: List<MailBox>

    val lineNumberMap: Map<Int, MailBox>

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
        if (code == 418) Error("This is a bad error")

        val input = DataInputStream(connection.inputStream)
        val result = JSONObject(input.readLine())
        sessionID = result.getInt("exec_id")

        val registers = result.getJSONObject("registers")
        PC = registers.getInt("pc")
        IP = registers.getInt("ip")
        ACC = registers.getString("acc")
        NF = registers.getBoolean("neg")

        val jsonBoxes = result.getJSONArray("asm")
        boxes = (0 until jsonBoxes.length()).map { jsonBoxes.getJSONObject(it) }.map {
            MailBox(
                    it.getInt("lno"),
                    it.getInt("addr"),
                    Instruction(it.getInt("data")),
                    try {
                        it.getBoolean("brk")
                    } catch (e: Exception) {
                        false
                    }
            )
        }

        lineNumberMap = boxes.associate { it.lineNo to it }
    }

}

class PlayingThread(val session: Session) : Thread("Playback Thread") {
    override fun run() {
        do {
            try {

                session.stepForward()
                sleep(1000)
            } catch (e: InterruptedException) {
                break
            }
        } while (session.playing && !isInterrupted && (session.ignoreBreakpoints || !session.boxes[session.IP].isBreakPoint))
    }
}

import java.io.File

/**
 * Logs writer
 *
 * For further analytics
 *
 * @param  logType  one of LogType enum, which role to which file log will be added
 * @param  data  string will be added with new line on end
 */
fun writeLog(logType: LogType, data: String) {
    File(logType.logFileName).appendText(data + "\n")
}

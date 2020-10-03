package core

import Tests.Companion.currentLogDirPath
import java.io.File

/**
 * Logs writer
 *
 * For further analytics
 *
 * @param  logType  one of core.LogType enum, which role to which file log will be added
 * @param  data  string will be added with new line on end
 */
fun writeLog(logType: LogType, data: String) {
    File(currentLogDirPath + logType.logFileName).appendText(data + "\n")
}

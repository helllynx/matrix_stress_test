/**
 * Logs type enum
 *
 * This class contains log file names
 *
 */
enum class LogType(val logFileName: String) {
    MESSAGE("message.log"),
    REGISTER("register.log"),
    LOGIN("login.log"),
    JOIN("join.log"),
    TOTAL("total.log")
}
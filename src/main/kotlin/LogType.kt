/**
 * Logs type enum
 *
 * This class contains log file names
 *
 */
enum class LogType(val logFileName: String) {
    MESSAGE("test_results/message.log"),
    REGISTER("test_results/register.log"),
    LOGIN("test_results/login.log"),
    JOIN("test_results/join.log"),
    TOTAL("test_results/total.log")
}
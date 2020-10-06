package core

/**
 * Logs type enum
 *
 * This class contains log file names
 *
 */
enum class LogType(val logFileName: String) {
    MESSAGE("message.log"),
    MESSAGE_PER_SEC_PUBLIC_ROOM("message_per_sec_public_room.log"),
    MESSAGE_PER_SEC_DIRECT("message_per_sec_direct.log"),
    REGISTER("register.log"),
    LOGIN("login.log"),
    JOIN("join.log"),
    TOTAL("total.log")
}
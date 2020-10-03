import kotlinx.coroutines.*
import matrix.MatrixID
import matrix.client.MatrixClientRequestException
import matrix.client.MatrixPasswordCredentials
import matrix.client.regular.MatrixHttpClient
import matrix.room.RoomCreationOptions
import matrix.room.RoomDirectoryVisibility
import java.io.File
import java.net.URL
import kotlin.system.measureNanoTime


/**
 * User names generator
 *
 * @param  n  number which will be added to `user_` prefix
 */
fun getNUserName(n: Int): String {
    return "user_$n"
}

/**
 * Room names generator
 *
 * @param  n  number which will be added to `room_` prefix
 */
fun getNRoomName(n: Int): String {
    return "room_$n"
}

/**
 * Async create public room
 *
 * @param  host  url of homeserver
 * @param  fromTo pair of Int's which specify from and to indexes users which will be used for test in this room
 * @param  password  password from all accounts
 */
suspend fun createPublicRoom(host: URL, fromTo: IntRange, userName: String, password: String) {
    try {
        val client = MatrixHttpClient(host).apply {
            login(MatrixPasswordCredentials(userName, password))
        }

        (fromTo.first until fromTo.last).map { n ->
            GlobalScope.async {
                val start = System.nanoTime()
                val roomOptions = RoomCreationOptions.Builder()
                        .setName(getNRoomName(n))
                        .setVisibility(RoomDirectoryVisibility.Public)
                        .get()

                writeLog(LogType.JOIN, (System.nanoTime() - start).toString())
                saveRoomIdToFile(client.createRoom(roomOptions).address)
            }
        }.joinAll()
        client.logout()
    } catch (e: MatrixClientRequestException) {
        e.printStackTrace()
    }
}

/**
 * Async create private 1 by 1 rooms
 *
 * @param  host  url of homeserver
 * @param  fromTo pair of Int's which specify from and to indexes users which will be used for test in this room
 * @param  password  password from all accounts
 */
suspend fun createPrivate1by1Room(host: URL, fromTo: IntRange, password: String) {
    try {
        (fromTo.first until fromTo.last step 2).map { n ->
            GlobalScope.async {
                val client = MatrixHttpClient(host).apply {
                    login(MatrixPasswordCredentials(getNUserName(n), password))
                }
                val roomOptions = RoomCreationOptions.Builder()
                        .setName(getNRoomName(n))
                        .setDirect(true)
                        .setVisibility(RoomDirectoryVisibility.Private)
                        .get()

                saveDirectRoomAndUsersToFile(n, n + 1, client.createRoom(roomOptions).address)
                client.logout()
            }
        }.joinAll()
    } catch (e: MatrixClientRequestException) {
        e.printStackTrace()
    }
}

/**
 * Async join users to room
 *
 * @param  host  url of homeserver
 * @param  fromTo pair of Int's which specify from and to indexes users which will be used for test in this room
 * @param  password  password from all accounts
 * @param  roomId  id of room like !KaVmYkkfnthPVfFpfi:matrix.example.com
 */
suspend fun joinToRoom(host: URL, fromTo: IntRange, password: String, roomId: String) {
    try {
        (fromTo.first until fromTo.last).map { n ->
            coroutineScope {
                async {
                    val start = System.nanoTime()
                    val client = MatrixHttpClient(host).apply {
                        login(MatrixPasswordCredentials(getNUserName(n), password))
                    }
                    client.joinRoom(roomId)
                    writeLog(LogType.JOIN, (System.nanoTime() - start).toString())
                    client.logout()
                }
            }
        }.joinAll()
    } catch (e: MatrixClientRequestException) {
        e.printStackTrace()
    }
}


/**
 * Async send messages
 *
 * @param  host  url of homeserver
 * @param  roomId  id of room like !KaVmYkkfnthPVfFpfi:matrix.example.com
 * @param  password  password from all accounts
 * @param  fromTo pair of Int's which specify from and to indexes users which will be used for test in this room
 * @param  message text message
 * @param  countOfMessages count messages which will be send by user
 */
suspend fun sendMessagesToPublicRooms(
        host: URL,
        roomId: String,
        password: String,
        fromTo: IntRange,
        message: String = "1234567890".repeat(5),
        countOfMessages: Int = 10
) = withContext(Dispatchers.IO) {
    try {
        (fromTo.first until fromTo.last).map { n ->
            GlobalScope.run {
                launch {
                    val client = MatrixHttpClient(host).apply {
                        login(MatrixPasswordCredentials(getNUserName(n), password))
                    }
                    val room = client.getRoom(roomId)
                    val time = measureNanoTime {
                        repeat(countOfMessages) {
                            room.sendText(message)
                        }
                    }
                    writeLog(LogType.MESSAGE, time.toString())
                    client.logout()
                }
            }
        }.joinAll()

    } catch (e: MatrixClientRequestException) {
        e.printStackTrace()
    }
}

/**
 * Async send messages
 *
 * @param  host  url of homeserver
 * @param  roomId  id of room like !KaVmYkkfnthPVfFpfi:matrix.example.com
 * @param  password  password from all accounts
 * @param  fromTo pair of Int's which specify from and to indexes users which will be used for test in this room
 * @param  message text message
 * @param  countOfMessages count messages which will be send by user
 */
suspend fun sendMessagesToPrivateConversation(
        host: URL,
        roomId: String,
        password: String,
        bob: Int,
        alice: Int,
        message: String = "1234567890".repeat(5),
        countOfMessages: Int = 10
) = withContext(Dispatchers.IO) {
    try {
        sendMessagesToDirectRoom(host, roomId, password, bob, message, countOfMessages)
        sendMessagesToDirectRoom(host, roomId, password, alice, message, countOfMessages)
    } catch (e: MatrixClientRequestException) {
        e.printStackTrace()
    }
}

private suspend fun sendMessagesToDirectRoom(
        host: URL,
        roomId: String,
        password: String,
        user: Int,
        message: String = "1234567890".repeat(5),
        countOfMessages: Int = 10
) = withContext(Dispatchers.IO) {
    try {
        GlobalScope.run {
            launch {
                val client = MatrixHttpClient(host).apply {
                    login(MatrixPasswordCredentials(getNUserName(user), password))
                }

                val room = client.getRoom(roomId)
                val time = measureNanoTime {
                    repeat(countOfMessages) {
                        room.sendText(message)
                    }
                }
                writeLog(LogType.MESSAGE, time.toString())
                client.logout()
            }
        }
    } catch (e: MatrixClientRequestException) {
        e.printStackTrace()
    }
}


/**
 * Async register user with password
 *
 * m.login.dummy method used
 * https://matrix.org/docs/guides/client-server-api#registration
 *
 * @param  host  url of homeserver
 * @param  fromTo pair of Int's which specify from and to indexes users which will be used for test in this room
 * @param  password  password of new user
 */
suspend fun registerUsers(
        host: URL,
        fromTo: IntRange,
        password: String
) = withContext(Dispatchers.IO) {
    try {
        (fromTo.first until fromTo.last).map { n ->
            GlobalScope.async {
                MatrixHttpClient(host).apply {
                    val start = System.nanoTime()
                    register(MatrixPasswordCredentials(getNUserName(n), password), false)
                    writeLog(LogType.REGISTER, (System.nanoTime() - start).toString())
                    logout()
                }
            }
        }.joinAll()
    } catch (e: MatrixClientRequestException) {
        e.printStackTrace()
    }
}


suspend fun inviteUserToDirectRoom(
        host: URL,
        domain: String,
        bob: Int,
        alice: Int,
        password: String,
        roomId: String
) = withContext(Dispatchers.IO) {
    try {
        GlobalScope.async {
            val client = MatrixHttpClient(host).apply {
                login(MatrixPasswordCredentials(getNUserName(bob), password))
            }
            val room = client.getRoom(roomId)
            room.invite(MatrixID.Builder("@${getNUserName(alice)}:$domain").valid())
            client.logout()
        }.join()
    } catch (e: MatrixClientRequestException) {
        e.printStackTrace()
    }
}

/**
 * Save room id to file
 *
 * @param  roomId  room id like !KaVmYkkfnthPVfFpfi:matrix.example.com
 */
fun saveRoomIdToFile(roomId: String) {
    File(Files.PUBLIC_ROOMS.path).appendText(roomId + "\n")
}

fun savePublicRoomAndUserStartEndToFile(start: Int, end: Int, roomId: String) {
    File(Files.PUBLIC_ROOMS_AND_USER_RANGE.path).appendText("$start $end $roomId\n")
}

fun saveDirectRoomAndUsersToFile(bob: Int, alice: Int, roomId: String) {
    File(Files.DIRECT_ROOMS_AND_USER_IDS.path).appendText("$bob $alice $roomId\n")
}

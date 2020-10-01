import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.joinAll
import matrix.client.MatrixClientRequestException
import matrix.client.MatrixPasswordCredentials
import matrix.client.regular.MatrixHttpClient
import matrix.room.RoomCreationOptions
import matrix.room.RoomDirectoryVisibility
import java.net.URL


/**
 * User names generator
 *
 * @param  n  number which will be added to `user_` prefix
 */
fun getNUserName(n: Int): String {
    return "user_$n"
}


/**
 * Create public room
 *
 * @param  host  url of homeserver
 * @param  userName  username which will create the room
 * @param  password  password from `userName` user
 * @param  roomName  name of room
 */
fun createPublicRoom(host: URL, userName: String, password: String, roomName: String): String {
    val client = MatrixHttpClient(host).apply {
        login(MatrixPasswordCredentials(userName, password))
    }

    val roomOptions = RoomCreationOptions.Builder()
        .setName(roomName)
        .setVisibility(RoomDirectoryVisibility.Public)
        .get()

    return client.createRoom(roomOptions).address
}

/**
 * Async join users to room
 *
 * @param  host  url of homeserver
 * @param  fromTo pair of Int's which specify from and to indexes users which will be used for test in this room
 * @param  password  password from all accounts
 * @param  roomId  id of room like !KaVmYkkfnthPVfFpfi:matrix.example.com
 */
suspend fun joinToRoom(host: URL, fromTo: Pair<Int, Int>, password: String, roomId: String) {
    try {
        val deferred = (fromTo.first..fromTo.second).map { n ->
            GlobalScope.async {
                val start = System.nanoTime()
                val client = MatrixHttpClient(host).apply {
                    login(MatrixPasswordCredentials(getNUserName(n), password))
                }
                client.joinRoom(roomId)
                writeLog(LogType.JOIN, (System.nanoTime() - start).toString())
            }
        }
        deferred.joinAll()

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
suspend fun sendMessages(
    host: URL,
    roomId: String,
    password: String,
    fromTo: Pair<Int, Int>,
    message: String = "1234567890".repeat(5),
    countOfMessages: Int = 10
) {
    try {
        val deferred = (fromTo.first..fromTo.second).map { n ->
            GlobalScope.async {
                val client = MatrixHttpClient(host).apply {
                    login(MatrixPasswordCredentials(getNUserName(n), password))
                }
                val room = client.getRoom(roomId)
                val start = System.nanoTime()
                repeat(countOfMessages) {
                    room.sendText(message)
                }
                writeLog(LogType.MESSAGE, (System.nanoTime() - start).toString())
            }
        }
        deferred.joinAll()

    } catch (e: MatrixClientRequestException) {
        e.printStackTrace()
    }
}

//fun registerUser(client: MatrixHttpClient, userName: String, password: String, ) {
//    client.register()
//}




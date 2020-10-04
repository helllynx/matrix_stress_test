import core.*
import kotlinx.coroutines.*
import matrix.client.MatrixClientRequestException
import java.io.File
import java.net.URL
import java.time.Instant
import java.time.format.DateTimeFormatter
import kotlin.concurrent.thread
import kotlin.system.measureNanoTime

fun main() {
    System.setProperty(IO_PARALLELISM_PROPERTY_NAME, 2000.toString()) // change 2000 to your max parallel limit

    File(Tests.currentLogDirPath).mkdirs()
    // run this once for creating users
    Tests().registerTestUsers(0..1000)

    // run this once to create rooms and join users to them
    Tests().prepareForPublicMassRoomsTest(50, 0..10, "user_0", Tests.password)

    Tests().publicMassRoomsTest(10000)

    // run this once to create rooms and join users to them
    Tests().prepareForDirectRoomsTest(0..1000)

    Tests().directRoomsTest(1000)
}

class Tests {

    fun registerTestUsers(usersRange: IntRange) {

        // create users with names like user_{0-1000}
        runBlocking {
            processRegisterUsersAsync(host, usersRange, password)
        }
    }

    fun publicMassRoomsTest(countOfMessages: Int = 1000) {
        // read rooms and users range which joined to this room
        val directRoomsData = File(Files.PUBLIC_ROOMS_AND_USER_RANGE.path).readLines().map {
            RoomIdForUserRange(it.split(" "))
        }

        // this method creates many user connections to matrix and start to sending messages
        runBlocking {
            GlobalScope.launch {
                processSendMessagesAsync(host, directRoomsData, password, countOfMessages)
            }.join()
        }
    }

    fun prepareForPublicMassRoomsTest(usersPerRoom: Int, roomIdRange: IntRange, userName: String, password: String) {
        // run it once for creating rooms
        runBlocking {
            processCreatePublicRoomsAsync(host, roomIdRange, userName, password)
        }

        // read room id-s from file
        val roomAddresses = File(Files.PUBLIC_ROOMS.path).readLines()

        // just temporary variable
        var currentUserId = 0

        val testData = mutableListOf<RoomIdForUserRange>()

        // create list of test data which contains RoomUserBatch objects
        for (i in roomAddresses) {
            testData.add(RoomIdForUserRange(i, currentUserId until currentUserId + usersPerRoom))
            currentUserId += usersPerRoom
        }

        // run it once when was chat created and this is join users to rooms which in
        runBlocking {
            processJoinToRoomAsync(host, testData, password)
        }
    }

    fun directRoomsTest(countOfMessages: Int) {
        // read room and users id from file
        val directRoomsData = File(Files.DIRECT_ROOMS_AND_USER_IDS.path).readLines().map {
            RoomIdForUserRange(it.split(" "))
        }

        runBlocking {
            processSendDirectMessagesAsync(host, directRoomsData, password, countOfMessages)
        }
    }

    fun prepareForDirectRoomsTest(usersRange: IntRange) {
        // run it once for creating direct rooms
        runBlocking {
            processCreateDirectRoomsAsync(host, usersRange, password)
        }

        // read room and users id from file
        val directRoomsData = File(Files.DIRECT_ROOMS_AND_USER_IDS.path).readLines().map {
            RoomIdForUserRange(it.split(" "))
        }

        // run it once when was chat created and this is join users to rooms which in
        runBlocking {
            processInviteToDirectRoomAsync(host, domain, directRoomsData, password)
        }
    }

    companion object {
        // path where current run logs will be saved
        val currentLogDirPath = "test_results/${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}/"

        // here replace with your homeserver URL
        val host = URL("https://matrix.example.com")

        // here replace with your homeserver domain
        val domain = "matrix.example.com"

        // this password will bi used for all created accounts
        val password = "password0000"
    }
}

// run coroutine with sendMessages for each element of testData, then sendMessages inside run coroutine for each user
suspend fun processSendMessagesAsync(host: URL, testData: List<RoomIdForUserRange>, password: String, countOfMessages: Int) = withContext(Dispatchers.IO) {
    try {
        val time = measureNanoTime {
            testData.map { data ->
                GlobalScope.run {
                    thread {
                        sendMessagesToPublicRooms(host, data.roomId, password, data.fromTo, countOfMessages = countOfMessages)
                    }
                }
            }.map { it.join() }
        }
        writeLog(LogType.TOTAL, "messages ${countOfMessages}: $time")
    } catch (e: MatrixClientRequestException) {
        e.printStackTrace()
    }
}

// run coroutine with sendMessages for each element of testData, then sendMessages inside run coroutine for each user
suspend fun processSendDirectMessagesAsync(host: URL, testData: List<RoomIdForUserRange>, password: String, countOfMessages: Int) = withContext(Dispatchers.IO) {
    try {
        val time = measureNanoTime {
            testData.map { data ->
                GlobalScope.run {
                    launch {
                        sendMessagesToPrivateConversation(host, data.roomId, password, data.fromTo.first, data.fromTo.last, countOfMessages = countOfMessages)
                    }
                }
            }.joinAll()
        }
        writeLog(LogType.TOTAL, "messages direct ${countOfMessages}: $time")
    } catch (e: MatrixClientRequestException) {
        e.printStackTrace()
    }
}


// run coroutine with core.joinToRoom for each element of testData, then core.joinToRoom inside run coroutine for each user
suspend fun processJoinToRoomAsync(host: URL, testData: List<RoomIdForUserRange>, password: String) = withContext(Dispatchers.IO) {
    try {
        val time = measureNanoTime {
            testData.map { data ->
                GlobalScope.run {
                    launch {
                        joinToRoom(host, data.fromTo, password, data.roomId)
                        savePublicRoomAndUserStartEndToFile(data.fromTo.first, data.fromTo.last, data.roomId)
                    }
                }
            }.joinAll()
        }
        writeLog(LogType.TOTAL, "join: $time")
    } catch (e: MatrixClientRequestException) {
        e.printStackTrace()
    }
}


// run coroutine with core.joinToRoom for each element of testData, then core.joinToRoom inside run coroutine for each user
suspend fun processRegisterUsersAsync(host: URL, fromTo: IntRange, password: String) = withContext(Dispatchers.IO) {
    try {
        val time = measureNanoTime {
            GlobalScope.run {
                launch {
                    registerUsers(host, fromTo, password)
                }
            }.join()
        }
        writeLog(LogType.TOTAL, "register ${fromTo.first} - ${fromTo.last}: $time")
    } catch (e: MatrixClientRequestException) {
        e.printStackTrace()
    }
}


// run coroutine with core.joinToRoom for each element of testData, then core.joinToRoom inside run coroutine for each user
suspend fun processCreatePublicRoomsAsync(host: URL, fromTo: IntRange, userName: String, password: String) = withContext(Dispatchers.IO) {
    try {
        val time = measureNanoTime {
            GlobalScope.run {
                launch {
                    createPublicRoom(host, fromTo, userName, password)
                }
            }.join()
        }
        writeLog(LogType.TOTAL, "create room ${fromTo.first} - ${fromTo.last}: $time")
    } catch (e: MatrixClientRequestException) {
        e.printStackTrace()
    }
}


// run coroutine with core.joinToRoom for each element of testData, then core.joinToRoom inside run coroutine for each user
suspend fun processCreateDirectRoomsAsync(host: URL, fromTo: IntRange, password: String) = withContext(Dispatchers.IO) {
    try {
        val time = measureNanoTime {
            GlobalScope.run {
                launch {
                    createPrivate1by1Room(host, fromTo, password)
                }
            }.join()
        }
        writeLog(LogType.TOTAL, "create private rooms ${fromTo.first} - ${fromTo.last}: $time")
    } catch (e: MatrixClientRequestException) {
        e.printStackTrace()
    }
}


// run coroutine with core.joinToRoom for each element of testData, then core.joinToRoom inside run coroutine for each user
suspend fun processInviteToDirectRoomAsync(host: URL, domain: String, testData: List<RoomIdForUserRange>, password: String) = withContext(Dispatchers.IO) {
    try {
        val time = measureNanoTime {
            testData.map { data ->
                GlobalScope.run {
                    launch {
                        inviteUserToDirectRoom(host, domain, data.fromTo.first, data.fromTo.last, password, data.roomId)
                    }
                }
            }.joinAll()
        }
        writeLog(LogType.TOTAL, "invite to direct: $time")
    } catch (e: MatrixClientRequestException) {
        e.printStackTrace()
    }
}

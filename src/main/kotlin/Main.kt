import Tests.Companion.timer
import core.*
import kotlinx.coroutines.*
import matrix.client.MatrixClientRequestException
import java.io.File
import java.net.URL
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.fixedRateTimer
import kotlin.math.log
import kotlin.system.measureNanoTime

fun main() {
    // this option working only on linux
    // change 2000 to your max parallel limit, but be careful, too big values can hang your pc
    System.setProperty(IO_PARALLELISM_PROPERTY_NAME, Tests.maxParallelIOCount.toString())

    File(Tests.currentLogDirPath).mkdirs()
    // run this once for creating users
//    Tests().registerTestUsers(1000..1010)

    // run this once to create rooms and join users to them
//    Tests().prepareForPublicMassRoomsTest(100, 0..10, "user_0", Tests.password)

//    Tests().publicMassRoomsTest(10000)

    // run this once to create rooms and join users to them
//    Tests().prepareForDirectRoomsTest(0..1000)

    Tests().directRoomsTest(10)
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
            processSendMessagesAsync(host, directRoomsData.slice(0..5), password, countOfMessages)
        }

        writeLog(LogType.MESSAGE_PER_SEC, messagesPerSecList.joinToString("\n"))
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
            processJoinToRoomAsync(host, testData, password, true)
        }
    }

    fun directRoomsTest(countOfMessages: Int) {
        // read room and users id from file
        val directRoomsData = File(Files.DIRECT_JOINED_ROOMS_AND_USER_IDS.path).readLines().map {
            RoomIdForUserRange(it.split(" "))
        }

        runBlocking {
            processSendDirectMessagesAsync(host, directRoomsData, password, countOfMessages)
        }

        writeLog(LogType.MESSAGE_PER_SEC, messagesPerSecList.joinToString("\n"))
    }

    fun prepareForDirectRoomsTest(usersRange: IntRange) {
        // run it once for creating direct rooms
        runBlocking {
            processCreateDirectRoomsAsync(host, usersRange, password)
        }

        // read room and users id from file
        val directRoomsData = File(Files.DIRECT_CREATED_ROOMS_AND_USER_IDS.path).readLines().map {
            RoomIdForUserRange(it.split(" "))
        }

        // run it once when was chat created and this is join users to rooms which in
        runBlocking {
            processInviteToDirectRoomAsync(host, domain, directRoomsData, password)
        }

        runBlocking {
            processJoinToRoomAsync(host, directRoomsData, password, false)
        }
    }

    companion object {
        // maximum count if parallel IO coroutines
        const val maxParallelIOCount = 400

        // path where current run logs will be saved
        val currentLogDirPath = "test_results/${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}_$maxParallelIOCount/"

        // here replace with your homeserver URL
        val host = URL("https://matrix.aura-ms.com")

        // here replace with your homeserver domain
        val domain = "matrix.aura-ms.com"

        // this password will bi used for all created accounts
        val password = "password0000"

        val counter = AtomicInteger()

        val messagesPerSecList = mutableListOf<Int>()

        val timer = fixedRateTimer("messagesCountTimer", false, 0L, 1000) {
            messagesPerSecList.add(counter.get())
            counter.set(0)
        }
    }
}

// run coroutine with sendMessages for each element of testData, then sendMessages inside run coroutine for each user
suspend fun processSendMessagesAsync(host: URL, testData: List<RoomIdForUserRange>, password: String, countOfMessages: Int) = withContext(Dispatchers.IO) {
    try {
        val time = measureNanoTime {
            testData.map { data ->
                launch {
                    sendMessagesToPublicRooms(host, data.roomId, password, data.fromTo, countOfMessages = countOfMessages)
                }
            }.joinAll()
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
                launch {
                    sendMessagesToPrivateConversation(host, data.roomId, password, data.fromTo.first, data.fromTo.last, countOfMessages = countOfMessages)
                }
            }.joinAll()
        }
        writeLog(LogType.TOTAL, "messages direct ${countOfMessages}: $time")
    } catch (e: MatrixClientRequestException) {
        e.printStackTrace()
    }
}


// run coroutine with core.joinToRoom for each element of testData, then core.joinToRoom inside run coroutine for each user
suspend fun processJoinToRoomAsync(host: URL, testData: List<RoomIdForUserRange>, password: String, publicRoom: Boolean) = withContext(Dispatchers.IO) {
    try {
        val time = measureNanoTime {
            testData.map { data ->
                launch {
                    joinToRoom(host, data.fromTo, password, data.roomId)
                    if (publicRoom)
                        savePublicRoomAndUserStartEndToFile(data.fromTo.first, data.fromTo.last, data.roomId)
                    else
                        saveJoinedDirectRoomAndUsersToFile(data.fromTo.first, data.fromTo.last, data.roomId)
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
            launch {
                registerUsers(host, fromTo, password)
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
            launch {
                createPublicRoom(host, fromTo, userName, password)
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
            launch {
                createPrivate1by1Room(host, fromTo, password)
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
                launch {
                    inviteUserToDirectRoom(host, domain, data.fromTo.first, data.fromTo.last, password, data.roomId)
                }
            }.joinAll()
        }
        writeLog(LogType.TOTAL, "invite to direct: $time")
    } catch (e: MatrixClientRequestException) {
        e.printStackTrace()
    }
}

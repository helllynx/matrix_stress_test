import kotlinx.coroutines.*
import matrix.client.MatrixClientRequestException
import java.io.File
import java.net.URL
import kotlin.system.measureNanoTime


fun main() {
    // here replace with your homeserver URL
    val host = URL("")

    val domain = ""

    // this password will bi used for all created accounts
    val password = ""

    // read room id-s from file
    val roomAddresses = File(Files.PUBLIC_ROOMS.path).readLines()

    // how mach users join and flood in room
    val usersPerRoom = 50

    // just temporary variable
    var currentUserId = 0

    // count of messages peer user
    val countOfMessages = 1000

    val countOfUsersForDirectTest = 500

    val testData = mutableListOf<RoomIdForUserRange>()

    // create list of test data which contains RoomUserBatch objects
    for (i in roomAddresses) {
        testData.add(RoomIdForUserRange(i, currentUserId..(currentUserId + usersPerRoom)))
        currentUserId += usersPerRoom + 1
    }

//    // run it once for creating rooms
//    runBlocking {
//        processCreatePublicRoomsAsync(host, 0..10, "admin", "admin")
//    }

    // read room id-s from file
    val directRoomsData = File(Files.DIRECT_ROOMS_AND_USER_IDS.path).readLines().map {
        RoomIdForUserRange(it.split(" "))
    }

    // run it once for creating direct rooms
//    runBlocking {
//        processCreateDirectRoomsAsync(host, 0..100, "admin", "admin")
//    }
//
//    runBlocking {
//        processRegisterUsersAsync(host, 0..1000, password)
//    }
//
    // run it once when was chat created and this is join users to rooms which in
    runBlocking {
        processInviteToDirectRoomAsync(host, domain, directRoomsData, password)
    }

    // run it once when was chat created and this is join users to rooms which in
//    runBlocking {
//        processJoinToRoomAsync(host, testData, password)
//    }
//
//    // this method creates many user connections to matrix and start to sending messages
//    runBlocking {
//        GlobalScope.launch {
//            processSendMessagesAsync(host, testData, password, countOfMessages)
//        }.join()
//    }
}


// run coroutine with sendMessages for each element of testData, then sendMessages inside run coroutine for each user
suspend fun processSendMessagesAsync(host: URL, testData: List<RoomIdForUserRange>, password: String, countOfMessages: Int) = withContext(Dispatchers.IO) {
    try {
        val time = measureNanoTime {
            testData.map { data ->
                GlobalScope.run {
                    launch {
                        sendMessagesToPublicRooms(host, data.roomId, password, data.fromTo, countOfMessages = countOfMessages)
                    }
                }
            }.joinAll()
        }
        writeLog(LogType.TOTAL, "messages ${countOfMessages}: $time")
    } catch (e: MatrixClientRequestException) {
        e.printStackTrace()
    }
}


// run coroutine with joinToRoom for each element of testData, then joinToRoom inside run coroutine for each user
suspend fun processJoinToRoomAsync(host: URL, testData: List<RoomIdForUserRange>, password: String) = withContext(Dispatchers.IO) {
    try {
        val time = measureNanoTime {
            testData.map { data ->
                GlobalScope.run {
                    launch {
                        joinToRoom(host, data.fromTo, password, data.roomId)
                    }
                }
            }.joinAll()
        }
        writeLog(LogType.TOTAL, "join: $time")
    } catch (e: MatrixClientRequestException) {
        e.printStackTrace()
    }
}


// run coroutine with joinToRoom for each element of testData, then joinToRoom inside run coroutine for each user
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


// run coroutine with joinToRoom for each element of testData, then joinToRoom inside run coroutine for each user
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


// run coroutine with joinToRoom for each element of testData, then joinToRoom inside run coroutine for each user
suspend fun processCreateDirectRoomsAsync(host: URL, fromTo: IntRange, userName: String, password: String) = withContext(Dispatchers.IO) {
    try {
        val time = measureNanoTime {
            GlobalScope.run {
                launch {
                    createPrivate1by1Room(host, fromTo, userName, password)
                }
            }.join()
        }
        writeLog(LogType.TOTAL, "create private rooms ${fromTo.first} - ${fromTo.last}: $time")
    } catch (e: MatrixClientRequestException) {
        e.printStackTrace()
    }
}


// run coroutine with joinToRoom for each element of testData, then joinToRoom inside run coroutine for each user
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

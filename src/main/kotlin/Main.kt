import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.runBlocking
import matrix.client.MatrixClientRequestException
import java.io.File
import java.net.URL


fun main() {
    // here replace with your homeserver URL
    val host = URL("")

    // this password will bi used for all created accounts
    val password = ""

    // read room id-s from file
    val roomAddresses = File(Files.ROOMS.path).readLines()

    // how mach users join and flood in room
    val usersPerRoom = 50

    // just temporary variable
    var currentUserId = 0

    // count of messages peer user
    val countOfMessages = 10

    val testData = mutableListOf<RoomUserBatch>()

    // create list of test data which contains RoomUserBatch objects
    for (i in roomAddresses) {
        testData.add(RoomUserBatch(i, currentUserId to currentUserId + usersPerRoom))
        currentUserId += usersPerRoom + 1
    }

    // run it once for creating rooms
//    runBlocking {
//        processCreatePublicRoomAsync(host, 0 to 10, "admin", "admin")
//    }

//    runBlocking {
//        processRegisterUsersAsync(host, 0 to 1000, password)
//    }

    // run it once when was chat created and this is join users to rooms which in
//    runBlocking {
//        processJoinToRoomAsync(host, testData, password)
//    }

    // this method creates many user connections to matrix and start to sending messages
    runBlocking {
        processSendMessagesAsync(host, testData, password, countOfMessages)
    }
}


// run coroutine with sendMessages for each element of testData, then sendMessages inside run coroutine for each user
suspend fun processSendMessagesAsync(host: URL, testData: List<RoomUserBatch>, password: String, countOfMessages: Int) {
    try {
        val start = System.nanoTime()
        testData.map { data ->
            GlobalScope.async {
                sendMessages(host, data.roomId, password, data.fromTo, countOfMessages = countOfMessages)
            }
        }.joinAll()
        writeLog(LogType.TOTAL, "messages ${countOfMessages}: ${(System.nanoTime() - start)}")
    } catch (e: MatrixClientRequestException) {
        e.printStackTrace()
    }
}


// run coroutine with joinToRoom for each element of testData, then joinToRoom inside run coroutine for each user
suspend fun processJoinToRoomAsync(host: URL, testData: List<RoomUserBatch>, password: String) {
    try {
        val start = System.nanoTime()
        testData.map { data -> GlobalScope.async { joinToRoom(host, data.fromTo, password, data.roomId) } }.joinAll()
        writeLog(LogType.TOTAL, "join: ${(System.nanoTime() - start)}")
    } catch (e: MatrixClientRequestException) {
        e.printStackTrace()
    }
}


// run coroutine with joinToRoom for each element of testData, then joinToRoom inside run coroutine for each user
suspend fun processRegisterUsersAsync(host: URL, fromTo: Pair<Int, Int>, password: String) {
    try {
        val start = System.nanoTime()
        GlobalScope.async { registerUsers(host, fromTo, password) }.join()
        writeLog(LogType.TOTAL, "register ${fromTo.first} - ${fromTo.second}: ${(System.nanoTime() - start)}")
    } catch (e: MatrixClientRequestException) {
        e.printStackTrace()
    }
}


// run coroutine with joinToRoom for each element of testData, then joinToRoom inside run coroutine for each user
suspend fun processCreatePublicRoomAsync(host: URL, fromTo: Pair<Int, Int>, userName: String, password: String) {
    try {
        val start = System.nanoTime()
        GlobalScope.async { createPublicRoom(host, fromTo, userName, password) }.join()
        writeLog(LogType.TOTAL, "create room ${fromTo.first} - ${fromTo.second}: ${(System.nanoTime() - start)}")
    } catch (e: MatrixClientRequestException) {
        e.printStackTrace()
    }
}

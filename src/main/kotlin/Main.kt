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
    val roomAddresses = File("rooms.txt").readLines()

    // how mach users join and flood in room
    val usersPerRoom = 50

    // just temporary variable
    var currentUserId = 0

    val testData = mutableListOf<RoomUserBatch>()

    // create list of test data which contains RoomUserBatch objects
    for (i in roomAddresses) {
        testData.add(RoomUserBatch(i, currentUserId to currentUserId + usersPerRoom))
        currentUserId += usersPerRoom + 1
    }

    // this method creates many user connections to matrix and start to sending messages
    runBlocking {
        processSendMessagesAsync(host, testData, password)
    }

    // run it once when was chat created and this is jpon users to rooms which in
//    runBlocking {
//        processJoinToRoomAsync(host, testData, password)
//    }

    // run it once to create rooms
//    for (i in 10..20) {
//        println(createPublicRoom(host, "admin", password, "test$i"))
//    }
}

// run coroutine with sendMessages for each element of testData, then sendMessages inside run coroutine for each user
suspend fun processSendMessagesAsync(host: URL, testData: List<RoomUserBatch>, password: String) {
    try {
        val deferred = testData.map { data ->
            GlobalScope.async {
                sendMessages(host, data.roomId, password, data.fromTo)
            }
        }
        deferred.joinAll()

    } catch (e: MatrixClientRequestException) {
        e.printStackTrace()
    }
}

// run coroutine with joinToRoom for each element of testData, then joinToRoom inside run coroutine for each user
suspend fun processJoinToRoomAsync(host: URL, testData: List<RoomUserBatch>, password: String) {
    try {
        val deferred = testData.map { data ->
            GlobalScope.async {
                joinToRoom(host, data.fromTo, password, data.roomId)
            }
        }
        deferred.joinAll()

    } catch (e: MatrixClientRequestException) {
        e.printStackTrace()
    }
}

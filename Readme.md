## Matrix homeserver stress test

In this app I try to create some load test for Matrix homeserver. 

I use code from this repo https://github.com/kamax-matrix/matrix-java-sdk

Thank you [maxidorius](https://github.com/maxidorius).

Pull requests are welcome!

### Quick instruction

First of all, you need to create users. To do this, run the following code:

```kotlin
fun main() {
    // here replace with your homeserver URL
    val host = URL("https://matrix.example.com")

    // this password will bi used for all created accounts
    val password = "somepassword"

    runBlocking {
        processRegisterUsersAsync(host, 0 to 1000, password)
    }
}
```

This code create 1000 users with username `user_{here id between 0 and 1000}`.

Next you need to create rooms:

```kotlin
fun main() {
    // here replace with your homeserver URL
    val host = URL("https://matrix.example.com")

    // this password will bi used for all created accounts
    val password = "somepassword"

    // run it once for creating rooms
    runBlocking {
        processСreatePublicRoomAsync(host, 0 to 10, "{here any user username}", password)
    }
}
```

This code create 10 room with room name `room_{here id between 0 and 10}`. All room id's will be saved to `rooms.txt` file.

Next you need to join users to rooms:

```kotlin
fun main() {
    // here replace with your homeserver URL
    val host = URL("https://matrix.example.com")

    // this password will bi used for all created accounts
    val password = "somepassword"

    // read room id-s from file
    val roomAddresses = File(Files.ROOMS.path).readLines()

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

    // run it once when was chat created and this is join users to rooms which in
    runBlocking {
        processJoinToRoomAsync(host, testData, password)
    }
}
```

The final step to testing public rooms is send messages:

```kotlin
fun main() {
    // here replace with your homeserver URL
    val host = URL("https://matrix.example.com")

    // this password will bi used for all created accounts
    val password = "somepassword"

    // read room id-s from file
    val roomAddresses = File(Files.ROOMS.path).readLines()

    // how mach users join and flood in room
    val usersPerRoom = 50

    // just temporary variable
    var currentUserId = 0

    // count of messages peer user
    val countOfMessages = 2000

    val testData = mutableListOf<RoomUserBatch>()

    // create list of test data which contains RoomUserBatch objects
    for (i in roomAddresses) {
        testData.add(RoomUserBatch(i, currentUserId to currentUserId + usersPerRoom))
        currentUserId += usersPerRoom + 1
    }

    // this method creates many user connections to matrix and start to sending messages
    runBlocking {
        processSendMessagesAsync(host, testData, password, countOfMessages)
    }
}
```


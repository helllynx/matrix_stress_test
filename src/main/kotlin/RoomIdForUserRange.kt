/**
 * Data class for test data which contains room and id-s of users.
 *
 * @param  roomId  room id like !KaVmYkkfnthPVfFpfi:matrix.example.com
 * @param  fromTo pair of Ints which specify from and to indexes users which will be used for test in this room
 */
data class RoomIdForUserRange(var roomId: String, var fromTo: IntRange) {
    constructor(data: List<String>) : this(data[2], data[0].toInt()..(data[1].toInt()))
}


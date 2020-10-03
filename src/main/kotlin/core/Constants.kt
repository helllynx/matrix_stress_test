package core

enum class Files(val path: String) {
    PUBLIC_ROOMS("data/public_rooms.txt"),
    PUBLIC_ROOMS_AND_USER_RANGE("data/public_rooms_range.txt"),
    DIRECT_ROOMS_AND_USER_IDS("data/direct_rooms.txt")
}
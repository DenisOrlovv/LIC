object Statistics {
    private const val STATS_FILE_NAME = "stats"

    fun init(){}

    fun getStats(): Pair<Int, Int> {
        var stats = FileAccess.readAllLinesFromFile(STATS_FILE_NAME)
        if (stats.isEmpty()){
            FileAccess.writeToFile(STATS_FILE_NAME, "0")
            FileAccess.writeToFile(STATS_FILE_NAME, "0")
            stats = FileAccess.readAllLinesFromFile(STATS_FILE_NAME)
        }
        return Pair(stats[0].toInt(), stats[1].toInt())
    }

    fun writeStats(coins: Int, games: Int) {
        FileAccess.deleteAllLinesFromFile(STATS_FILE_NAME)
        FileAccess.writeToFile(STATS_FILE_NAME, "$games")
        FileAccess.writeToFile(STATS_FILE_NAME, "$coins")
    }
}
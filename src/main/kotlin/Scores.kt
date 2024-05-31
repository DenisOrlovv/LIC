

fun main(){
    val scores = listOf(
        Scores.Score(1, "Alice", 95),
        Scores.Score(2, "Bob", 88),
        Scores.Score(31, "Charlie", 92),
        Scores.Score(40, "Diana", 85),
        Scores.Score(5, "Ethan", 90),
        Scores.Score(6, "Fiona", 87),
        Scores.Score(0, "George", 94),
        Scores.Score(8, "Hannah", 89),
        Scores.Score(9, "Ian", 91),
        Scores.Score(10, "Jill", 86),
        Scores.Score(11, "Kevin", 83),
        Scores.Score(20, "Laura", 84),
        Scores.Score(13, "Mike", 80),
        Scores.Score(11, "Nina", 82),
        Scores.Score(10, "Oscar", 78),
        Scores.Score(16, "Paul", 81),
        Scores.Score(17, "Quinn", 76),
        Scores.Score(18, "Rachel", 79),
        Scores.Score(19, "Steve", 77),
        Scores.Score(20, "Tina", 75))

    val teste = Scores.sortScores(scores)
    val pao = Scores.setScore("denis", 100, teste)
    println(pao)

}


object Scores {
    data class Score(var position: Int, val name: String, val score: Int)

    private const val SCORES_FILE_NAME = "scores"


    /**
     * writes a list of scores to the folder
     */
    fun writeScore(listOfScores: List<Score>) {
        val sortedLines = listOfScores.map { "${"%02d".format(it.position)}-${it.name}-${it.score}" }
        //clears the file so it can write new leaderboard
        FileAccess.deleteAllLinesFromFile(SCORES_FILE_NAME)
        for (line in sortedLines) {
            FileAccess.writeToFile(SCORES_FILE_NAME, line)
        }
    }

    fun sortScores(listOfScores: List<Score>): List<Score> {
        val scores = listOfScores.sortedByDescending { it.score }
        scores.forEachIndexed { index, score ->
            score.position = index + 1
        }
        val topScores = scores.take(10)
        return topScores
    }

    /**
     * Does all the work of parsing the data into a List<Score> from the txt
     */
    fun readScores(): MutableList<Score> {
        val scores = mutableListOf<Score>()
        val lines = FileAccess.readAllLinesFromFile(SCORES_FILE_NAME)
        for (line in lines) {
            if (line.isNotBlank()) {
                val cutData = line.split("-")
                val position = cutData[0].toInt()
                val name = cutData[1]
                val score = cutData[2].toInt()
                scores.add(Score(position, name, score))
            }
        }
        return scores
    }


    /**
     * Tells if given score can get in the top 10 of the scores.txt
     */
    fun isNewTopScore(score: Int): Boolean {
        val scores = readScores()
        val newScore = Score(0, "test", score)
        val updatedScores = (scores + newScore).sortedByDescending { it.score }
        return updatedScores.indexOf(newScore) < 10
    }

    fun setScore(name:String, score:Int, scores:List<Score>):List<Score>{
        val newScore = Score(0, name, score)
        val updatedScores = scores + newScore
        val sortedScores = sortScores(updatedScores)
        return sortedScores
    }
}


fun main(){
    repeat(100){
        val letters = ('a'..'z') + ('A'..'Z')
        val nome = (1..5).map { letters.random() }.joinToString("")
        val randomScore = (0..10000).random()
        if (Scores.isNewTopScore(randomScore)){
            Scores.writeScore(nome, randomScore)
        }
        //println(Scores.Info(nome, randomScore))
    }
}


object Scores {
    data class Score(var position: Int, val name: String, val score: Int)

    private const val SCORES_FILE_NAME = "scores"


    /**
     * Does all the work regarding the writing process of each score
     */
    fun writeScore(name: String, score: Int) {
        //updates the score file with the new score and sorts it
        val scores = readScores()
        val newScore = Score(0, name, score)
        val updatedScores = (scores + newScore).sortedByDescending { it.score }

        //sets the right leaderboard place to each score
        updatedScores.forEachIndexed { index, score ->
            score.position = index + 1
        }
        //only uses the 10 top ones
        val topScores = updatedScores.take(10)
        val sortedLines = topScores.map { "${"%02d".format(it.position)}-${it.name}-${it.score}" }
        //clears the file so it can write new leaderboard
        FileAccess.deleteAllLinesFromFile(SCORES_FILE_NAME)
        for (line in sortedLines) {
            FileAccess.writeToFile(SCORES_FILE_NAME, line)
        }
    }


    /**
     * Does all the work of parsing the data into a List<Score> from the txt
     */
    fun readScores(): List<Score> {
        val scores = mutableListOf<Score>()
        val lines = FileAccess.readAllLinesFromFile(SCORES_FILE_NAME)
        for (line in lines) {
            val cutData = line.split("-")
            val position = cutData[0].toInt()
            val name = cutData[1]
            val score = cutData[2].toInt()
            scores.add(Score(position, name, score))
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
}
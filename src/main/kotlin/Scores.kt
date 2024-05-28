fun main(){

}


object Scores {
    data class Info (val name: String, val score: Int)
    data class Score(var position: Int, val info: Info)

    private const val SCORES_FILE_NAME = "scores"

    fun writeScores(content: Info){
        val data = "0-${content.name}-${content.score}"
        FileAccess.writeToFile(SCORES_FILE_NAME, data)
        sortScores()
    }

    fun readScores(): List<Score> {
        val scores = mutableListOf<Score>()
        val lines = FileAccess.readAllLinesFromFile(SCORES_FILE_NAME)
        for (line in lines){
            val cutData = line.split("-")
            val position = cutData[0].toInt()
            val name = cutData[1]
            val score = cutData[2].toInt()
            scores.add(Score(position, Info(name, score)))
        }
        return scores
    }

    fun sortScores(){
        val scores = readScores()
        val sortedScores = scores.sortedByDescending { it.info.score }
        for (score in sortedScores){
            score.position = sortedScores.indexOf(score) + 1
        }
        val sortedLines = sortedScores.map { "${"%02d".format(it.position)}-${it.info.name}-${it.info.score}" }
        FileAccess.deleteAllLinesFromFile(SCORES_FILE_NAME)
        for (line in sortedLines){
            FileAccess.writeToFile(SCORES_FILE_NAME, line)
        }
    }

    fun isNewTopScore(score: Int): Boolean {
        val scores = readScores()
        val newScore = Score(0,Info("test", score))
        val updatedScores = (scores + newScore).sortedByDescending { it.info.score }
        return updatedScores.indexOf(newScore) < 10
    }
}
import java.io.File

fun main(){
    println(FileAccess.deleteLineFromFile("teste", 7))
}



object FileAccess {

    private fun createTxtFile(fileName: String, content: String){
        val file = File(fileName)
        file.writeText(content)
    }

    fun writeToFile(fileName: String, content: String){
        val file = File(fileName)
        val contentWithNewLine = content + "\n"
        if (file.exists()) {
            file.appendText(contentWithNewLine)
        }else
        {
            createTxtFile(fileName, contentWithNewLine)
        }
    }

    fun readAllLinesFromFile(fileName: String): List<String>{
        val file = File(fileName)
        return if (file.exists() && file.readLines().isNotEmpty()) {
            file.readLines()
        }else emptyList()
    }

    fun readLineFromFile(fileName: String, lineNumber: Int) : String?{
        val file = File(fileName)
        val lines = file.readLines()
        return if (lineNumber in 1..lines.size) {
            lines[lineNumber-1]
        } else {
            null
        }
    }

    fun deleteAllLinesFromFile(fileName: String){
        val file = File(fileName)
        val lines = file.readLines().toMutableList()
        if (file.exists() && file.readLines().isNotEmpty()) {
            for (line in lines) file.writeText("")
        }
    }

    fun deleteLineFromFile(fileName:String, lineNumber: Int): Boolean{
        val file = File(fileName)
        val lines = file.readLines().toMutableList()
        if (lineNumber in 1..lines.size) {
            lines.removeAt(lineNumber-1)
            file.writeText(lines.joinToString("\n"))
            return true
        } else return false
    }
}
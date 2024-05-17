object TUI {

    fun init() {
        LCD.init()
        KBD.init()
    }
    const val NONE = KBD.NONE

    fun clear() {
        LCD.clear()
    }

    fun read(timeOut: Long): Char {
        return KBD.waitKey(timeOut)
    }

    fun write(letter: Char) {
        LCD.write(letter)
    }

    fun cursor(line: Int, column: Int){
        LCD.cursor(line, column)
    }

    fun write(string: String){
        LCD.write(string)
    }
}
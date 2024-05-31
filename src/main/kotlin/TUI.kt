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
    fun getKey(): Char {
        return KBD.getKey()
    }

    fun write(letter: Char) {
        LCD.write(letter)
    }

    fun writeCMD(data: Int){
        LCD.writeCMD(data)
    }

    fun writeDATA(data:Int){
        LCD.writeDATA(data)
    }

    fun cursor(line: Int, column: Int){
        LCD.cursor(line, column)
    }

    fun write(string: String){
        LCD.write(string)
    }
}
import isel.leic.utils.Time

fun main(){

}
object LCD { // Escreve no LCD usando a interface a 8 bits.
    private const val LINES = 2
    private const val COLS = 16 // Dimensão do display.
    private const val SERIAL_INTERFACE = true // Define se a interface é Série ou Paralela
    private const val RS = 0x40
    private const val RS_TRUE = 1
    private const val RS_FALSE = 0
    private const val ENABLE = 0X20
    private const val DATA_MASK = 0x0F
    private const val CLOCK = 0x10

    // Escreve um byte de comando/dados no LCD em paralelo
    private fun writeByteParallel(rs: Boolean, data: Int) {
        if (rs) HAL.setBits(RS)
        else HAL.clrBits(RS)
        val dataIn = data shr 4

        HAL.setBits(ENABLE)

        HAL.writeBits(DATA_MASK, dataIn)

        HAL.setBits(CLOCK)      //clock
        HAL.clrBits(CLOCK)      //clock

        HAL.writeBits(DATA_MASK, data)

        HAL.setBits(CLOCK)      //clock
        HAL.clrBits(CLOCK)      //clock

        HAL.clrBits(ENABLE)
    }


    // Escreve um byte de comando/dados no LCD em série
    private fun writeByteSerial(rs: Boolean, data: Int) {
        val shiftedData = if(rs) data.shl(1) + RS_TRUE else data.shl(1) + RS_FALSE
        SerialEmitter.send(SerialEmitter.Destination.LCD ,shiftedData,9)
    }


    // Escreve um byte de comando/dados no LCD
    private fun writeByte(rs: Boolean, data: Int) {
        if (SERIAL_INTERFACE) writeByteSerial(rs, data)
        else writeByteParallel(rs, data)
    }


    // Escreve um comando no LCD
    fun writeCMD(data: Int) {
        writeByte(false, data)
    }


    // Escreve um dado no LCD
    fun writeDATA(data: Int) {
        writeByte(true, data)
    }


    // Envia a sequência de iniciação para comunicação a 8 bits.
    fun init() {
        Time.sleep(100)
        writeCMD(0x30)
        Time.sleep(10)
        writeCMD(0x30)
        Time.sleep(10)
        writeCMD(0x30)
        writeCMD(0x38)
        writeCMD(0x08)
        writeCMD(0x01)
        writeCMD(0x06)
        writeCMD(0x0D) // 0x0F for blinking cursor, OX0D for cursor off
    }


    // Escreve um caráter na posição corrente.
    fun write(c: Char) {
        writeDATA(c.code)
    }


    // Escreve uma string na posição corrente.
    fun write(text: String) {
        for (i in text.indices){
            write(text[i])
        }
    }


    // Envia comando para posicionar cursor (‘line’:0..LINES-1 , ‘column’:0..COLS-1)
    fun cursor(line: Int, column: Int) {
        if (line == 0) {
            writeCMD(0x80 + column)
        } else writeCMD(0xC0 + column)
    }


    // Envia comando para limpar o ecrã e posicionar o cursor em (0,0)
    fun clear() {
        Time.sleep(5)
        writeCMD(0x01)
    }
}
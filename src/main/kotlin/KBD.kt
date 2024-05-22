import isel.leic.utils.Time

const val Kval = 0x10
const val Kack = 0x80
const val KeyK = 0x0F

fun main(){

}
object KBD {
    const val NONE = 0.toChar()

    // Inicia a classe
    fun init() {
        HAL.init()
        HAL.clrBits(Kack)
    }

    // Retorna de imediato a tecla premida ou NONE se não há tecla premida.
    fun getKey(): Char {
        if (HAL.isBit(Kval)) {
            val key = HAL.readBits(KeyK)
            while (HAL.isBit(Kval))
                HAL.setBits(Kack)
            HAL.clrBits(Kack)
            return when (key) {
                0x00 -> '1'
                0x01 -> '4'
                0x02 -> '7'
                0x03 -> '*'
                0x04 -> '2'
                0x05 -> '5'
                0x06 -> '8'
                0x07 -> '0'
                0x08 -> '3'
                0x09 -> '6'
                0x0A -> '9'
                0x0B -> '#'
                else -> NONE
            }

        } else
            return NONE
    }

    // Retorna a tecla premida, caso ocorra antes do ‘timeout’ (representado em milissegundos), ou NONE caso contrário.
      fun waitKey(timeout: Long): Char {
        val timeEnd = Time.getTimeInMillis() + timeout
        var timeCurrent = Time.getTimeInMillis()
        while(timeEnd >  timeCurrent){
            val key = getKey()
            if (key != NONE) return key
            else timeCurrent = Time.getTimeInMillis()
        }
        return NONE
    }
}

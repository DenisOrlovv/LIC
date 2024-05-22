import isel.leic.UsbPort

fun main(){
    HAL.init()
}
object HAL { // Virtualiza o acesso ao sistema UsbPort
    var currentValue = 0
    // Inicia a classe
    fun init(){
        currentValue = 0x00
        UsbPort.write(currentValue)
    }

    // Coloca os bits representados por mask no valor lógico ‘1’
    fun setBits(mask: Int){
        currentValue = currentValue or mask
        UsbPort.write(currentValue)
    }

    // Escreve nos bits representados por mask os valores dos bits correspondentes em value
    fun writeBits(mask: Int, value: Int) {
        val new = value and mask
        currentValue = currentValue and mask.inv()
        currentValue = currentValue or new
        UsbPort.write(currentValue)
    }
    // Coloca os bits representados por mask no valor lógico ‘0’
    fun clrBits(mask: Int){
        currentValue = currentValue and mask.inv()
        UsbPort.write(currentValue)
    }

    // Retorna true se o bit tiver o valor lógico ‘1’
    fun isBit(mask: Int): Boolean{
        var bits = UsbPort.read()
        bits = bits and mask
        return bits != 0
    }

    // Retorna os valores dos bits representados por mask presentes no UsbPort
    fun readBits(mask: Int): Int{
        var bits = UsbPort.read()
        bits = bits and mask
        return bits
    }
}
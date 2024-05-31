import isel.leic.utils.Time

fun main(){
    ScoreDisplay.init()
    ScoreDisplay.off(false)
    ScoreDisplay.setScore(123456)

}
object ScoreDisplay {
    var currentValue = Array(6){0xF}
    // Inicia a classe, estabelecendo os valores iniciais.
    fun init() {
        SerialEmitter.init()
    }
    // Envia comando para atualizar o valor do mostrador de pontuação
    fun setScore(value: Int) {
        var numberToString = value.toString().map{ it.toString().toInt() }
        numberToString = numberToString.reversed()
        val currentValueRev = currentValue.reversed()

        while (numberToString.size < currentValueRev.size) numberToString = numberToString + 0xF

        for (i in numberToString.indices){
            if (numberToString[i] != currentValueRev[i]) {
                val temp = i + numberToString[i].shl(3)
                SerialEmitter.send(SerialEmitter.Destination.SCORE, temp, 7)
                currentValue[5-i] = numberToString[i]
            }
        }
        SerialEmitter.send(SerialEmitter.Destination.SCORE, 0x06,7)
    }
    // Envia comando para desativar/ativar a visualização do mostrador de pontuação
    fun off(value: Boolean){
        val command = 7
        if (value) SerialEmitter.send(SerialEmitter.Destination.SCORE, command + 8, 7 )
        else SerialEmitter.send(SerialEmitter.Destination.SCORE, command , 7 )
    }

    fun send( data:Int, size:Int){
        SerialEmitter.send(SerialEmitter.Destination.SCORE,data,size)
    }
}
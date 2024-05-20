object ScoreDisplay {
    var currentValue = 0
    // Inicia a classe, estabelecendo os valores iniciais.
    fun init() {
        currentValue = 0
        SerialEmitter.init()
    }
    // Envia comando para atualizar o valor do mostrador de pontuação
    fun setScore(value: Int) {
        var numberToString = value.toString().map{ it.toString().toInt() }
        numberToString = numberToString.reversed()
        var currentNumberToString = currentValue.toString().map{ it.toString().toInt() }
        currentNumberToString = currentNumberToString.reversed()

        if(currentValue == 0){
            while (currentNumberToString.size <= 6) currentNumberToString = currentNumberToString + 0x0
        }
        else{
            while (currentNumberToString.size <= 6) currentNumberToString = currentNumberToString + 0xF
        }
        while (numberToString.size < currentNumberToString.size) numberToString = numberToString + 0xF

        for (i in numberToString.indices){
            if (numberToString[i] != currentNumberToString[i]) {
                val temp = i + numberToString[i].shl(3)
                SerialEmitter.send(SerialEmitter.Destination.SCORE, temp, 7)
            }
        }

        currentValue = value
        SerialEmitter.send(SerialEmitter.Destination.SCORE, 0x06,7)
    }
    // Envia comando para desativar/ativar a visualização do mostrador de pontuação
    fun off(value: Boolean){
        val command = 7
        if (value) SerialEmitter.send(SerialEmitter.Destination.SCORE, command + 8, 7 )
        else SerialEmitter.send(SerialEmitter.Destination.SCORE, command , 7 )
    }
}
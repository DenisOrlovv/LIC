object ScoreDisplay {
    var currentValue = 0
    // Inicia a classe, estabelecendo os valores iniciais.
    fun init() {
        currentValue = 0x00
        SerialEmitter.init()
    }
    // Envia comando para atualizar o valor do mostrador de pontuação
    fun setScore(value: Int) {
        var numberToString = value.toString().map{ it.toString().toInt() }
        numberToString = numberToString.reversed()

        var currentNumberToString = currentValue.toString().map{ it.toString().toInt() }
        currentNumberToString = currentNumberToString.reversed()


        for (i in 0..numberToString.size){
            if (numberToString[i] != currentNumberToString[i]) {
                var temp = i.shl(4)
                temp += numberToString[i]
                SerialEmitter.send(SerialEmitter.Destination.SCORE, temp, 7)
            }
        }


        currentValue = value
        SerialEmitter.send(SerialEmitter.Destination.SCORE, 0x60,7)
    }
    // Envia comando para desativar/ativar a visualização do mostrador de pontuação
    fun off(value: Boolean){
        val command = 7
        if (value) SerialEmitter.send(SerialEmitter.Destination.SCORE, command.shl(4) + 1, 7 )
        else SerialEmitter.send(SerialEmitter.Destination.SCORE, command.shl(4) + 0, 7 )
    }
}
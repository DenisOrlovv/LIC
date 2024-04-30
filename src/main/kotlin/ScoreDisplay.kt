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
        for (i in 0..numberToString.size){
            var temp = i.shl(4)
            temp += numberToString[i]
            SerialEmitter.send(SerialEmitter.Destination.SCORE, temp,7)
            println(temp)
        }


        currentValue = value
        SerialEmitter.send(SerialEmitter.Destination.SCORE, 0x60,7)
    }
    // Envia comando para desativar/ativar a visualização do mostrador de pontuação
    fun off(value: Boolean){

    }
}
object ScoreDisplay {
    var currentValue = 0
    // Inicia a classe, estabelecendo os valores iniciais.
    fun init() {
        currentValue = 0x00
        SerialEmitter.init()
    }
    // Envia comando para atualizar o valor do mostrador de pontuação
    fun setScore(value: Int) {

        var dataToSend = 0


        val currentValueAsArray = value.toArray()
        val lastValueAsArray = currentValue.toArray()




        currentValue = value
        SerialEmitter.send(SerialEmitter.Destination.SCORE, dataToSend,7)
    }
    // Envia comando para desativar/ativar a visualização do mostrador de pontuação
    fun off(value: Boolean){

    }
}
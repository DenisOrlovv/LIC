object SerialEmitter { // Envia tramas para os diferentes módulos Serial Receiver.

    enum class Destination {LCD, SCORE}

    private const val LCD_Sel = 0x01
    private const val WriteBitsMask = 0x08
    private const val Clock = 0x10
    private const val SC_Sel = 0x02
    // Inicia a classe
    fun init() {
        HAL.init()
    }
    // Envia uma trama para o SerialReceiver identificado o destino em addr,os bits de dados em
    // ‘data’ e em size o número de bits a enviar.
    fun send(addr: Destination, data: Int, size : Int) {

        if (addr == Destination.LCD){   // sets the select bits to 0 accordingly, to start the hardware
            HAL.clrBits(LCD_Sel)
        }else HAL.clrBits(SC_Sel)

        var parity = 0

        for (i in 0..<size){
            HAL.clrBits(Clock) //clock

            val dataShifted = data.shr(i)
            val dataCheck = 0x01

             //escreve bit por bit na mascara
            if( dataShifted and dataCheck == 1) {
                HAL.setBits(WriteBitsMask)
                parity++
            }  else HAL.clrBits(WriteBitsMask)//Verifica se o numero de bits a 1 é par ou impar

            HAL.setBits(Clock) //clock
        }

        HAL.clrBits(Clock) //clock

        if(parity%2 == 0) HAL.clrBits(WriteBitsMask)  //Checks the parity, and if even sets temp bit to 0, if not set to 1
        else HAL.setBits(WriteBitsMask)

        HAL.setBits(Clock) //clock

        HAL.setBits(LCD_Sel)  // sets the select bits back to 1
        HAL.setBits(SC_Sel)

        HAL.clrBits(Clock) //clock
    }
}
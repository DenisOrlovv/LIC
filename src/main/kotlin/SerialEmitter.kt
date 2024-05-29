import isel.leic.utils.Time

fun main(){
    SerialEmitter.send(SerialEmitter.Destination.LCD, 0b111000111, 9)
}

object SerialEmitter { // Envia tramas para os diferentes módulos Serial Receiver.

    enum class Destination {LCD, SCORE}

    private const val LCD_Sel = 0x01
    private const val SDX = 0x08
    private const val SCLK = 0x10
    private const val SC_Sel = 0x02
    // Envia uma trama para o SerialReceiver identificado o destino em addr,os bits de dados em
    // ‘data’ e em size o número de bits a enviar.
    fun send(addr: Destination, data: Int, size : Int) {

        if (addr == Destination.LCD){   // sets the select bits to 0 accordingly, to start the hardware
            HAL.clrBits(LCD_Sel)
        }else HAL.clrBits(SC_Sel)

        var parity = 0

        for (i in 0..<size){
            HAL.clrBits(SCLK) //clock

            val dataShifted = data.shr(i)
            val dataCheck = 0x01

            //escreve bit por bit na mascara
            if( dataShifted and dataCheck == 1) {
                HAL.setBits(SDX)
                parity++
            }  else HAL.clrBits(SDX)//Verifica se o numero de bits a 1 é par ou impar

            HAL.setBits(SCLK) //clock
        }

        HAL.clrBits(SCLK) //clock

        if(parity%2 == 0) HAL.clrBits(SDX)  //Checks the parity, and if even sets temp bit to 0, if not set to 1
        else HAL.setBits(SDX)

        HAL.setBits(SCLK) //clock
        HAL.clrBits(SCLK) //clock

        HAL.setBits(LCD_Sel)  // sets the select bits back to 1
        HAL.setBits(SC_Sel)

    }
    // Inicia a classe
    fun init() {
        HAL.init()
    }
}
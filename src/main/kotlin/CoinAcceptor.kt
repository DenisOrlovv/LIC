object CoinAcceptor {
    private const val COIN_MASK = 0x40
    private var previousState  = 0

    fun init(){
    }

    fun getCoin():Boolean{
        val currentState = if(HAL.isBit(COIN_MASK)) 1 else 0
        val risingEdgeDetect = (previousState == 1) && (currentState == 0)
        if (risingEdgeDetect) HAL.setBits(COIN_MASK) else HAL.clrBits(COIN_MASK)
        previousState = currentState
        return risingEdgeDetect
    }
}
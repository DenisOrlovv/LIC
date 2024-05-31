object CoinAcceptor {
    private const val COIN_MASK = 0x40

    fun init(){
    }

    fun getCoin():Boolean{
        return if (HAL.isBit(COIN_MASK)){
            HAL.setBits(COIN_MASK)
            true
        }
        else {
            HAL.clrBits(COIN_MASK)
            false
        }
    }
}
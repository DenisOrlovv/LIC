
object Maintenance{
    private const val MAINTENANCE_MASK = 0x80

    fun init(){}

    fun checkMaintenance(): Boolean = HAL.isBit(MAINTENANCE_MASK)


}
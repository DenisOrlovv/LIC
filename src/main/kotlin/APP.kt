import isel.leic.utils.Time


class APP (key: Char){
    val previousKey: Char? = null
    val currKey: Char = key
    val invadersTimeToGen: Long = 1000
    val scorePerInv = 0

    var currLine:Int = 1
    var currTime = Time.getTimeInMillis()
    var inv1: Array<Char> = emptyArray()
    var inv2: Array<Char> = emptyArray()
    var score: Int = 0

    fun getRefTime() = Time.getTimeInMillis()

    fun checkTime(refTime:?):Boolean {
        val endTime = refTime + invadersTimeToGen
        return if (endTime <= currTime) true
        else false
    }
    fun Array<Char>.generateInvaders(timeout: Boolean) = if (timeout) this += ('0'..'9').random()
    fun checkShot():Boolean{
        val array = if(currLine == 1) inv1 else inv2
        return if (currKey == '#' && previousKey == array[0]){
            array.drop(1)//remove first array element
            true
        } else false
    }
    fun checkLost():Boolean = inv1.size == 15 || inv2.size == 15
    fun trackScore() { if (checkShot()) score += scorePerInv }
}
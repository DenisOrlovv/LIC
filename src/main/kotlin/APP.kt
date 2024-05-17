import isel.leic.utils.Time

object APP {
    fun init(){
        TUI.init()
        ScoreDisplay.init()
    }


    var currKey: Char = TUI.read(1000)
    private const val INVADERS_TIME_GEN: Long = 1000
    private const val SCORE_PER_INV = 0

    private var previousKey: Char? = null
    private var currLine:Int = 1
    private var currTime = Time.getTimeInMillis()
    private var inv1: Array<Char> = emptyArray()
    private var inv2: Array<Char> = emptyArray()
    var score: Int = 0

    fun startGame(){
        var timeRef = getRefTime()
        while(!lost()){
            val invSelect = (0..1).random()
            if(invSelect==1)inv1.generateInvaders(checkTime(timeRef))
            else inv2.generateInvaders(checkTime(timeRef))
            if((!checkLine() || currKey == '#') &&  currKey != TUI.NONE) drawPlayer()
            incScore()

            timeRef = if (checkTime(timeRef)) getRefTime() else timeRef
            previousKey = currKey
            currKey = TUI.read(1000)
        }
        ScoreDisplay.setScore(score)
    }

    private fun drawPlayer(){
        TUI.cursor(currLine-1, 0)
        TUI.write(currKey)
    }

    private fun Array<Char>.drawInvaders(){
        for(i in this.indices){
            TUI.cursor(1, 16-i)
            TUI.write(inv2[i])
        }
    }
    private fun checkLine():Boolean {
        return if(currKey == '*'){
            currLine = if (currLine == 1) 2 else 1
            true
        }
        else false
    }
    private fun getRefTime() = Time.getTimeInMillis()


    private fun checkTime(refTime:Long):Boolean {
        val endTime = refTime + INVADERS_TIME_GEN
        return if (endTime <= currTime) true
        else false
    }
    private fun Array<Char>.generateInvaders(timeout: Boolean) {
        if (timeout){
            this + ('0'..'9').random()
            this.drawInvaders()
        }
    }




    private fun checkShot():Boolean{
        if(previousKey == null) return false
        val array = if(currLine == 1) inv1 else inv2
        return if (currKey == '#' && previousKey == array[0]){
            array.drop(1)//remove first array element
            true
        } else false
    }
    private fun lost():Boolean = inv1.size == 15 || inv2.size == 15
    private fun incScore():Boolean {
        return if (checkShot()) {
            score += SCORE_PER_INV
            true
        } else false
    }
}
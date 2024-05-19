import isel.leic.utils.Time
import kotlin.random.Random

object APP {
    fun init(){
        TUI.init()
        ScoreDisplay.init()
    }


    private var currKey: Char = TUI.read(1000)
    private const val INVADERS_TIME_GEN: Long = 1000
    private const val SCORE_PER_INV = 10
    private const val DISPLAY_LENGTH = 16
    private const val DISPLAY_WIDTH = 2
    private const val SHIP = '>'
    private const val CHARGE = ']'

    private var previousKey: Char? = null
    private var currLine:Int = 0
    private var currTime = getTime()
    private val display = Array(DISPLAY_WIDTH){Array(DISPLAY_LENGTH){TUI.NONE} }
    private var invPointer = Array(2){15}
    private var score: Int = 0

    fun startGame(){
        var timeRef = getTime()
        display[0][0] = CHARGE
        display[1][0] = CHARGE
        display[0][1] = SHIP
        drawDisplay()
        while(!lost()){
            println(score)

            currTime = getTime()
            currKey = TUI.read(100)
            when(currKey) {
                '*' -> changeLine()
                '#' -> checkShot()
                TUI.NONE ->{}
                else ->{
                    display[currLine][0] = currKey
                    drawPlayer()
                }
            }
            if(Random.nextBoolean()) generateInvader(checkTime(timeRef), 0)
            else generateInvader(checkTime(timeRef),1)
            timeRef = if (checkTime(timeRef)) getTime() else timeRef
            ScoreDisplay.setScore(score)
        }

    }

    private fun drawPlayer(){
        TUI.cursor(currLine, 0)
        TUI.write(currKey)
    }

    /**
     * Function to update the full display
     */
    private fun drawDisplay(){
        for(i in display.indices) {
            for (j in display[i].indices) {
                TUI.cursor(i,j)
                TUI.write(display[i][j])
            }
        }
    }


    /**
     * Function to be used immediately after new invader spawn
     */
    private fun drawNewInvader(line: Int){
        TUI.cursor(line, invPointer[line]-1)
        TUI.write(display[line][invPointer[line]-1])
    }
    private fun changeLine() {
        display[currLine][1] = TUI.NONE
        display[currLine][0] = CHARGE
        currLine = if (currLine == 1){
            0
        } else 1
        display[currLine][1] = SHIP
        drawDisplay()
    }
    private fun getTime() = Time.getTimeInMillis()

    private fun checkTime(refTime:Long):Boolean {
        val endTime = refTime + INVADERS_TIME_GEN
        return endTime <= currTime
    }
    private fun shiftInvaders(line: Int){
        val savePointer = invPointer[line]
        while(invPointer[line] < DISPLAY_LENGTH-1){
            display[line][invPointer[line]] = display[line][invPointer[line]+1]
            invPointer[line] += 1
        }
        invPointer[line] = savePointer
    }
    private fun generateInvader(timeout: Boolean, line: Int){
        if (timeout){
            shiftInvaders(line)
            display[line][DISPLAY_LENGTH-1] = ('0'..'9').random()
            invPointer[line] -= 1
            drawDisplay()
        }
    }

    private fun checkShot(){
        if (invPointer[currLine] < DISPLAY_LENGTH-1 && display[currLine][0] == display[currLine][invPointer[currLine]+1]){
            incScore(display[currLine][0].digitToInt())
            display[currLine][invPointer[currLine]+1] = TUI.NONE//remove first array element
            invPointer[currLine] += 1
        }
        display[currLine][0] = CHARGE
        drawDisplay()
    }
    private fun lost():Boolean = invPointer[0] == 1 || invPointer[1] == 1
    private fun incScore(value: Int) { score += value}
}
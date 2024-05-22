import isel.leic.utils.Time
import kotlin.random.Random

fun main () {
    APP.init()
    while (true){
        APP.appSetup()
    }
}
object APP {
    fun init() {
        TUI.init()
        ScoreDisplay.init()
        writeShips()
    }


    private var currKey: Char = TUI.read(1000)
    private var INVADERS_TIME_GEN: Long = 1300
    private const val END_SCREEN_TIME: Long = 5000
    private const val DISPLAY_LENGTH = 16
    private const val DISPLAY_WIDTH = 2
    private var DIGITAL_SHIP = '>'
    private var SHIP = 3
    private const val CHARGE = ']'

    private var currLine: Int = 0
    private var currTime = getTime()
    private val currDisplay = Array(DISPLAY_WIDTH) { Array(DISPLAY_LENGTH) { TUI.NONE } }
    private val nextDisplay = Array(DISPLAY_WIDTH) { Array(DISPLAY_LENGTH) { TUI.NONE } }
    private var invPointer = Array(2) { DISPLAY_LENGTH - 1 }
    private var score: Int = 0
    private var difficulty: Int = 1

    fun appSetup() {
        TUI.clear()
        TUI.cursor(0,0)
        TUI.write(" Space Invaders ")
        TUI.cursor(1, 0)
        TUI.write("Press * To Start")
        val key = TUI.read(1000)
        if (key == '*') {
            INVADERS_TIME_GEN = 1300
            pickShipScreen()
            pickDifficultyScreen()
            startGame()
            endScreen()
        }
    }

    /**
     * Draws the pick ship screen
     * sets the ship for the next game
     */
    private fun pickShipScreen(){
        TUI.clear()
        TUI.write("Choose your ship")
        TUI.cursor(1, 0)
        TUI.write(" 1-")
        drawShip(1)
        TUI.write("  2-")
        drawShip(2)
        TUI.write("  3-")
        drawShip(3)
        while (true) {
            val read = TUI.read(1000)
            if (read in '1'..'3') {
                SHIP = read.digitToInt()
                break
            }
        }
    }

    /**
     * Draws the eng game screen
     * Waits for LOST_SCREEN_TIME
     */
    private fun endScreen(){
        ScoreDisplay.setScore(0)
        ScoreDisplay.off(true)
        TUI.clear()
        TUI.write("***Game Lost****")
        TUI.cursor(1, 0)
        TUI.write("Score: $score")
        val time = getTime()
        currTime = getTime()
        while (!checkTimeout(time, END_SCREEN_TIME)) {
            currTime = getTime()
        }
    }

    /**
     * Draws the pick difficulty screen
     * sets the difficulty for the next game
     */
    private fun pickDifficultyScreen(){
        TUI.clear()
        TUI.write("Difficulty Level")
        TUI.cursor(1, 0)
        TUI.write(" 1  2  3  4  5")
        while (true) {
            val read = TUI.read(1000)
            if (read in '1'..'5') {
                difficulty = read.digitToInt()
                break
            }
        }
    }

    private fun setDifficulty() { INVADERS_TIME_GEN -= difficulty * 200 } // Sets the invaders generation time according to the difficult chosen

    /**
     * Sequence of instructions to reset the game
     */
    private fun resetGame() {
        TUI.clear()
        score = 0
        setDifficulty()
        invPointer[0] = DISPLAY_LENGTH - 1
        invPointer[1] = DISPLAY_LENGTH - 1
        currLine = 0
        for (i in nextDisplay.indices) {
            for (j in nextDisplay[i].indices) {
                nextDisplay[i][j] = TUI.NONE
            }
        }
        for (i in currDisplay.indices) {
            for (j in currDisplay[i].indices) {
                currDisplay[i][j] = TUI.NONE
            }
        }
        nextDisplay[0][0] = CHARGE
        nextDisplay[1][0] = CHARGE
        nextDisplay[0][1] = DIGITAL_SHIP
        TUI.cursor(currLine, 1)
        drawShip(SHIP)
    }

    /**
     * Game App
     * Will be running until the game ends
     */
    private fun startGame() {
        ScoreDisplay.off(false)
        var timeRef = getTime()
        resetGame()
        updateDisplay()
        while (!lost()) {
            currTime = getTime()
            currKey = TUI.read(100)
            when (currKey) {
                '*' -> changeLine()
                '#' -> checkShot()
                TUI.NONE -> {}
                else -> {
                    nextDisplay[currLine][0] = currKey
                    drawPlayer()
                }
            }
            generateInvader(timeout = checkTimeout(timeRef, INVADERS_TIME_GEN))
            timeRef = if (checkTimeout(timeRef, INVADERS_TIME_GEN)) getTime() else timeRef
            ScoreDisplay.setScore(score)
        }

    }


    //Display Update API
    private fun drawShip(ship: Int){ TUI.writeDATA(ship) } // Draws ship pattern in the current cursor position

    /**
     * Draws ship during the game in the set position
     */
    private fun drawShipInGame(ship: Int){
        TUI.cursor(currLine,1)
        drawShip(ship)
    }
    /**
     * Draws only the value charged
     * To be called only when a number was pressed during the game
      */
    private fun drawPlayer() {
        nextDisplay[currLine][0]= currKey
        TUI.cursor(currLine, 0)
        TUI.write(currKey)
    }

    /**
     * Function that updates the full display
     * It will only change what needs to be changed
     */
    private fun updateDisplay() {
        for (i in nextDisplay.indices) {
            for (j in nextDisplay[i].indices) {
                if(currDisplay[i][j] != nextDisplay[i][j]) {
                    if (j == 1 && i == currLine) {
                        drawShipInGame(SHIP)
                    } else {
                        TUI.cursor(i,j)
                        TUI.write(nextDisplay[i][j])
                    }
                    currDisplay[i][j] = nextDisplay[i][j]
                }
            }
        }
    }

    //Game API
    /**
     * Sequence of actions to change the line
     * To be called only when '*' was pressed during the game
     */
    private fun changeLine() {
        nextDisplay[currLine][1] = TUI.NONE
        nextDisplay[currLine][0] = CHARGE
        currLine = if (currLine == 1) {
            0
        } else 1
        currLine
        nextDisplay[currLine][1] = DIGITAL_SHIP
        updateDisplay()
    }

    private fun getTime() = Time.getTimeInMillis()// gets the current time

    /**
     * Will return true when the time interval as passed
     */
    private fun checkTimeout(refTime: Long, interval: Long): Boolean {
        val endTime = refTime + interval
        return endTime <= currTime
    }

    /**
     * Shifts all the current invaders to receive new
     */
    private fun shiftInvaders(line: Int) {
        val savePointer = invPointer[line]
        while (invPointer[line] < DISPLAY_LENGTH - 1) {
            nextDisplay[line][invPointer[line]] = nextDisplay[line][invPointer[line] + 1]
            invPointer[line] += 1
        }
        invPointer[line] = savePointer
    }

    /**
     * Inserts a new random invader
     */
    private fun generateInvader(timeout: Boolean) {
        val line = if (Random.nextBoolean()) 0 else 1
        if (timeout) {
            shiftInvaders(line)
            nextDisplay[line][DISPLAY_LENGTH - 1] = ('0'..'9').random()
            invPointer[line] -= 1
            updateDisplay()
        }
    }

    /**
     * Function that removes the invader in the front if the shot is valid
     */
    private fun checkShot() {
        if (nextDisplay[currLine][0] == CHARGE) return
        if (invPointer[currLine] < DISPLAY_LENGTH - 1 && nextDisplay[currLine][0] == nextDisplay[currLine][invPointer[currLine] + 1]) {
            incScore(nextDisplay[currLine][0].digitToInt())
            nextDisplay[currLine][invPointer[currLine] + 1] = TUI.NONE//remove first array element
            invPointer[currLine] += 1
        }
        nextDisplay[currLine][0] = CHARGE
        updateDisplay()
    }

    private fun lost(): Boolean = invPointer[0] == 0 || invPointer[1] == 0 //Checks losing conditions
    private fun incScore(value: Int) { score += (value * difficulty)/3 } // Score increase depending on the difficulty

    //LCD memory set
    /**
     * Function that writes the ships designs in the LCD CGRAM
     *
     */
    private fun writeShips() {

        //NONE
        TUI.writeCMD(0x40)
        TUI.writeDATA(0x00)//--------1
        TUI.writeCMD(0x41)
        TUI.writeDATA(0x00)//--------2
        TUI.writeCMD(0x42)
        TUI.writeDATA(0x00)//--------3
        TUI.writeCMD(0x43)
        TUI.writeDATA(0x00)//--------4
        TUI.writeCMD(0x44)
        TUI.writeDATA(0x00)//--------5
        TUI.writeCMD(0x45)
        TUI.writeDATA(0x00)//--------6
        TUI.writeCMD(0x46)
        TUI.writeDATA(0x00)//--------7
        TUI.writeCMD(0x47)
        TUI.writeDATA(0x00)//--------8


        //nave default
        TUI.writeCMD(0x58)
        TUI.writeDATA(0x1E)//--------1
        TUI.writeCMD(0x59)
        TUI.writeDATA(0x18)//--------2
        TUI.writeCMD(0x5A)
        TUI.writeDATA(0x1C)//--------3
        TUI.writeCMD(0x5B)
        TUI.writeDATA(0x1F)//--------4
        TUI.writeCMD(0x5C)
        TUI.writeDATA(0x1C)//--------5
        TUI.writeCMD(0x5D)
        TUI.writeDATA(0x18)//--------6
        TUI.writeCMD(0x5E)
        TUI.writeDATA(0x1E)//--------7
        TUI.writeCMD(0x5F)
        TUI.writeDATA(0x00)//--------8

        //Ship design 1
        TUI.writeCMD(0x48)
        TUI.writeDATA(0x0E)//--------1
        TUI.writeCMD(0x49)
        TUI.writeDATA(0x19)//--------2
        TUI.writeCMD(0x4A)
        TUI.writeDATA(0x1C)//--------3
        TUI.writeCMD(0x4B)
        TUI.writeDATA(0x1F)//--------4
        TUI.writeCMD(0x4C)
        TUI.writeDATA(0x1C)//--------5
        TUI.writeCMD(0x4D)
        TUI.writeDATA(0x19)//--------6
        TUI.writeCMD(0x4E)
        TUI.writeDATA(0x0E)//--------7
        TUI.writeCMD(0x4F)
        TUI.writeDATA(0x00)//--------8
        //Ship design 2
        TUI.writeCMD(0x50)
        TUI.writeDATA(0x0F)//--------1
        TUI.writeCMD(0x51)
        TUI.writeDATA(0x1C)//--------2
        TUI.writeCMD(0x52)
        TUI.writeDATA(0x18)//--------3
        TUI.writeCMD(0x53)
        TUI.writeDATA(0x1C)//--------4
        TUI.writeCMD(0x54)
        TUI.writeDATA(0x18)//--------5
        TUI.writeCMD(0x55)
        TUI.writeDATA(0x1C)//--------6
        TUI.writeCMD(0x56)
        TUI.writeDATA(0x0F)//--------7
        TUI.writeCMD(0x57)
        TUI.writeDATA(0x00)//--------8
    }
}
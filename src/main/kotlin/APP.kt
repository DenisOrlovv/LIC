import isel.leic.utils.Time
import kotlin.random.Random

object APP {
    fun init() {
        TUI.init()
        ScoreDisplay.init()
        writeShips()
    }


    private var currKey: Char = TUI.read(1000)
    private var INVADERS_TIME_GEN: Long = 1300
    private const val LOST_SCREEN_TIME: Long = 10000
    private const val DISPLAY_LENGTH = 16
    private const val DISPLAY_WIDTH = 2
    private const val SHIP = 1
    private const val CHARGE = ']'

    private var currLine: Int = 0
    private var currTime = getTime()
    private val display = Array(DISPLAY_WIDTH) { Array(DISPLAY_LENGTH) { TUI.NONE } }
    private var invPointer = Array(2) { DISPLAY_LENGTH - 1 }
    private var score: Int = 0
    private var difficulty: Int = 1

    fun appSetup() {
        TUI.clear()
        TUI.write(" Space Invaders ")
        TUI.cursor(1, 0)
        TUI.write("Press * To Start")
        val key = TUI.read(1000)
        if (key == '*') {
            INVADERS_TIME_GEN = 1300
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
            startGame()
            TUI.clear()
            TUI.write("***Game Lost****")
            TUI.cursor(1, 0)
            TUI.write("Score: $score")
            val time = getTime() + LOST_SCREEN_TIME
            currTime = getTime()
            while (time > currTime) {
                currTime = getTime()
            }
        }
    }
    private fun drawShip(){
        TUI.cursor(currLine,1)
        TUI.writeDATA(SHIP)
    }

    private fun setDifficulty() {
        INVADERS_TIME_GEN -= difficulty * 200
    }

    private fun reset() {
        score = 0
        setDifficulty()
        invPointer[0] = DISPLAY_LENGTH - 1
        invPointer[1] = DISPLAY_LENGTH - 1
        currLine = 0
        for (i in display.indices) {
            for (j in display[i].indices) {
                display[i][j] = TUI.NONE
            }
        }
        display[0][0] = CHARGE
        display[1][0] = CHARGE
        drawShip()
    }

    private fun startGame() {
        var timeRef = getTime()
        reset()
        drawDisplay()
        while (!lost()) {
            println(score)

            currTime = getTime()
            currKey = TUI.read(100)
            when (currKey) {
                '*' -> changeLine()
                '#' -> checkShot()
                TUI.NONE -> {}
                else -> {
                    display[currLine][0] = currKey
                    drawPlayer()
                }
            }
            if (Random.nextBoolean()) generateInvader(checkTime(timeRef), 0)
            else generateInvader(checkTime(timeRef), 1)
            timeRef = if (checkTime(timeRef)) getTime() else timeRef
            ScoreDisplay.setScore(score)
        }

    }

    private fun drawPlayer() {
        TUI.cursor(currLine, 0)
        TUI.write(currKey)
    }

    /**
     * Function to update the full display
     */
    private fun drawDisplay() {
        for (i in display.indices) {
            for (j in display[i].indices) {
                if(j == 1 && i == currLine) {
                    drawShip()
                }
                else {
                    TUI.cursor(i, j)
                    TUI.write(display[i][j])
                }
            }
        }
    }

    private fun changeLine() {
        display[currLine][1] = TUI.NONE
        display[currLine][0] = CHARGE
        currLine = if (currLine == 1) {
            0
        } else 1
        drawDisplay()
    }

    private fun getTime() = Time.getTimeInMillis()

    private fun checkTime(refTime: Long): Boolean {
        val endTime = refTime + INVADERS_TIME_GEN
        return endTime <= currTime
    }

    private fun shiftInvaders(line: Int) {
        val savePointer = invPointer[line]
        while (invPointer[line] < DISPLAY_LENGTH - 1) {
            display[line][invPointer[line]] = display[line][invPointer[line] + 1]
            invPointer[line] += 1
        }
        invPointer[line] = savePointer
    }

    private fun generateInvader(timeout: Boolean, line: Int) {
        if (timeout) {
            shiftInvaders(line)
            display[line][DISPLAY_LENGTH - 1] = ('0'..'9').random()
            invPointer[line] -= 1
            drawDisplay()
        }
    }

    private fun checkShot() {
        if (invPointer[currLine] < DISPLAY_LENGTH - 1 && display[currLine][0] == display[currLine][invPointer[currLine] + 1]) {
            incScore(display[currLine][0].digitToInt())
            display[currLine][invPointer[currLine] + 1] = TUI.NONE//remove first array element
            invPointer[currLine] += 1
        }
        display[currLine][0] = CHARGE
        drawDisplay()
    }

    private fun lost(): Boolean = invPointer[0] == 0 || invPointer[1] == 0
    private fun incScore(value: Int) {
        score += (value * difficulty)/3
    }

    private fun writeShips() {
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
        TUI.writeDATA(0x1F)//--------1
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
        TUI.writeDATA(0x1F)//--------7
        TUI.writeCMD(0x57)
        TUI.writeDATA(0x00)//--------8
    }
}
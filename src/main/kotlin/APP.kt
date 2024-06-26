import isel.leic.utils.Time
import kotlin.random.Random

fun main () {
    APP.init()
    APP.systemSetup()
}
object APP {
    fun init() {
        TUI.init()
        ScoreDisplay.init()
        setScoreAnimation()
        writeShips()
    }

    //Game constants
    private const val END_SCREEN_TIME: Long = 1000      //Time it takes to go back to main menu after a game
    private const val DISPLAY_LENGTH = 16
    private const val DISPLAY_WIDTH = 2
    private const val DIGITAL_SHIP = '>'
    private const val CHARGE = ']'
    private const val SCORES_SWITCH_TIME:Long = 2000

    //Game variables
    private var currKey: Char = TUI.read(1000)  //key that was pressed
    private var INVADERS_TIME_GEN: Array<Long> = arrayOf(1000, 800, 600, 400, 300)// Times it takes to generate invaders
    private var SHIP = 3                //Ship that appears in LCD
    private var currLine: Int = 0
    private var currTime = getTime()    //System Clock
    private val currDisplay = Array(DISPLAY_WIDTH) { Array(DISPLAY_LENGTH) { TUI.NONE } } //Digital print of LCD
    private val nextDisplay = Array(DISPLAY_WIDTH) { Array(DISPLAY_LENGTH) { TUI.NONE } } //Next frame of the game
    private var invPointer = Array(2) { DISPLAY_LENGTH - 1 } //Front invader current position
    private var score: Int = 0      //Score of the current game
    private var difficulty: Int = 3 //Difficulty set by the player

    //Display variables
    private var scores = Scores.readScores()
    private var Coins  = Statistics.getStats().second
    private var Games = Statistics.getStats().first
    private var scoreCounter = 0
    private var CredsShower = true
    private var scoresTimeRef:Long = 0
    private var creds = 0

    fun systemSetup(){
        displayWrite(0, " Space Invaders ")
        displayWrite(1, " *-Start   $$Coins ")
        Time.sleep(2000)
        outerLoop@while (true) {
            while(!Maintenance.checkMaintenance()) {
                appSetup()
            }
            while(Maintenance.checkMaintenance()){
                if (maintenanceSetup()) break@outerLoop
            }

        }
        Scores.writeScore(scores)
        Statistics.writeStats(Coins, Games)
        ScoreDisplay.off(true)
        TUI.clear()
    }

    fun appSetup() {
        mainMenu()
        if (CoinAcceptor.getCoin()){
            Coins ++
            creds += 2
        }
        val key = TUI.read(100)
        if (CoinAcceptor.getCoin()){
            Coins ++
            creds += 2
        }
        if (key == '*' && Coins != 0) {
            Games++
            creds--
            ScoreDisplay.off(true)
            pickShipScreen()
            pickDifficultyScreen()
            startGame()
            endScreen()
        }
        if (score != 0) {
            val name = getName()
            scores = Scores.setScore(name, score, scores)
        }
        score = 0
    }

    private fun maintenanceSetup():Boolean{
        maintenanceScreen()
        val key = TUI.read(250)
        when(key){
            '*'->{
                statsScreen()
                val key = TUI.read(3000)
                if(key == '#') clearStats()
            }
            '#'->{ if (shutDown()) return true }
            TUI.NONE->{}
            else ->{
                SHIP = 3
                difficulty = 3
                startGame()
                endScreen()
                score = 0
            }
        }
        return false
    }

    // Screens API
    private fun clearStats(){
        displayWrite(0," Clear Counters ")
        displayWrite(1,"5-Yes  other-No ")
        val key = TUI.read(3000)
        if (key == '5'){
            Games = 0
            Coins = 0
        }
    }


    private fun mainMenu(){
        currTime = getTime()
        displayWrite(0, " Space Invaders ")
        if(checkTimeout(scoresTimeRef, SCORES_SWITCH_TIME )) {
            if (creds == 0) {
                if (scores.isNotEmpty()) drawScores()
            } else {
                if (scores.isNotEmpty() && !CredsShower) {
                    drawScores()
                    CredsShower = true
                } else {
                    displayWrite(1, " *-Start   $$creds    ")
                    CredsShower = false
                }

            }
            scoresTimeRef = getTime()
        }

        scoreAnimation()
    }

    private fun drawScores(){
        displayWrite(1, "${"%02d".format(scores[scoreCounter].position)}-${scores[scoreCounter].name}   ${scores[scoreCounter].score}      ")
        if (scoreCounter < scores.size - 1) scoreCounter++ else scoreCounter = 0
    }


    private fun maintenanceScreen(){
        displayWrite(0," On Maintenance ")
        displayWrite(1,"*-Count #-shutD ")
    }

    private fun shutDown():Boolean{
        displayWrite(0,"   ShutDown    ")
        displayWrite(1," 5-Yes other-No  ")
        val key = TUI.read(5000)
        return key == '5'
    }

    private fun statsScreen(){
        displayWrite(0,"Games:$Games         ")
        displayWrite(1,"Coins:$Coins         ")
    }
    /**
     * Draws the pick ship screen
     * sets the ship for the next game
     */
    private fun pickShipScreen(){
        TUI.clear()
        displayWrite(0, "Choose your ship")
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
        TUI.clear()
        displayWrite(0, "***Game Over****")
        displayWrite(1,"Score: $score" )
        val time = getTime()
        currTime = getTime()
        while (!checkTimeout(time, END_SCREEN_TIME)) {
            currTime = getTime()
        }
        setScoreAnimation()
    }

    /**
     * Function that will get the name of the player with the current score
     */
    private fun getName():String{
        TUI.writeCMD(0x0F)// cursor on
        displayWrite(0,"Name:           ")
        var key = TUI.read(500)
        val name = Array(8){TUI.NONE}
        var cursor = 0
        var character = 'A'
        var nameLength = 0
        while (key != '5'){
            name[cursor] = character
            displayWrite(0,5+cursor,name[cursor])
            TUI.cursor(0,5+cursor)
            key = TUI.read(1000)
            when(key){
                '2' -> if(character<'Z') character++
                '8' -> if (character>'A') character--
                '6' -> if(cursor < 7){
                    if (cursor == nameLength){
                        character = 'A'
                        cursor++
                        nameLength++
                    }
                    else {
                        cursor++
                        character = name[cursor]
                    }
                }
                '4' -> if (cursor > 0) {
                    cursor--
                    character = name[cursor]
                }
                '*' -> if(cursor > 0 && nameLength <= cursor){
                    name[cursor] = TUI.NONE
                    displayWrite(0,5+cursor,name[cursor])
                    cursor--
                    character = name[cursor]
                    nameLength--
                }
                else ->{}
            }
        }
        TUI.writeCMD(0x0C)// cursor off
        return (name.slice(0..nameLength)).joinToString("")
    }

    /**
     * Draws the pick difficulty screen
     * sets the difficulty for the next game
     */
    private fun pickDifficultyScreen(){
        TUI.clear()
        displayWrite(0,"Difficulty Level")
        displayWrite(1," 1  2  3  4  5")
        while (true) {
            val read = TUI.read(1000)
            if (read in '1'..'5') {
                difficulty = read.digitToInt()
                break
            }
        }
    }

    /**
     * Sequence of instructions to reset the game
     */
    private fun resetGame() {
        TUI.clear()
        score = 0
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
        ScoreDisplay.setScore(score)
        ScoreDisplay.off(false)
        TUI.cursor(currLine, 1)
        drawShip(SHIP)
        updateDisplay()
    }

    /**
     * Game App.
     * Will be running until the game ends.
     */
    private fun startGame() {
        var timeRef = getTime()
        resetGame()
        while (!lost()) {
            currTime = getTime()
            currKey = TUI.read(100)
            when (currKey) {
                '*' -> changeLine()
                '#' -> checkShot()
                TUI.NONE -> { }
                else -> { drawPlayer(currKey) }
            }
            generateInvader(timeout = checkTimeout(timeRef, INVADERS_TIME_GEN[difficulty-1]))
            timeRef = if (checkTimeout(timeRef, INVADERS_TIME_GEN[difficulty-1])) getTime() else timeRef
            ScoreDisplay.setScore(score)
        }

    }


    //Display Update API
    private fun displayWrite(line: Int, text: String){
        TUI.cursor(line,0)
        TUI.write(text)
    }
    private fun displayWrite(line: Int, col: Int, character: Char){
        TUI.cursor(line,col)
        TUI.write(character)
    }
    private fun drawShip(ship: Int){ TUI.writeDATA(ship) } // Draws ship pattern in the current cursor position

    /**
     * Draws ship during the game in the set position
     */
    private fun drawShipInGame(ship: Int){
        TUI.cursor(currLine,1)
        drawShip(ship)
    }
    /**
     * Draws only the value charged.
     * To be called only when a number was pressed during the game.
      */
    private fun drawPlayer(key: Char) {
        nextDisplay[currLine][0] = key
        TUI.cursor(currLine, 0)
        TUI.write(nextDisplay[currLine][0])
    }

    /**
     * Function that updates the full display.
     * It will only change what needs to be changed.
     */
    private fun updateDisplay() {
        for (i in nextDisplay.indices) {
            for (j in nextDisplay[i].indices) {
                if(currDisplay[i][j] != nextDisplay[i][j]) {
                    if (j == 1 && i == currLine) drawShipInGame(SHIP)
                    else displayWrite(i,j,nextDisplay[i][j])
                    currDisplay[i][j] = nextDisplay[i][j]
                }
            }
        }
    }

    //Game API
    /**
     * Sequence of actions to change the line.
     * To be called only when '*' was pressed during the game.
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
        drawPlayer(CHARGE)
        updateDisplay()
    }

    private fun lost(): Boolean = invPointer[0] == 0 || invPointer[1] == 0 //Checks losing conditions
    private fun incScore(value: Int) { score += (value * difficulty)/3 } // Score increase depending on the difficulty

    //LCD memory set
    /**
     * Function that writes the ships designs in the LCD CGRAM
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

    //Score Display Animations API
    private fun setScoreAnimation(){
        ScoreDisplay.currentValue[0] = 0xA
        ScoreDisplay.currentValue[1] = 0xE
        ScoreDisplay.currentValue[2] = 0xD
        ScoreDisplay.currentValue[3] = 0XC
        ScoreDisplay.currentValue[4] = 0xB
        ScoreDisplay.currentValue[5] = 0xA
    }
    private fun scoreAnimation() {
        for (i in ScoreDisplay.currentValue.indices){
            val data = ScoreDisplay.currentValue[i].shl(3) + (5-i)
            ScoreDisplay.send( data ,7)
            if (ScoreDisplay.currentValue[i] < 0xE){
                ScoreDisplay.currentValue[i]++
            }
            else ScoreDisplay.currentValue[i] = 0xA
        }
        ScoreDisplay.send( 0x06,7)
    }
}
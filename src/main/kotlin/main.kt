fun main () {
    TUI.init()

    //nave default
    LCD.writeCMD(0x40)
    LCD.writeDATA(0x1E)//--------1
    LCD.writeCMD(0x41)
    LCD.writeDATA(0x18)//--------2
    LCD.writeCMD(0x42)
    LCD.writeDATA(0x1C)//--------3
    LCD.writeCMD(0x43)
    LCD.writeDATA(0x1F)//--------4
    LCD.writeCMD(0x44)
    LCD.writeDATA(0x1C)//--------5
    LCD.writeCMD(0x45)
    LCD.writeDATA(0x18)//--------6
    LCD.writeCMD(0x46)
    LCD.writeDATA(0x1E)//--------7
    LCD.writeCMD(0x47)
    LCD.writeDATA(0x00)//--------8

    //
    LCD.writeCMD(0x80)
    LCD.writeDATA(0)

    //nave design 1
    LCD.writeCMD(0x48)
    LCD.writeDATA(0x0E)//--------1
    LCD.writeCMD(0x49)
    LCD.writeDATA(0x19)//--------2
    LCD.writeCMD(0x4A)
    LCD.writeDATA(0x1C)//--------3
    LCD.writeCMD(0x4B)
    LCD.writeDATA(0x1F)//--------4
    LCD.writeCMD(0x4C)
    LCD.writeDATA(0x1C)//--------5
    LCD.writeCMD(0x4D)
    LCD.writeDATA(0x19)//--------6
    LCD.writeCMD(0x4E)
    LCD.writeDATA(0x0E)//--------7
    LCD.writeCMD(0x4F)
    LCD.writeDATA(0x00)//--------8

    LCD.writeCMD(0x81)
    LCD.writeDATA(1)

    // design
    LCD.writeCMD(0x50)
    LCD.writeDATA(0x1F)//--------1
    LCD.writeCMD(0x51)
    LCD.writeDATA(0x1C)//--------2
    LCD.writeCMD(0x52)
    LCD.writeDATA(0x18)//--------3
    LCD.writeCMD(0x53)
    LCD.writeDATA(0x1C)//--------4
    LCD.writeCMD(0x54)
    LCD.writeDATA(0x18)//--------5
    LCD.writeCMD(0x55)
    LCD.writeDATA(0x1C)//--------6
    LCD.writeCMD(0x56)
    LCD.writeDATA(0x1F)//--------7
    LCD.writeCMD(0x57)
    LCD.writeDATA(0x00)//--------8

    LCD.writeCMD(0x82)
    LCD.writeDATA(2)
}


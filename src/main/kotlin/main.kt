fun main () {
    TUI.init()
    while (true){
        var a = TUI.read(100000)
        TUI.write(a)
    }
}


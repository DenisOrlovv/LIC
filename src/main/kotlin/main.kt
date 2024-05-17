fun main () {
    KBD.init()
    while(true) {
        var a = KBD.waitKey(10000)
        println(a)
    }
}


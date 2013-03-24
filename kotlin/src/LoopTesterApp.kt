/**
 * Created with IntelliJ IDEA.
 * User: Nutzer
 * Date: 23.02.13
 * Time: 22:42
 * To change this template use File | Settings | File Templates.
 */
public class LoopTesterApp {

    public var cfg : CFG = CFG()
    public var lsg : LSG = LSG()
    public var root : BasicBlock = cfg.createNode(0)

    public open fun buildDiamond(start : Int) : Int {
        var bb0 : Int = start
        BasicBlockEdge(cfg, bb0, bb0 + 1)
        BasicBlockEdge(cfg, bb0, bb0 + 2)
        BasicBlockEdge(cfg, bb0 + 1, bb0 + 3)
        BasicBlockEdge(cfg, bb0 + 2, bb0 + 3)
        return bb0 + 3
    }

    public open fun buildConnect(start : Int, end : Int) : Unit {
        BasicBlockEdge(cfg, start, end)
    }

    public open fun buildStraight(start : Int, n : Int) : Int {
        for (i in 0..n - 1) {
            buildConnect(start + i, start + i + 1)
        }
        return start + n
    }

    public open fun buildBaseLoop(from : Int) : Int {
        var header : Int = buildStraight(from, 1)
        var diamond1 : Int = buildDiamond(header)
        var d11 : Int = buildStraight(diamond1, 1)
        var diamond2 : Int = buildDiamond(d11)
        var footer : Int = buildStraight(diamond2, 1)
        buildConnect(diamond2, d11)
        buildConnect(diamond1, header)
        buildConnect(footer, from)
        footer = buildStraight(footer, 1)
        return footer
    }

    public open fun getMem() : Unit {
        var runtime : Runtime = Runtime.getRuntime()
        var value : Long = (runtime.totalMemory()) / 1024
        println("  Total Memory: " + value + " KB")
    }

}

fun main(args: Array<String>) {
    println("Welcome to LoopTesterApp, Java edition")
    println("Constructing App...")
    var app : LoopTesterApp = LoopTesterApp()
    app.getMem()
    println("Constructing Simple CFG...")
    app.cfg.createNode(0)
    app.buildBaseLoop(0)
    app.cfg.createNode(1)
    BasicBlockEdge(app.cfg, 0, 2)
    println("15000 dummy loops")
    for (dummyloop in 0..(15000 - 1)) {
        var finder : HavlakLoopFinder = HavlakLoopFinder(app.cfg, app.lsg)
        finder.findLoops()
    }
    println("Constructing CFG...")
    var n : Int = 2
    for (parlooptrees in 0..(10 - 1)) {
        app.cfg.createNode(n + 1)
        app.buildConnect(2, n + 1)
        n = n + 1
        for (i in 0..100 - 1) {
            var top : Int = n
            n = app.buildStraight(n, 1)
            for (j in 0..25 - 1) {
                n = app.buildBaseLoop(n)
            }
            var bottom : Int = app.buildStraight(n, 1)
            app.buildConnect(n, top)
            n = bottom
        }
        app.buildConnect(n, 1)
    }
    app.getMem()
    println("Performing Loop Recognition\n1 Iteration\n")
    var finder : HavlakLoopFinder = HavlakLoopFinder(app.cfg, app.lsg)
    finder.findLoops()
    app.getMem()
    println("Another 50 iterations...")
    var start : Long = System.currentTimeMillis()
    var maxMemory : Long = Runtime.getRuntime().maxMemory()
    for (i in 0..(50 - 1)) {
        println(maxMemory - (Runtime.getRuntime().freeMemory()))
        var finder2 : HavlakLoopFinder = HavlakLoopFinder(app.cfg, LSG())
        finder2.findLoops()
    }
    println("Time: " + (System.currentTimeMillis() - start) + " ms")
    println("")
    app.getMem()
    println("# of loops: " + (app.lsg.getNumLoops()) + " (including 1 artificial root node)")
    println("# of BBs  : " + BasicBlock.getMyNumBasicBlocks())
    // println("# max time: " + (finder.getMaxMillis())!!)
    // println("# min time: " + (finder.getMinMillis())!!)
    app.lsg.calculateNestingLevel()
}
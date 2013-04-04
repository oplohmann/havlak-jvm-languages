/**
 * Created with IntelliJ IDEA. 
 * User: Nutzer
 * Date: 23.02.13
 * Time: 22:42
 * To change this template use File | Settings | File Templates.
 */
import kotlin.test.assertEquals

class LoopTesterApp {

    val cfg = CFG()
    val lsg = LSG()
    val root = cfg.createNode(0)

    fun buildDiamond(start : Int) : Int {
        BasicBlockEdge(cfg, start, start + 1)
        BasicBlockEdge(cfg, start, start + 2)
        BasicBlockEdge(cfg, start + 1, start + 3)
        BasicBlockEdge(cfg, start + 2, start + 3)
        return start + 3
    }

    fun buildConnect(start : Int, end : Int) {
        BasicBlockEdge(cfg, start, end)
    }

    fun buildStraight(start : Int, n : Int) : Int {
        for (i in 0..n - 1) {
            buildConnect(start + i, start + i + 1)
        }
        return start + n
    }

    fun buildBaseLoop(from : Int) : Int {
        val header = buildStraight(from, 1)
        val diamond1 = buildDiamond(header)
        val d11 = buildStraight(diamond1, 1)
        val diamond2 = buildDiamond(d11)
        val footer = buildStraight(diamond2, 1)
        buildConnect(diamond2, d11)
        buildConnect(diamond1, header)
        buildConnect(footer, from)
        return buildStraight(footer, 1)
    }

    fun getMem() {
        val runtime = Runtime.getRuntime()
        val value : Long = runtime.totalMemory() / 1024
        println(" Total Memory: $value KB")
    }

}

fun main(args: Array<String>) {
    println("Welcome to LoopTesterApp, Java edition")
    println("Constructing App...")
    val app : LoopTesterApp = LoopTesterApp()
    app.getMem()

    println("Constructing Simple CFG...")
    app.cfg.createNode(0)
    app.buildBaseLoop(0)
    app.cfg.createNode(1)
    BasicBlockEdge(app.cfg, 0, 2)

    println("15000 dummy loops")
    15000 times {
        val finder : HavlakLoopFinder = HavlakLoopFinder(app.cfg, app.lsg)
        finder.findLoops()
    }

    println("Constructing CFG...")
    var n : Int = 2
    10 times {
        app.cfg.createNode(n + 1)
        app.buildConnect(2, n + 1)
        n++
        100 times {
            val top = n
            n = app.buildStraight(n, 1)
            25 times {
                n = app.buildBaseLoop(n)
            }
            val bottom = app.buildStraight(n, 1)
            app.buildConnect(n, top)
            n = bottom
        }
        app.buildConnect(n, 1)
    }
    app.getMem()

    println("Performing Loop Recognition\n1 Iteration\n")
    val finder : HavlakLoopFinder = HavlakLoopFinder(app.cfg, app.lsg)
    finder.findLoops()
    app.getMem()

    println("Another 50 iterations...")
    val start : Long = System.currentTimeMillis()
//    val maxMemory : Long = Runtime.getRuntime().maxMemory()
    for (i in 1..50) {
        print(".")
//        println(maxMemory - (Runtime.getRuntime().freeMemory()))
        val finder2 : HavlakLoopFinder = HavlakLoopFinder(app.cfg, LSG())
        finder2.findLoops()
    }
    println("\nTime: ${System.currentTimeMillis() - start} ms")
    println()

    app.getMem()
    println("# of loops: ${app.lsg.getNumLoops()} (including 1 artificial root node)")
    println("# of BBs  : ${BasicBlock.numBasicBlocks}")

    assertEquals(121002, app.lsg.getNumLoops())
    assertEquals(252013, BasicBlock.numBasicBlocks)

    // println("# max time: " + (finder.getMaxMillis())!!)
    // println("# min time: " + (finder.getMinMillis())!!)
//    app.lsg.calculateNestingLevel()
}
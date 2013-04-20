/**
 * Created with IntelliJ IDEA. 
 * User: Nutzer
 * Date: 22.01.13
 * Time: 21:38
 * To change this template use File | Settings | File Templates.
 */
//
// LoopStructureGraph
//
// Maintain loop structure for a given CFG.
//
// Two values are maintained for this loop graph, depth, and nesting level.
// For example:
//
// loop        nesting level    depth
//----------------------------------------
//   loop-0    2                0
//   loop-1    1                1
//   loop-3    1                1
//   loop-2    0                2

import java.util.LinkedList

class LSG
{
    class object {
        var loopCounter = 0
    }

    val loops = LinkedList<SimpleLoop>()
    val root = SimpleLoop();

    {
        root.nestingLevel = 0
        root.counter = LSG.loopCounter++
        addLoop(root)
    }

    fun createNewLoop(): SimpleLoop {
        val loop = SimpleLoop()
        loop.counter = LSG.loopCounter++
        return loop
    }

    fun addLoop(loop: SimpleLoop) {
        loops.add(loop)
    }

    fun dump() = dumpRec(root, 0)

    fun dumpRec(loop: SimpleLoop, indent: Int) {
        loop.dump(indent)
        for (liter in loop.children) {
            dumpRec(liter, indent + 1)
        }
    }

    fun calculateNestingLevel() {
        for (liter in loops) {
            if (!liter.isRoot && liter.parent == null)
                liter.parent = root
        }

        calculateNestingLevelRec(root, 0)
    }

    fun max(a: Int, b: Int) = if (a > b) a else b

    fun calculateNestingLevelRec(loop: SimpleLoop, depth: Int) {
        loop.depthLevel = depth
        for (liter in loop.children) {
            calculateNestingLevelRec(liter, depth + 1)
            loop.nestingLevel = max(loop.nestingLevel, 1 + liter.nestingLevel)
        }
    }

    fun getNumLoops(): Int = loops.size()
}
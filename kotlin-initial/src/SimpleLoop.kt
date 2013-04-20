/**
 * Created with IntelliJ IDEA. 
 * User: Nutzer
 * Date: 22.01.13
 * Time: 21:37
 * To change this template use File | Settings | File Templates.
 */
//
// class SimpleLoop
//
// Basic representation of loops, a loop has an entry point,
// one or more exit edges, a set of basic blocks, and potentially
// an outer loop - a "parent" loop.
//
// Furthermore, it can have any set of properties, e.g.,
// it can be an irreducible loop, have control flow, be
// a candidate for transformations, and what not.
//
import java.util.HashSet

class SimpleLoop
{
    var basicBlocks = HashSet<BasicBlock>()
    var children = HashSet<SimpleLoop>()
    var parent: SimpleLoop? = null
    var header: BasicBlock? = null

    var isRoot: Boolean = false
    var isReducible: Boolean = true
    var counter: Int = 0
    var nestingLevel: Int = 0
    var depthLevel: Int = 0

    // method made to return a dummy value as a work around for
    // http://youtrack.jetbrains.com/issue/KT-904
    fun addNode(bb: BasicBlock): String {
        basicBlocks.add(bb)
        return ""
    }

    // method made to return a dummy value as a work around for
    // http://youtrack.jetbrains.com/issue/KT-904
    fun addChildLoop(loop: SimpleLoop): String {
        children.add(loop)
        return "";
    }

    fun dump(indent: Int) {
        for (i in 0..indent)
            System.out.format("  ")

        System.out.format("loop-%d nest: %d depth %d %s\n",
                counter,
                nestingLevel,
                depthLevel,
                if (isReducible) ""
                else "(Irreducible) ");
    }

    // method made to return a dummy value as a work around for
    // http://youtrack.jetbrains.com/issue/KT-904
    fun setSimpleLoopParent(parent: SimpleLoop): String {
        this.parent = parent
        this.parent?.addChildLoop(this)
        return ""
    }

    // method made to return a dummy value as a work around for
    // http://youtrack.jetbrains.com/issue/KT-904
    fun setHeader(bb: BasicBlock): String {
        basicBlocks.add(bb)
        header = bb
        return ""
    }

    // method made to return a dummy value as a work around for
    // http://youtrack.jetbrains.com/issue/KT-904
    fun setTheNestingLevel(level: Int): String {
        nestingLevel = level
        isRoot = level == 0
        return ""
    }
}
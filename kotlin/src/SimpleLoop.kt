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
    val basicBlocks = HashSet<BasicBlock>()
    val children = HashSet<SimpleLoop>()
    var parent: SimpleLoop? = null
        set(p: SimpleLoop?) {
            $parent = p;
            p?.addChildLoop(this);
        }
    var header: BasicBlock? = null
        set(bb: BasicBlock?) {
            basicBlocks.add(bb!!)
            $header = bb
        }

    var isRoot = false
    var isReducible = true
    var counter = 0
    var nestingLevel = 0
        set(level: Int) {
            $nestingLevel = level
            isRoot = level == 0
        }
    var depthLevel = 0

    fun addNode(bb: BasicBlock) {
        basicBlocks.add(bb)
    }

    fun addChildLoop(loop: SimpleLoop) {
        children.add(loop)
    }

    fun dump(indent: Int) {
        for (i in 0..indent)
            print("  ")

        println("loop-$counter nest: $nestingLevel depth $depthLevel ${ if (isReducible) "" else "(Irreducible)" }");
    }
}
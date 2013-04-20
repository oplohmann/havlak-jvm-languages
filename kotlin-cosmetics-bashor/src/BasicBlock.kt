/**
* Created with IntelliJ IDEA.
* User: Nutzer
* Date: 22.01.13
* Time: 21:36
* To change this template use File | Settings | File Templates.
*/
import java.util.ArrayList

class BasicBlock(val name: Int)
{
    {
        numBasicBlocks++;
    }

    val inEdges = ArrayList<BasicBlock>()
    val outEdges = ArrayList<BasicBlock>()

    fun toString() = "BB#$name"
    fun getNumPred(): Int = inEdges.size()
    fun getNumSucc(): Int = outEdges.size()

    fun addInEdge (bb: BasicBlock) {
        inEdges.add(bb)
    }
    fun addOutEdge(bb: BasicBlock) {
        outEdges.add(bb)
    }

    fun dump(): Unit {
        var res = "BB#$name"
        if (inEdges.size() > 0)
            res += "\tin : " + inEdges
        if (outEdges.size() > 0)
            res += "\tout: " + outEdges
    }

    class object
    {
        var numBasicBlocks: Int = 0
    }
}
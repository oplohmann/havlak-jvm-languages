/**
 * Created with IntelliJ IDEA. 
 * User: Nutzer
 * Date: 22.01.13
 * Time: 21:36
 * To change this template use File | Settings | File Templates.
 */

import java.util.LinkedList

class BasicBlock(val name: Int)
{
    {
        numBasicBlocks++;
    }

    val inEdges = LinkedList<BasicBlock>()
    val outEdges = LinkedList<BasicBlock>()

    fun toString() = "BB#$name"
    fun getNumPred() = inEdges.size()
    fun getNumSucc() = outEdges.size()

    fun addInEdge (bb: BasicBlock) {
        inEdges.add(bb)
    }
    fun addOutEdge(bb: BasicBlock) {
        outEdges.add(bb)
    }

    fun dump() {
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
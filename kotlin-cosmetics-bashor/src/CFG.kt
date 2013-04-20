/**
* Created with IntelliJ IDEA.
* User: Nutzer
* Date: 22.01.13
* Time: 21:36
* To change this template use File | Settings | File Templates.
*/
//
// class CFG
//
// CFG maintains a list of nodes, plus a start node.
// That's it.
//
import java.util.HashMap
import java.util.ArrayList

class CFG()
{
    var startNode: BasicBlock? = null
    val basicBlockMap = HashMap<Int, BasicBlock>()
    val edgeList = ArrayList<BasicBlockEdge>()

    fun createNode(name: Int): BasicBlock {
        fun createAndRegisterBaseBlock(name: Int) : BasicBlock {
            val tmp = BasicBlock(name)
            basicBlockMap.put(name, tmp)
            return tmp;
        }

        val node = basicBlockMap[name] ?: createAndRegisterBaseBlock(name)

        if (getNumNodes() == 1)
            startNode = node

        return node
    }

    fun dump() {
        for (bb in basicBlockMap.values())
            bb.dump()
    }

    fun addEdge(edge: BasicBlockEdge) {
        edgeList.add(edge)
    }

    fun getNumNodes() = basicBlockMap.size()
    fun getDst(edge: BasicBlockEdge): BasicBlock = edge.to
    fun getSrc(edge: BasicBlockEdge): BasicBlock = edge.from
}
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
    var basicBlockMap = HashMap<Int, BasicBlock>()
    var edgeList = ArrayList<BasicBlockEdge>()

    fun createNode(name: Int): BasicBlock
    {
        var node = basicBlockMap.get(name)
        if(node == null) {
            val tmp = BasicBlock(name)
            tmp.init()
            basicBlockMap.put(name, tmp)
            node = tmp
        }

        if (getNumNodes() == 1)
            startNode = node

        return node!!
    }

    fun dump() {
        for (bb in basicBlockMap.values())
            bb.dump()
    }

    fun addEdge(edge: BasicBlockEdge): Unit {
        edgeList.add(edge)
    }

    fun getNumNodes(): Int = basicBlockMap.size()
    fun getDst(edge: BasicBlockEdge): BasicBlock = edge.to
    fun getSrc(edge: BasicBlockEdge): BasicBlock = edge.from
}
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

    var inEdges = ArrayList<BasicBlock>()
    var outEdges = ArrayList<BasicBlock>()

    ;{
        BasicBlock.incrementBasicBlocks()
    }

    override fun toString() = "BB#" + name
    fun getNumPred(): Int = inEdges.size()
    fun getNumSucc(): Int = outEdges.size()

    fun addInEdge (bb: BasicBlock) {
        inEdges.add(bb)
    }
    fun addOutEdge(bb: BasicBlock) {
        outEdges.add(bb)
    }

    fun dump(): Unit {
        var res = "BB#" + name + " "
        if (inEdges.size() > 0)
            res += "\tin : " + inEdges
        if (outEdges.size() > 0)
            res += "\tout: " + outEdges
        // println(res)
    }

    class object
    {
        var numBasicBlocks: Int = 0

        // method made to return a dummy value as a work around for
        // http://youtrack.jetbrains.com/issue/KT-904
        fun setMyNumBasicBlocks(n: Int) : Int {
            numBasicBlocks = n
            return numBasicBlocks
        }

        fun incrementBasicBlocks() : String {
            setMyNumBasicBlocks(numBasicBlocks + 1)
            return ""
        }

        // method renamed from getNumBasicBlocks to getMyNumBasicBlocks as a work
        // around for http://youtrack.jetbrains.com/issue/KT-904
        fun getMyNumBasicBlocks(): Int = numBasicBlocks
    }
}
/**
 * Created with IntelliJ IDEA. 
 * User: Nutzer
 * Date: 22.01.13
 * Time: 21:39
 * To change this template use File | Settings | File Templates.
 */
import java.util.ArrayList

/**
* class UnionFindNode
*
* The algorithm uses the Union/Find algorithm to collapse
* complete loops into a single node. These nodes and the
* corresponding functionality are implemented with this class
*/

class UnionFindNode {

    var parent: UnionFindNode? = null
    var bb: BasicBlock? = null
    var loop: SimpleLoop? = null
    var dfsNumber: Int = 0

    // Initialize this node.
    //
    fun initNode(bb: BasicBlock, dfsNumber: Int): Unit {
        this.parent = this
        this.bb = bb
        this.dfsNumber = dfsNumber
        this.loop = null
    }

    // Union/Find Algorithm - The find routine.
    //
    // Implemented with Path Compression (inner loops are only
    // visited and collapsed once, however, deep nests would still
    // result in significant traversals).
    //
    fun findSet(): UnionFindNode
    {
        var nodeList = ArrayList<UnionFindNode>()

        var node = this
        while (node != node.parent) {
            if (node.parent != node.parent?.parent) {
                nodeList.add(node)
            }
            node = node.parent!!
        }

        // Path Compression, all nodes' parents point to the 1st level parent.
        for (iter in nodeList)
            iter.parent = node.parent

        return node
    }

    // Union/Find Algorithm - The union routine.
    //
    // Trivial. Assigning parent pointer is enough,
    // we rely on path compression.
    //
    fun union(basicBlock: UnionFindNode): Unit {
        parent = basicBlock
    }
}
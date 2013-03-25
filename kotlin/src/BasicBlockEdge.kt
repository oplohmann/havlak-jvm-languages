/**
 * Created with IntelliJ IDEA.
 * User: Nutzer
 * Date: 22.01.13
 * Time: 21:37
 * To change this template use File | Settings | File Templates.
 */
//
// class BasicBlockEdge
//
// These data structures are stubbed out to make the code below easier
// to review.
//
// BasicBlockEdge only maintains two pointers to BasicBlocks.
//
class BasicBlockEdge(cfg: CFG, fromName: Int, toName: Int)
{
    var from: BasicBlock = cfg.createNode(fromName)
    var to: BasicBlock = cfg.createNode(toName)

    constructor {
        from.addOutEdge(to)
        to.addInEdge(from)
        cfg.addEdge(this)
    }

}
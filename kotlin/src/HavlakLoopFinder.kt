/**
 * Created with IntelliJ IDEA.
 * User: plohmann 
 * Date: 16.01.13
 * Time: 14:14
 * To change this template use File | Settings | File Templates.
 */
// Copyright 2011 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

//======================================================
// Scaffold Code
//======================================================

//
// class BasicBlock
//
// BasicBlock only maintains a vector of in-edges and
// a vector of out-edges.
//
import java.util.ArrayList
import java.util.HashMap
import java.util.LinkedList

class HavlakLoopFinder(val cfg: CFG, val lsg: LSG) {

    enum class BasicBlockClass {
        BB_TOP          // uninitialized
        BB_NONHEADER    // a regular BB
        BB_REDUCIBLE    // reducible loop
        BB_SELF         // single BB loop
        BB_IRREDUCIBLE  // irreducible loop
        BB_DEAD         // a dead BB
        BB_LAST          // Sentinel
    }

    val UNVISITED = -1

    // Safeguard against pathologic algorithm behavior.
    val MAX_NONBACK_PREDS = 32 * 1024

    //
    // IsAncestor
    //
    // As described in the paper, determine whether a node 'w' is a
    // "true" ancestor for node 'v'.
    //
    // Dominance can be tested quickly using a pre-order trick
    // for depth-first spanning trees. This is why DFS is the first
    // thing we run below.
    //
    fun isAncestor(w: Int, v: Int, last: IntArray): Boolean = (w <= v) && (v <= last[w])

    //
    // DFS - Depth-First-Search
    //
    // DESCRIPTION:
    // Simple depth first traversal along out edges with node numbering.
    //
    fun doDFS(
            currentNode: BasicBlock,
            nodes: ArrayList<UnionFindNode>,
            number: MutableMap<BasicBlock, Int>,
            last: IntArray,
            current: Int): Int
    {
        nodes[current].initNode(currentNode, current)
        number[currentNode] = current

        var lastid = current
        for (target in currentNode.outEdges) {
            if (number[target] == UNVISITED)
                lastid = doDFS(target, nodes, number, last, lastid + 1)
        }

        last[number[currentNode]!!] = lastid
        return lastid
    }

    //
    // findLoops
    //
    // Find loops and build loop forest using Havlak's algorithm, which
    // is derived from Tarjan. Variable names and step numbering has
    // been chosen to be identical to the nomenclature in Havlak's
    // paper (which, in turn, is similar to the one used by Tarjan).
    //
    fun findLoops(): Int {
        val startNode = cfg.startNode
        if (startNode == null)
            return 0

        val size = cfg.numNodes

        val nonBackPreds = ArrayList<LinkedList<Int>>(size)
        val backPreds = ArrayList<LinkedList<Int>>(size)

        val number = HashMap<BasicBlock, Int>()

        val header = IntArray(size)
        val types = ArrayList<BasicBlockClass>(size)
        val last = IntArray(size)
        val nodes = ArrayList<UnionFindNode>(size)

        for (i in 0..size - 1) {
            nonBackPreds.add(LinkedList<Int>())
            backPreds.add(LinkedList<Int>())
            types.add(BasicBlockClass.BB_TOP)
            nodes.add(UnionFindNode())
        }

        // Step a:
        //   - initialize all nodes as unvisited.
        //   - depth-first traversal and numbering.
        //   - unreached BB's are marked as dead.
        //
        for (value in cfg.basicBlockMap.values()) {
            number[value] = UNVISITED
        }

        doDFS(startNode, nodes, number, last, 0)

        // Step b:
        //   - iterate over all nodes.
        //
        //   A backedge comes from a descendant in the DFS tree, and non-backedges
        //   from non-descendants (following Tarjan).
        //
        //   - check incoming edges 'v' and add them to either
        //     - the list of backedges (backPreds) or
        //     - the list of non-backedges (nonBackPreds)
        //
        for (w in 0..size - 1) {
            header[w] = 0
            types[w] = BasicBlockClass.BB_NONHEADER

            val nodeW = nodes[w].bb
            if (nodeW == null) {
                types[w] = BasicBlockClass.BB_DEAD
                // No 'continue'
            }
            else {
                if (nodeW.getNumPred() > 0) {
                    for (nodeV in nodeW.inEdges) {
                        val v = number[nodeV]!!
                        if (v != UNVISITED) {
                            if (isAncestor(w, v, last)) {
                                backPreds[w].add(v)
                            } else {
                                nonBackPreds[w].add(v);
                            }
                        }
                    }
                }
            }
        }

        // Start node is root of all other loops.
        header[0] = 0

        // Step c:
        //
        // The outer loop, unchanged from Tarjan. It does nothing except
        // for those nodes which are the destinations of backedges.
        // For a header node w, we chase backward from the sources of the
        // backedges adding nodes to the set P, representing the body of
        // the loop headed by w.
        //
        // By running through the nodes in reverse of the DFST preorder,
        // we ensure that inner loop headers will be processed before the
        // headers for surrounding loops.
        //
        for (w in size - 1 downTo 0) {
            // this is 'P' in Havlak's paper
            val nodePool = LinkedList<UnionFindNode>()

            val nodeW = nodes[w].bb
            if (nodeW != null) {
                // dead BB

                // Step d:
                for (v in backPreds[w]) {
                    if (v != w) {
                        nodePool.add(nodes[v].findSet())
                    } else {
                        types[w] = BasicBlockClass.BB_SELF
                    }
                }

                // Copy nodePool to workList.
                //
                val workList = LinkedList<UnionFindNode>(nodePool)

                if (nodePool.size() != 0) {
                    types[w] = BasicBlockClass.BB_REDUCIBLE
                }

                // work the list...
                //
                while (!workList.isEmpty()) {
                    val x = workList.removeFirst()

                    // Step e:
                    //
                    // Step e represents the main difference from Tarjan's method.
                    // Chasing upwards from the sources of a node w's backedges. If
                    // there is a node y' that is not a descendant of w, w is marked
                    // the header of an irreducible loop, there is another entry
                    // into this loop that avoids w.
                    //

                    // The algorithm has degenerated. Break and
                    // return in this case.
                    //
                    val nonBackSize = nonBackPreds[x.dfsNumber].size()
                    if (nonBackSize > MAX_NONBACK_PREDS) {
                        return 0
                    }

                    for (iter in nonBackPreds[x.dfsNumber]) {
                        val y = nodes[iter]
                        val ydash = y.findSet()

                        if (!isAncestor(w, ydash.dfsNumber, last)) {
                            types[w] = BasicBlockClass.BB_IRREDUCIBLE
                            nonBackPreds[w].add(ydash.dfsNumber)
                        }
                        else if (ydash.dfsNumber != w && !nodePool.contains(ydash)) {
                            workList.add(ydash)
                            nodePool.add(ydash)
                        }
                    }
                }

                // Collapse/Unionize nodes in a SCC to a single node
                // For every SCC found, create a loop descriptor and link it in.
                //
                if ((nodePool.size() > 0) || (types[w] == BasicBlockClass.BB_SELF)) {
                    val loop = lsg.createNewLoop()

                    loop.header = nodeW
                    loop.isReducible = types[w] != BasicBlockClass.BB_IRREDUCIBLE

                    // At this point, one can set attributes to the loop, such as:
                    //
                    // the bottom node:
                    //    iter  = backPreds(w).begin();
                    //    loop bottom is: nodes(iter).node;
                    //
                    // the number of backedges:
                    //    backPreds(w).size()
                    //
                    // whether this loop is reducible:
                    //    types(w) != BB_IRREDUCIBLE
                    //
                    nodes[w].loop = loop

                    for (node in nodePool) {
                        // Add nodes to loop descriptor.
                        header[node.dfsNumber] = w
                        node.union(nodes[w])

                        // Nested loops are not added, but linked together.
                        val l = node.loop
                        if (l != null) {
                            l.parent = loop
                        } else {
                            loop.addNode(node.bb!!)
                        }
                    }

                    lsg.addLoop(loop)
                }  // nodePool.size
            }  // dead BB
        }  // Step c

        return lsg.getNumLoops()
    }  // findLoops
}

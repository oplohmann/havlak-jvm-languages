// Copyright 2011 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License")
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
// Main Algorithm
//======================================================

/**
 * The Havlak loop finding algorithm.
 *
 * @author rhundt
 */
/*
 * Java code ported to Groovy by Oliver Plohmann
 */

package havlakloopfinder

import cfg.BasicBlock
import cfg.CFG
import lsg.LSG
import lsg.SimpleLoop

import java.util.*

/**
 * class HavlakLoopFinder
 *
 * This class encapsulates the complete finder algorithm
 */
public class HavlakLoopFinder {

    def CFG cfg      // Control Flow Graph
    def LSG lsg      // Loop Structure Graph

    def static long maxMillis = 0
    def static long minMillis = Integer.MAX_VALUE

    def HavlakLoopFinder(def cfg, def lsg) {
        this.cfg = cfg
        this.lsg = lsg
    }

    /**
     * enum BasicBlockClass
     *
     * Basic Blocks and Loops are being classified as regular, irreducible,
     * and so on. This enum contains a symbolic name for all these classifications
     */
    def enum BasicBlockClass {
        BB_TOP,          // uninitialized
        BB_NONHEADER,    // a regular BB
        BB_REDUCIBLE,    // reducible loop
        BB_SELF,         // single BB loop
        BB_IRREDUCIBLE,  // irreducible loop
        BB_DEAD,         // a dead BB
        BB_LAST          // Sentinel
    }


    /**
     * class UnionFindNode
     *
     * The algorithm uses the Union/Find algorithm to collapse
     * complete loops into a single node. These nodes and the
     * corresponding functionality are implemented with this class
     */
    public class UnionFindNode {

        def UnionFindNode() {
        }

        // Initialize this node.
        //
        def initNode(def bb, def dfsNumber) {
            this.parent     = this
            this.bb         = bb
            this.dfsNumber  = dfsNumber
            this.loop       = null
        }

        // Union/Find Algorithm - The find routine.
        //
        // Implemented with Path Compression (inner loops are only
        // visited and collapsed once, however, deep nests would still
        // result in significant traversals).
        //
        def UnionFindNode findSet() {

            def nodeList = new ArrayList<UnionFindNode>()

            def node = this
            while (node != node.getParent()) {
                if (node.getParent() != node.getParent().getParent()) {
                    nodeList.add(node)
                }
                node = node.getParent()
            }

            // Path Compression, all nodes' parents point to the 1st level parent.
            for (iter in nodeList)
                iter.setParent(node.getParent())
            return node
        }

        // Union/Find Algorithm - The union routine.
        //
        // Trivial. Assigning parent pointer is enough,
        // we rely on path compression.
        //
        def union(def basicBlock) {
            setParent(basicBlock)
        }

        def parent
        def bb
        def loop
        def dfsNumber
    }

    //
    // Constants
    //
    // Marker for uninitialized nodes.
    def static final UNVISITED = Integer.MAX_VALUE

    // Safeguard against pathologic algorithm behavior.
    def static final MAXNONBACKPREDS = (32 * 1024)

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
    def isAncestor(def w, def v, def last) {
        return (w <= v) && (v <= last[w])
    }

    //
    // DFS - Depth-First-Search
    //
    // DESCRIPTION:
    // Simple depth first traversal along out edges with node numbering.
    //
    def doDFS(def currentNode,
              def nodes,
              def number,
              def last,
              def final current) {
        nodes[current].initNode(currentNode, current)
        number.put(currentNode, current)

        def lastid = current
        for (target in currentNode.getOutEdges()) {
            if (number.get(target) == UNVISITED) {
                lastid = doDFS(target, nodes, number, last, lastid + 1)
            }
        }
        last[number.get(currentNode)] = lastid
        return lastid
    }

    static def nonBackPreds = new ArrayList<Set<Integer>>()
    static def backPreds = new ArrayList<List<Integer>>()
    static def number = new HashMap<BasicBlock, Integer>()
    static def maxSize = 0
    static def header
    static def type
    static def last
    static def nodes
    //
    // findLoops
    //
    // Find loops and build loop forest using Havlak's algorithm, which
    // is derived from Tarjan. Variable names and step numbering has
    // been chosen to be identical to the nomenclature in Havlak's
    // paper (which, in turn, is similar to the one used by Tarjan).
    //
    def findLoops() {
        if (cfg.getStartBasicBlock() == null) {
            return
        }

        def startMillis = System.currentTimeMillis()

        def size = cfg.getNumNodes()

        nonBackPreds.clear()
        backPreds.clear()
        number.clear()
        if (size > maxSize) {
            header = new int[size]
            type = new BasicBlockClass[size]
            last = new int[size]
            nodes = new UnionFindNode[size]
            maxSize = size
        }

        for (def i = 0; i < size; ++i) {
            nonBackPreds.add(new HashSet<Integer>())
            backPreds.add(new ArrayList<Integer>())
            nodes[i] = new UnionFindNode()
        }

        // Step a:
        //   - initialize all nodes as unvisited.
        //   - depth-first traversal and numbering.
        //   - unreached BB's are marked as dead.
        //
        for (bbIter in cfg.getBasicBlocks().values()) {
            number.put(bbIter, UNVISITED)
        }

        doDFS(cfg.getStartBasicBlock(), nodes, number, last, 0)

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
        for (def w = 0; w < size; w++) {
            header[w] = 0
            type[w] = BasicBlockClass.BB_NONHEADER

            def nodeW = nodes[w].getBb()
            if (nodeW == null) {
                type[w] = BasicBlockClass.BB_DEAD
                continue  // dead BB
            }

            if (nodeW.getNumPred() > 0) {
                for (nodeV in nodeW.getInEdges()) {
                    def v = number.get(nodeV)
                    if (v == UNVISITED) {
                        continue  // dead node
                    }

                    if (isAncestor(w, v, last)) {
                        backPreds.get(w).add(v)
                    } else {
                        nonBackPreds.get(w).add(v)
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
        for (def w = size - 1; w >= 0; w--) {
            // this is 'P' in Havlak's paper
            def nodePool = new LinkedList<UnionFindNode>()

            def nodeW = nodes[w].getBb()
            if (nodeW == null) {
                continue  // dead BB
            }

            // Step d:
            for (def v : backPreds.get(w)) {
                if (v != w) {
                    nodePool.add(nodes[v].findSet())
                } else {
                    type[w] = BasicBlockClass.BB_SELF
                }
            }

            // Copy nodePool to workList.
            //
            def workList = new LinkedList<UnionFindNode>()
            for (niter in nodePool)
                workList.add(niter)

            if (nodePool.size() != 0) {
                type[w] = BasicBlockClass.BB_REDUCIBLE
            }

            // work the list...
            //
            while (!workList.isEmpty()) {
                def x = workList.getFirst()
                workList.removeFirst()

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
                def nonBackSize = nonBackPreds.get(x.getDfsNumber()).size()
                if (nonBackSize > MAXNONBACKPREDS) {
                    return
                }

                for (iter in nonBackPreds.get(x.getDfsNumber())) {
                    def y = nodes[iter]
                    def ydash = y.findSet()

                    if (!isAncestor(w, ydash.getDfsNumber(), last)) {
                        type[w] = BasicBlockClass.BB_IRREDUCIBLE
                        nonBackPreds.get(w).add(ydash.getDfsNumber())
                    } else {
                        if (ydash.getDfsNumber() != w) {
                            if (!nodePool.contains(ydash)) {
                                workList.add(ydash)
                                nodePool.add(ydash)
                            }
                        }
                    }
                }
            }

            // Collapse/Unionize nodes in a SCC to a single node
            // For every SCC found, create a loop descriptor and link it in.
            //
            if ((nodePool.size() > 0) || (type[w] == BasicBlockClass.BB_SELF)) {
                def loop = lsg.createNewLoop()

                loop.setHeader(nodeW)
                loop.setIsReducible(type[w] != BasicBlockClass.BB_IRREDUCIBLE)

                // At this point, one can set attributes to the loop, such as:
                //
                // the bottom node:
                //    iter  = backPreds[w].begin()
                //    loop bottom is: nodes[iter].node)
                //
                // the number of backedges:
                //    backPreds[w].size()
                //
                // whether this loop is reducible:
                //    type[w] != BasicBlockClass.BB_IRREDUCIBLE
                //
                nodes[w].setLoop(loop)

                for (node in nodePool) {
                    // Add nodes to loop descriptor.
                    header[node.getDfsNumber()] = w
                    node.union(nodes[w])

                    // Nested loops are not added, but linked together.
                    if (node.getLoop() != null) {
                        node.getLoop().setParent(loop)
                    } else {
                        loop.addNode(node.getBb())
                    }
                }

                lsg.addLoop(loop)
            }  // nodePool.size
        }  // Step c

        def totalMillis = System.currentTimeMillis() - startMillis

        if (totalMillis > maxMillis) {
            maxMillis = totalMillis
        }
        if (totalMillis < minMillis) {
            minMillis = totalMillis
        }
    }  // findLoops

}


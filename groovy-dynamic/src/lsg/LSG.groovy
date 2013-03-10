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

/**
 * Loop Structure Graph - Scaffold Code
 *
 * @author rhundt
 */

/*
 * Java code ported to Groovy by Oliver Plohmann
 */

package lsg;

import java.util.ArrayList;
import java.util.List;

/**
 * LoopStructureGraph
 *
 * Maintain loop structure for a given CFG.
 *
 * Two values are maintained for this loop graph, depth, and nesting level.
 * For example:
 *
 * loop        nesting level    depth
 *----------------------------------------
 * loop-0      2                0
 *   loop-1    1                1
 *   loop-3    1                1
 *     loop-2  0                2
 */
public class LSG {

    def root
    def loops
    def loopCounter

    def LSG() {
        loopCounter = 0
        loops = new ArrayList<SimpleLoop>()
        root = new SimpleLoop()
        root.setNestingLevel(0)
        root.setCounter(loopCounter++)
        addLoop(root)
    }

    def createNewLoop() {
        def loop = new SimpleLoop()
        loop.setCounter(loopCounter++)
        return loop
    }

    def addLoop(def loop) {
        loops.add(loop)
    }

    def dump() {
        dumpRec(root, 0)
    }

    def private dumpRec(def loop, def indent) {
        // Simplified for readability purposes.
        lodefop.dump(indent)

        for(liter in loop.getChildren())
            dumpRec(liter,  indent + 1)
    }

    def calculateNestingLevel() {
        // link up all 1st level loops to artificial root node.
        for(liter in loops) {
            if (liter.isRoot()) {
                continue
            }
            if (liter.getParent() == null) {
                liter.setParent(root)
            }
        }

        // recursively traverse the tree and assign levels.
        calculateNestingLevelRec(root, 0)
    }

    def calculateNestingLevelRec(def loop, def depth) {
        loop.setDepthLevel(depth)
        for(liter in loop.getChildren()) {
            calculateNestingLevelRec(liter, depth + 1)
            loop.setNestingLevel(Math.max(loop.getNestingLevel(), 1 + liter.getNestingLevel()))
        }
    }

    def getNumLoops() {
        return loops.size()
    }

}

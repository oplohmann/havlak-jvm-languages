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
    public LSG() {
        loopCounter = 0;
        loops = new ArrayList<SimpleLoop>();
        root = new SimpleLoop();
        root.setNestingLevel(0);
        root.setCounter(loopCounter++);
        addLoop(root);
    }

    public SimpleLoop createNewLoop() {
        SimpleLoop loop = new SimpleLoop();
        loop.setCounter(loopCounter++);
        return loop;
    }

    public void addLoop(SimpleLoop loop) {
        loops.add(loop);
    }

    public void dump() {
        dumpRec(root, 0);
    }

    private void dumpRec(SimpleLoop loop, int indent) {
        // Simplified for readability purposes.
        loop.dump(indent);

        for (SimpleLoop liter : loop.getChildren())
            dumpRec(liter,  indent + 1);
    }

    public void calculateNestingLevel() {
        // link up all 1st level loops to artificial root node.
        for (SimpleLoop liter : loops) {
            if (liter.isRoot()) {
                continue;
            }
            if (liter.getParent() == null) {
                liter.setParent(root);
            }
        }

        // recursively traverse the tree and assign levels.
        calculateNestingLevelRec(root, 0);
    }

    public void calculateNestingLevelRec(SimpleLoop loop, int depth) {
        loop.setDepthLevel(depth);
        for (SimpleLoop liter : loop.getChildren()) {
            calculateNestingLevelRec(liter, depth + 1);

            loop.setNestingLevel(Math.max(loop.getNestingLevel(),
                    1 + liter.getNestingLevel()));
        }
    }

    public int getNumLoops() {
        return loops.size();
    }
    public SimpleLoop getRoot() {
        return root;
    }

    private SimpleLoop root;
    private List<SimpleLoop> loops;
    private int              loopCounter;
};

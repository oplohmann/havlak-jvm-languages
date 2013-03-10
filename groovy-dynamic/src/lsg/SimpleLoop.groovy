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
// Scaffold Code
//======================================================

/**
 * The Havlak loop finding algorithm.
 *
 * @author rhundt
 */
package lsg

import cfg.BasicBlock

import java.util.HashSet
import java.util.Set

/**
 * class SimpleLoop
 *
 * Basic representation of loops, a loop has an entry point,
 * one or more exit edges, a set of basic blocks, and potentially
 * an outer loop - a "parent" loop.
 *
 * Furthermore, it can have any set of properties, e.g.,
 * it can be an irreducible loop, have control flow, be
 * a candidate for transformations, and what not.
 */
public class SimpleLoop
{
    def basicBlocks
    def children
    def parent
    def header

    def isRoot
    def isReducible
    def counter
    def nestingLevel
    def depthLevel

    def SimpleLoop() {
        parent = null
        isRoot = false
        isReducible  = true
        nestingLevel = 0
        depthLevel   = 0
        basicBlocks  = new HashSet<BasicBlock>()
        children     = new HashSet<SimpleLoop>()
    }

    def addNode(def bb) {
        basicBlocks.add(bb)
    }

    def addChildLoop(def loop) {
        children.add(loop)
    }

    def dump(def indent) {
        for (def i = 0; i < indent; i++)
            System.out.format("  ")

        System.out.format("loop-%d nest: %d depth %d %s",
                counter, nestingLevel, depthLevel,
                isReducible ? "" : "(Irreducible) ")
        if (!getChildren().isEmpty()) {
            System.out.format("Children: ")
            for (SimpleLoop loop : getChildren()) {
                System.out.format("loop-%d ", loop.getCounter())
            }
        }
        if (!basicBlocks.isEmpty()) {
            System.out.format("(")
            for (bb in basicBlocks) {
                System.out.format("BB#%d%s", bb.getName(), header == bb ? "* " : " ")
            }
            System.out.format("\b)")
        }
        System.out.format("\n")
    }

    def setParent(def parent) {
        this.parent = parent
        this.parent.addChildLoop(this)
    }

    def setHeader(def bb) {
        basicBlocks.add(bb)
        header = bb
    }

    def isRoot() {
        return isRoot;
    }

    def setIsRoot() {
        isRoot = true
    }

    def setNestingLevel(def level) {
        nestingLevel = level
        if (level == 0) {
            setIsRoot()
        }
    }

    def setIsReducible(def isReducible) {
        this.isReducible = isReducible
    }

}


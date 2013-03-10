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

/**
 * A simple class simulating the concept of Basic Blocks
 *
 * @author rhundt
 */

package cfg;

import java.util.ArrayList;
import java.util.List;

/**
 * class BasicBlock
 *
 * BasicBlock only maintains a vector of in-edges and
 * a vector of out-edges.
 */
public class BasicBlock {

    def static numBasicBlocks = 0

    def static getNumBasicBlocks() {
        return numBasicBlocks
    }

    def BasicBlock(def name) {
        this.name = name
        inEdges   = new ArrayList<BasicBlock>()
        outEdges  = new ArrayList<BasicBlock>()
        ++numBasicBlocks
    }

    def dump() {
        System.out.format("BB#%03d: ", getName())
        if (inEdges.size() > 0) {
            System.out.format("in : ")
            for (def bb : inEdges) {
                System.out.format("BB#%03d ", bb.getName())
            }
        }
        if (outEdges.size() > 0) {
            System.out.format("out: ")
            for (def bb : outEdges) {
                System.out.format("BB#%03d ", bb.getName())
            }
        }
        System.out.println()
    }

    def getName() {
        return name
    }

    def getInEdges() {
        return inEdges
    }

    def getOutEdges() {
        return outEdges
    }

    def getNumPred() {
        return inEdges.size()
    }

    def getNumSucc() {
        return outEdges.size()
    }

    def addOutEdge(def to) {
        outEdges.add(to)
    }

    def addInEdge(def from) {
        inEdges.add(from)
    }

    def private inEdges, outEdges
    def private name
}

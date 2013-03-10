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
// Test Code
//======================================================

/**
 * Test Program for the Havlak loop finder.
 *
 * This program constructs a fairly large control flow
 * graph and performs loop recognition. This is the Java
 * version.
 *
 * @author rhundt
 */
import cfg.BasicBlock
import cfg.BasicBlockEdge
import cfg.CFG
import havlakloopfinder.HavlakLoopFinder
import lsg.LSG

class LoopTesterApp
{
    def CFG cfg
    def LSG lsg
    def BasicBlock root

    public LoopTesterApp() {
        cfg = new CFG()
        lsg = new LSG()
        root = cfg.createNode(0)
    }

    // Create 4 basic blocks, corresponding to and if/then/else clause
    // with a CFG that looks like a diamond
    def buildDiamond(def start) {
        def bb0 = start
        new BasicBlockEdge(cfg, bb0, bb0 + 1)
        new BasicBlockEdge(cfg, bb0, bb0 + 2)
        new BasicBlockEdge(cfg, bb0 + 1, bb0 + 3)
        new BasicBlockEdge(cfg, bb0 + 2, bb0 + 3)

        return bb0 + 3
    }

    // Connect two existing nodes
    def buildConnect(def start, def end) {
        new BasicBlockEdge(cfg, start, end)
    }

    // Form a straight connected sequence of n basic blocks
    def buildStraight(def start, def n) {
        for (def i = 0; i < n; i++)
            buildConnect(start + i, start + i + 1)
        return start + n
    }

    // Construct a simple loop with two diamonds in it
    def buildBaseLoop(def from) {
        def header = buildStraight(from, 1)
        def diamond1 = buildDiamond(header)
        def d11 = buildStraight(diamond1, 1)
        def diamond2 = buildDiamond(d11)
        def footer = buildStraight(diamond2, 1)
        buildConnect(diamond2, d11)
        buildConnect(diamond1, header)

        buildConnect(footer, from)
        footer = buildStraight(footer, 1)
        return footer
    }

    def getMem() {
        def runtime = Runtime.getRuntime()
        def val = runtime.totalMemory() / 1024
        System.out.println("  Total Memory: " + val + " KB")
    }

    def static void main(String[] args) {
        System.out.println("Welcome to LoopTesterApp, Groovy without @CompileStatic edition")

        System.out.println("Constructing App...")
        def app = new LoopTesterApp()
        app.getMem()

        System.out.println("Constructing Simple CFG...")
        app.cfg.createNode(0)
        app.buildBaseLoop(0)
        app.cfg.createNode(1)
        new BasicBlockEdge(app.cfg, 0, 2)

        System.out.println("15000 dummy loops")
        for (def dummyloop = 0; dummyloop < 15000; dummyloop++) {
            HavlakLoopFinder finder = new HavlakLoopFinder(app.cfg, app.lsg)
            finder.findLoops()
        }

        System.out.println("Constructing CFG...")
        def n = 2

        for (def parlooptrees = 0; parlooptrees < 10; parlooptrees++) {
            app.cfg.createNode(n + 1)
            app.buildConnect(2, n + 1)
            n = n + 1

            for (def i = 0; i < 100; i++) {
                def top = n
                n = app.buildStraight(n, 1)
                for (def j = 0; j < 25; j++) {
                    n = app.buildBaseLoop(n)
                }
                def bottom = app.buildStraight(n, 1)
                app.buildConnect(n, top)
                n = bottom
            }
            app.buildConnect(n, 1)
        }

        app.getMem()
        System.out.format("Performing Loop Recognition\n1 Iteration\n")
        def finder = new HavlakLoopFinder(app.cfg, app.lsg)
        finder.findLoops()
        app.getMem()

        System.out.println("Another 50 iterations...")

        def start = System.currentTimeMillis()
        def maxMemory = Runtime.getRuntime().maxMemory()

        for (def i = 0; i < 50; i++) {
            System.out.println(maxMemory - Runtime.getRuntime().freeMemory())
            def finder2 = new HavlakLoopFinder(app.cfg, new LSG())
            finder2.findLoops()
        }

        System.out.println("\nTime: " + (System.currentTimeMillis() - start) + " ms")

        System.out.println("")
        app.getMem()
        System.out.println("# of loops: " + app.lsg.getNumLoops() +
                " (including 1 artificial root node)")
        System.out.println("# of BBs  : " + BasicBlock.getNumBasicBlocks())
        System.out.println("# max time: " + finder.getMaxMillis())
        System.out.println("# min time: " + finder.getMinMillis())
        app.lsg.calculateNestingLevel()
        //app.lsg.Dump()
    }

}

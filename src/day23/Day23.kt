package day23

import Env
import com.sun.org.apache.bcel.internal.generic.IFEQ
import com.sun.org.apache.xerces.internal.impl.dtd.models.CMNode
import jdk.internal.org.jline.terminal.impl.PosixSysTerminal
import println
import readInput

private class LANParty(env: Env) {
    private val input = if (env == Env.REAL) readInput("day23/Day23") else readInput("day23/Day23_test")

    private val network = buildMap<String,MutableSet<String>> {
        input.map {
            val (c1, c2) = it.split("-").map { it.trim() }
            computeIfAbsent(c1) { kotlin.collections.mutableSetOf() }.add(c2)
            computeIfAbsent(c2) { mutableSetOf() }.add(c1)
        }
    }

    fun connectedCompute_3():Int {
        val set = mutableSetOf<List<String>>()
        for (first in network.keys) {
            for (second in network[first] ?: emptyList()) {
                // second computer chosen should not be same as first
                if (first == second) continue
                for (third in network[second] ?: emptyList()) {
                    // third computer should be connected to first, to form a LAN
                    if (network[third]!!.contains(first)) {
                        if (listOf(first,second,third).any { it.startsWith("t") }){
                            set.add(listOf(first,second,third).sorted())
                        }
                    }
                }
            }
        }
        return set.size
    }

    private fun largestConnectedNetwork(
        computer: String,
        existingNetwork:Set<String>,
        seen: MutableSet<Set<String>>
    ) {
        if (seen.contains(existingNetwork)) return
        seen.add(existingNetwork)
        for (neighbor in network[computer]!!) {
            // next computer should not be from existing network
            if (neighbor in existingNetwork) continue
            // picked up computer should be connected to all computers in existing network
            if (!network[neighbor]!!.containsAll(existingNetwork)) continue
            largestConnectedNetwork(neighbor, existingNetwork + neighbor, seen)
        }
    }

    fun password():String{
        val seen = mutableSetOf(setOf<String>())
        for (node in network.keys) {
            largestConnectedNetwork(
                computer = node,
                existingNetwork = setOf(node),
                seen = seen
            )
        }
        return seen.associate { it.size to it }
            .toSortedMap().lastEntry()
            .value.sorted().joinToString()
    }
}

fun main() {
    val env = Env.REAL
    LANParty(env).connectedCompute_3().println()
    LANParty(env).password().println()
}

package day05

import println
import readInput

fun main() {

    fun part1(input:List<String>):Int {
        val rules = input.takeWhile { it.isNotEmpty() }
        val pages = input.dropWhile { it.isNotEmpty() }.drop(1)

        val ruleOderMap = rules.map { it.split("|").map { it.toInt() } }
            .fold(mutableMapOf<Int, MutableSet<Int>>()) { acc, (x, y) ->
                acc.computeIfAbsent(x) { mutableSetOf() }.add(y)
                acc
            }

        return pages
            .map { it.split(",").map { it.toInt() } }
            .filter {
                val pageToIndex = it.withIndex().associate { it.value to it.index }
                ruleOderMap.all { (x, dependents) ->
                    dependents.all { y ->
                        x !in pageToIndex || y !in pageToIndex || pageToIndex[x]!! < pageToIndex[y]!!
                    }
                }
            }.sumOf { it[it.size / 2] }
    }

    fun part2(input:List<String>):Int {
        val rules = input.takeWhile { it.isNotEmpty() }
        val pages = input.dropWhile { it.isNotEmpty() }.drop(1)

        val ruleOderMap = rules.map { it.split("|").map { it.toInt() } }
            .fold(mutableMapOf<Int, MutableSet<Int>>()) { acc, (x, y) ->
                acc.computeIfAbsent(x) { mutableSetOf() }.add(y)
                acc
            }

        fun ordered(page: List<Int>): Boolean {
            val pageToIdxMap = page.withIndex().associate { it.value to it.index }
            for ((r_before, afterList) in ruleOderMap) {
                for (r_after in afterList) {
                    if (r_before in pageToIdxMap && r_after in pageToIdxMap && pageToIdxMap[r_before]!! >= pageToIdxMap[r_after]!!) {
                        return false
                    }
                }
            }
            return true
        }

        fun topologicalSort(page: List<Int>):List<Int> {
            val graph = mutableMapOf<Int, MutableList<Int>>()
            val inDegree = mutableMapOf<Int, Int>().withDefault { 0 }

            // Build graph from rules and page
            for ((r_before, afterList) in ruleOderMap) {
                for (r_after in afterList) {
                    if (r_before in page && r_after in page) {
                        graph.computeIfAbsent(r_before) { mutableListOf() }.add(r_after)
                        inDegree[r_before] = inDegree.getValue(r_before) // it will always have in-degree of 0
                        inDegree[r_after] = inDegree.getValue(r_after) + 1
                    }
                }
            }

            val queue = ArrayDeque<Int>()
            for (node in page){
                if (inDegree.getValue(node) == 0)
                    queue.add(node) // add all node with in-degree in queue to be processed, they can be take out in any order, while performing topo sort
            }

            val topoSort = mutableListOf<Int>()
            while (queue.isNotEmpty()) {
                val current = queue.removeFirst()
                topoSort.add(current)
                for (neighbor in graph[current] ?: emptyList()) {
                    // since current node is removed, decrease in-degree on all dependent nodes
                    inDegree[neighbor] = inDegree[neighbor]!!-1
                    if (inDegree[neighbor]!! == 0) { // if any node in-degree is 0, add it in queue to be added in topo sort list
                        queue.add(neighbor)
                    }
                }
            }

            return topoSort
        }

        return pages.map {
            val page = it.split(",").map { it.toInt() }
            if (!ordered(page)){
                topologicalSort(page)[page.size/2]
            }else {
                0
            }
        }.sum()
    }

    val input = readInput("day05/Day05")
    part1(input).println()
    part2(input).println()
}

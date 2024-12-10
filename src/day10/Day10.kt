package day10

import println
import readInput
import kotlin.math.abs

data class Coord(val x: Int, val y: Int)

fun Coord.inRange(grid: List<String>) = x in grid.indices && y in grid[0].indices

fun Coord.up() = Coord(x - 1, y)
fun Coord.down() = Coord(x + 1, y)
fun Coord.left() = Coord(x, y - 1)
fun Coord.right() = Coord(x, y + 1)
fun Coord.all() = listOf(up(),down(),left(),right())

class LavaProductionFacility(val input: List<String>) {
    val graph = input.map { it.toCharArray().map { it.digitToIntOrNull() ?: 78 } }
    val startPos = mutableListOf<Coord>()
    val target = 9
    init {
        for (x in graph.indices) {
            for (y in graph[0].indices) {
                if (graph[x][y] == 0) {
                    startPos.add(Coord(x, y))
                }
            }
        }
    }

    private fun score_bfs(startNode: Coord):Int {
        var score = 0
        val queue = ArrayDeque<Coord>()
        val visited = hashSetOf<Coord>()

        queue.addFirst(startNode)
        visited.add(startNode)

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()

            if (graph[current.x][current.y] == target) score++

            val neighbors = current.all().filter { it.inRange(input) }
                .filter { (graph[it.x][it.y] - graph[current.x][current.y]) == 1 }

            for(neighbor in neighbors) {
                if (visited.contains(neighbor)) continue
                visited.add(neighbor)
                queue.add(neighbor)
            }
        }
        return score
    }

    private fun rating_dfs(startNode: Coord):Int{
        val stack = ArrayDeque<Coord>()
        stack.addFirst(startNode)

        var rating = 0

        while (stack.isNotEmpty()) {
            val current = stack.removeLast()
            if (graph[current.x][current.y] == target) rating++

            val neighbors = current.all().filter { it.inRange(input) }
                .filter { (graph[it.x][it.y] - graph[current.x][current.y]) == 1 }

            for(neighbor in neighbors) {
                stack.add(neighbor)
            }
        }

        return rating
    }

    fun score() = startPos.map { score_bfs(it) }.sum()
    fun rating() = startPos.map { rating_dfs(it) }.sum()
}

fun main() {
    fun part1(input: List<String>) =  LavaProductionFacility(input).score()
    fun part2(input: List<String>) = LavaProductionFacility(input).rating()

    val input = readInput("day10/Day10")
    part1(input).println()
    part2(input).println()
}

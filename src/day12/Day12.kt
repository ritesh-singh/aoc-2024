package day12

import Coord
import all
import down
import inRange
import left
import println
import readInput
import right
import up
import java.util.UUID
import kotlin.math.abs


class GardenGroups(val garden: List<String>) {
    private val plantType = mutableListOf<Char>()
    private val allPlants = buildList { garden.forEachIndexed { r, str -> str.forEachIndexed { s, _ -> add(Coord(r, s)) } } }
        .also {
            plantType.addAll(it.map { garden[it.x][it.y] }.distinct())
        }

    private fun regions(): Map<Coord, List<Coord>> {
        val allPlants = mutableListOf(*allPlants.toTypedArray())
        // Plant type to plants type
        val region = hashMapOf<Coord, List<Coord>>()
        val visited = hashSetOf<Coord>()
        while (allPlants.isNotEmpty()) {
            val looking = allPlants.removeFirst()
            val plot = mutableListOf(looking)
            fun traverse_bfs() {
                val queue = ArrayDeque<Coord>().also { it.add(looking) }
                visited.add(looking)
                while (queue.isNotEmpty()) {
                    val cur = queue.removeFirst()
                    val neigh = cur.all().filter { it.x in garden.indices && it.y in garden[0].indices }
                    for (n in neigh) {
                        when {
                            visited.contains(n) -> continue
                            garden[n.x][n.y] == garden[looking.x][looking.y] -> {
                                queue.add(n)
                                visited.add(n)
                                plot.add(n)
                                allPlants.remove(n)
                            }
                        }
                    }
                }
            }
            traverse_bfs()
            region.put(looking, plot)
        }
        return region
    }

    fun totalPricePart1(): Int {
        return regions().map { region ->
            region.value.count() * region.value.map { plot ->
                4 - plot.all().filter { it.x in garden.indices && it.y in garden[0].indices && it in region.value }
                    .count()
            }.sum()
        }.sum()
    }

    enum class Dir {
        L, R, D, U
    }

    fun totalPricePart2(): Int {
//        val garden = garden.map { it.toCharArray() }
//        var lookFor = 'R'
//        val lCoord =  run {
//            for (i in garden.indices)
//                for (j in garden[i].indices)
//                    if (garden[i][j] == lookFor)
//                        return@run Coord(i, j)
//        }
//        val queue = ArrayDeque<Coord>().also { it.addFirst(lCoord) }
//        while (queue.isNotEmpty()) {
//            val cur = queue.removeFirst()
//
//            lookFor = garden[cur.x][cur.y]
//        }
        return 0
    }
}

fun main() {
//    fun part1(input: List<String>): Int = GardenGroups(input).totalPricePart1()
    fun part2(input: List<String>): Int = GardenGroups(input).totalPricePart2()

    val input = readInput("day12/Day12_test")
//    part1(input).println()
    part2(input)
}

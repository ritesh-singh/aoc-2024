package day06

import println
import readInput
import kotlin.math.abs
import kotlin.math.sign

private enum class GuardDir {
    UP,
    DOWN,
    LEFT,
    RIGHT
}

private val OBSTRUCTION = '#'
private val EMPTY = '.'

data class Coord(val x: Int, val y: Int) {
    fun up() = Coord(x - 1, y)
    fun down() = Coord(x + 1, y)
    fun left() = Coord(x, y - 1)
    fun right() = Coord(x, y + 1)
}

private fun rotate90GuardPos(guardDir: GuardDir): GuardDir {
    return when (guardDir) {
        GuardDir.UP -> GuardDir.RIGHT
        GuardDir.RIGHT -> GuardDir.DOWN
        GuardDir.DOWN -> GuardDir.LEFT
        GuardDir.LEFT -> GuardDir.UP
    }
}

private data class Guard(val pos: GuardDir, val coord: Coord)

private data class Area(val input: List<String>) {
    val graph = input.map { it.toCharArray() }
    lateinit var guard: Guard
        private set

    init {
        run loop@{
            input.forEachIndexed { r, str ->
                str.forEachIndexed { c, char ->
                    when (char) {
                        '^' -> {
                            guard = Guard(pos = GuardDir.UP, coord = Coord(r, c))
                            return@loop
                        }

                        'v' -> {
                            guard = Guard(pos = GuardDir.DOWN, coord = Coord(r, c))
                            return@loop
                        }

                        '<' -> {
                            guard = Guard(pos = GuardDir.LEFT, coord = Coord(r, c))
                            return@loop
                        }

                        '>' -> {
                            guard = Guard(pos = GuardDir.RIGHT, coord = Coord(r, c))
                            return@loop
                        }
                    }
                }
            }
        }
    }

    private val inRange: (Coord) -> Boolean = { coord -> coord.x in graph.indices && coord.y in graph[0].indices }
    private fun Coord.inRange() = inRange(this)

    fun findAllVistingPosition(): Set<Coord> {
        val visited = mutableSetOf<Coord>()

        var curDir = guard.pos
        var curCoord = guard.coord

        while (true) {
            visited.add(curCoord)
            val nextCoord = when (curDir) {
                GuardDir.UP -> curCoord.up()
                GuardDir.DOWN -> curCoord.down()
                GuardDir.LEFT -> curCoord.left()
                GuardDir.RIGHT -> curCoord.right()
            }

            if (!nextCoord.inRange()) break

            if (graph[nextCoord.x][nextCoord.y] == OBSTRUCTION) {
                curDir = rotate90GuardPos(curDir)
            } else {
                curCoord = nextCoord
            }
        }
        return visited
    }

    private fun isCycle(): Boolean {
        val visited = mutableSetOf<Pair<Coord, GuardDir>>()
        var curDir = guard.pos
        var curCoord = guard.coord

        while (true) {
            visited.add(Pair(curCoord, curDir))
            val nextCoord = when (curDir) {
                GuardDir.UP -> curCoord.up()
                GuardDir.DOWN -> curCoord.down()
                GuardDir.LEFT -> curCoord.left()
                GuardDir.RIGHT -> curCoord.right()
            }

            if (!nextCoord.inRange()) break

            if (graph[nextCoord.x][nextCoord.y] == OBSTRUCTION) {
                curDir = rotate90GuardPos(curDir)
            } else {
                curCoord = nextCoord
                if (visited.contains(Pair(curCoord, curDir))) {
                    return true
                }
            }
        }

        return false
    }

    fun loops(): Int {
        val blockers = findAllVistingPosition()
        var loops = 0
        blockers.forEach {
            if (graph[it.x][it.y] == EMPTY) {
                graph[it.x][it.y] = OBSTRUCTION
                if (isCycle()) {
                    loops++
                }
            }
            graph[it.x][it.y] = EMPTY
        }
        return loops
    }
}

fun main() {

    fun part1(input: List<String>): Int = Area(input).findAllVistingPosition().size

    fun part2(input: List<String>): Int =  Area(input).loops()

    val test = readInput("day06/Day06")
    part1(test).println()
    part2(test).println()
}

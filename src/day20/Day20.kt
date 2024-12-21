package day20

import println
import readInput
import kotlin.math.abs

import CoordRC
import Env
import all
import inRange
import println
import readInput
import sun.security.krb5.internal.crypto.Des
import java.beans.DesignMode
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.collections.List
import kotlin.math.abs
import kotlin.math.sign
import kotlin.time.measureTime

private class RaceCondition(private val env: Env = Env.TEST) {
    private val input = (if (env == Env.REAL) readInput("day20/Day20") else readInput("day20/Day20_test"))
        .map { it.toCharArray() }
    private val inRange: (CoordRC) -> Boolean = { coord -> coord.row in input.indices && coord.col in input[0].indices }

    lateinit var start: CoordRC
    lateinit var end: CoordRC

    init {
        input.forEachIndexed { row, s ->
            s.withIndex().forEach { (col, c) ->
                if (c == 'S') start = CoordRC(row, col)
                if (c == 'E') end = CoordRC(row, col)
            }
        }
    }

    private fun CoordRC.manhattan(other: CoordRC): Int = abs(row - other.row) + abs(col - other.col)

    private fun path(): List<CoordRC> {
        val result = mutableListOf<List<CoordRC>>()
        val queue = ArrayDeque<List<CoordRC>>()
        queue.add(mutableListOf(start))
        while (queue.isNotEmpty()) {
            val path = queue.removeFirst()
            val last = path.last()
            if (last == end)
                result.add(path)
            last.all().filter { inRange(it) && input[it.row][it.col] != '#' }
                .forEach { neighbor ->
                    if (!path.contains(neighbor)) {
                        queue.add(path + neighbor)
                    }
                }
        }
        check(result.size == 1)
        return result.last()
    }

    fun atleast_100_picoseconds(cheat:Int): Int {
        val path = path()
        return path.indices.map { index ->
            (100 + index..path.lastIndex).count { next ->
                val dist =  path[index].manhattan(path[next])
                dist <= cheat && dist <= next - index - 100
            }
        }.sum()
    }
}

fun main() {
    val env = Env.REAL
    RaceCondition(env).atleast_100_picoseconds(cheat = 2).println()
    RaceCondition(env).atleast_100_picoseconds(cheat = 20).println()
}

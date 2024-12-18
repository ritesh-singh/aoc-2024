package day18

import Coord
import Env
import all
import inRange
import jdk.incubator.vector.VectorOperators.Test
import println
import readInput
import kotlin.math.abs

private class RAMRun(private val env: Env = Env.TEST, private val part1:Boolean) {
    private val input = if (env == Env.REAL) readInput("day18/Day18") else readInput("day18/Day18_test")

    val xRange = 0..if (env == Env.TEST) 6 else 70
    val yRange = 0..if (env == Env.TEST) 6 else 70

    val start = Coord(xRange.start,yRange.start)
    val end = Coord(xRange.last,yRange.last)

    private val memorySpace = input.map { it.split(",").let { Coord(x = it[0].toInt(), y = it[1].toInt()) } }

    private fun steps(corrupted: List<Coord>): Int {
        val queue = ArrayDeque<Pair<Coord, Int>>()
        val visited = ArrayDeque<Coord>()
        queue.add(Pair(start, 0))
        visited.add(start)
        while (queue.isNotEmpty()) {
            val (coord, dist) = queue.removeFirst()
            if (coord == end) return dist
            for (next in coord.all().filter { it.x in xRange && it.y in yRange && it !in corrupted }) {
                if (visited.contains(next)) continue
                queue.add(Pair(next, dist + 1))
                visited.add(next)
            }
        }
        throw RuntimeException("No path")
    }

    fun minSteps():Int{
        val corrupted = when {
            part1 -> memorySpace.take(if (env == Env.TEST) 12 else 1024)
            else -> memorySpace
        }
        return steps(corrupted)
    }

    fun blockigByte():String {
        var next = if (env == Env.TEST) 12+1 else 1024 + 1
        while (true){
            try {
                steps(memorySpace.take(next++))
            }catch (e: Exception){
                break
            }
        }
        return memorySpace.take(next-1).last().let {
            "${it.x},${it.y}"
        }
    }
}

fun main() {
    val env = Env.REAL
    RAMRun(env = env, part1 = true).minSteps().println()
    RAMRun(env = env,part1 = false).blockigByte().println()
}

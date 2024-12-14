package day14

import Coord
import Env
import println
import readInput
import kotlin.math.E
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.sign


private class RestroomRedoubt(env: Env = Env.TEST) {
    private val input = if (env == Env.REAL) readInput("day14/Day14") else readInput("day14/Day14_test")

    private val xMax = if (env == Env.TEST) 11 else 101
    private val yMax = if (env == Env.TEST) 7 else 103
    private val seconds = 100

    private data class Robot(val coord: Coord, val velocity: Velocity)
    private data class Velocity(val id: Int, val coord: Coord)

    private fun Coord.moveH(velocity: Coord): Coord {
        return Coord(
            x = (if (x + velocity.x < 0) xMax - (x + velocity.x).absoluteValue else x + velocity.x) % xMax,
            y = y
        )
    }

    private fun Coord.moveV(velocity: Coord): Coord {
        return Coord(
            x = x,
            y = (if (y + velocity.y < 0) yMax - (y + velocity.y).absoluteValue else y + velocity.y) % yMax
        )
    }

    private var robots = buildList {
        input.forEach {
            var id = 0
            it.split(" ").let {
                add(
                    Robot(
                        coord = it[0].substringAfter("=").trim().split(",")
                            .let { Coord(it[0].toInt(), it[1].toInt()) },
                        velocity = Velocity(
                            id = id++,
                            coord = it[1].substringAfter("v=").trim().split(",")
                                .let { Coord(it[0].toInt(), it[1].toInt()) }
                        )
                    ))
            }
        }
    }.toMutableList()

    fun safetyFactor(): Int {
        val map = hashMapOf<Coord, MutableSet<Velocity>>()
        repeat(seconds) {
            robots.forEach { robot ->
                var robot = robot
                val (coord, velcoty) = robot.coord to robot.velocity
                val newPos = coord.moveH(velcoty.coord).moveV(velcoty.coord)
                robot = robot.copy(coord = newPos)
                if (map.contains(robot.coord)) {
                    map[robot.coord]!!.add(robot.velocity)
                } else {
                    map[robot.coord] = mutableSetOf(robot.velocity)
                }
            }
            robots.clear()
            robots.addAll(map.map { a -> a.value.map { Robot(coord = a.key, velocity = it) } }.flatten())
            map.clear()
        }
        return listOf(
            Pair(0..xMax / 2 - 1, 0..yMax / 2 - 1),
            Pair(xMax / 2 + 1..xMax - 1, 0..yMax / 2 - 1),
            Pair(0..xMax / 2 - 1, yMax / 2 + 1..yMax - 1),
            Pair(xMax / 2 + 1..xMax - 1, yMax / 2 + 1..yMax - 1)
        ).map {
            robots.map { it.coord }.filter { coord -> coord.x in it.first && coord.y in it.second }.count()
        }.fold(1) { acc: Int, i: Int -> acc * i }
    }

    fun easterEgg() {
        fun printTree(map: Map<Coord, Set<Velocity>>) {
            for (i in 0..xMax) {
                for (j in 0..yMax) {
                    if (map.contains(Coord(i, j))) {
                        print("#")
                    } else print(".")
                }
                println("")
            }
        }

        /**
         * I ended up printing the tree and searching for ############# in console logs, to check for tree pattern
         */
        val map = hashMapOf<Coord, MutableSet<Velocity>>()
        var repeat = 10000
        var seconds = 0
        while (repeat-- > 0) {
            robots.forEach { robot ->
                var robot = robot
                val (coord, velcoty) = robot.coord to robot.velocity
                val newPos = coord.moveH(velcoty.coord).moveV(velcoty.coord)
                robot = robot.copy(coord = newPos)
                if (map.contains(robot.coord)) {
                    map[robot.coord]!!.add(robot.velocity)
                } else {
                    map[robot.coord] = mutableSetOf(robot.velocity)
                }
            }
            robots.clear()
            robots.addAll(map.map { a -> a.value.map { Robot(coord = a.key, velocity = it) } }.flatten())
            ++seconds
            // this check is print tree with consecuytive x coords
            if (robots.map { it.coord }.groupBy { it.x }.any { it.value.zipWithNext { a, b ->  abs(a.x-b.x)==1 }.count() > 20 }){
                println(seconds)
                printTree(map)
            }
            map.clear()
        }
    }
}

fun main() {
    fun part1(): Int = RestroomRedoubt(env = Env.REAL).safetyFactor()
    fun part2() = RestroomRedoubt(env = Env.REAL).easterEgg()
    part1().println()
    part2()
}
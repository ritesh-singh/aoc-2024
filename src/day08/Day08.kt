package day08

import println
import readInput
import kotlin.math.abs

fun main() {

    data class Point(val x: Int, val y: Int)

    // [1,2,3] => [(1,2),(1,3),(2,3)]
    fun <T, R> Iterable<T>.zipWithAll(transform: (T, T) -> R): List<R> =
        flatMapIndexed { i, a -> drop(i + 1).map { b -> transform(a, b) } }

    val inRange: List<String>.(Point) -> Boolean = { point -> point.x in this.indices && point.y in this[0].indices }

    val parseInput:Map<Char,MutableSet<Point>> = readInput("day08/Day08").let {
        buildMap {
            it.forEachIndexed { row, s ->
                s.forEachIndexed { col, c ->
                    if (c.isLetterOrDigit())
                        this.computeIfAbsent(c) { mutableSetOf() }.add(Point(row, col))
                }
            }
        }
    }

    fun part1(input: List<String>): Set<Point> {
        return buildSet {
            parseInput.values.forEach { points ->
                points.zipWithAll { f, s ->
                    val diffX = abs(f.x - s.x)
                    val diffY = abs(f.y - s.y)
                    when {
                        f.x < s.x && f.y < s.y -> {
                            listOf(
                                Point(f.x - diffX, f.y - diffY),
                                Point(s.x + diffX, s.y + diffY)
                            )
                        }
                        else -> {
                            listOf(
                                Point(s.x + diffX, s.y - diffY),
                                Point(f.x - diffX, f.y + diffY)
                            )
                        }
                    }.filter { input.inRange(it) }.forEach { add(it) }
                }
            }
        }
    }

    fun part2(input: List<String>): Set<Point> {
        return buildSet {
            parseInput.values.forEach { points ->
                points.zipWithAll { f, s ->
                    addAll(listOf(f, s))

                    val diffX = abs(f.x - s.x)
                    val diffY = abs(f.y - s.y)

                    fun generateAntinodes(antiNode: Point, dx: Int, dy: Int) {
                        var cAntiNode = antiNode
                        while (true) {
                            val next = Point(cAntiNode.x + dx, cAntiNode.y + dy)
                            if (!input.inRange(next)) break
                            this.add(next)
                            cAntiNode = next
                        }
                    }

                    when {
                        f.x < s.x && f.y < s.y -> {
                            generateAntinodes(s, diffX, diffY)
                            generateAntinodes(f, -diffX, -diffY)
                        }
                        f.x < s.x && f.y > s.y -> {
                            generateAntinodes(f, -diffX, diffY)
                            generateAntinodes(s, diffX, -diffY)
                        }
                    }
                }
            }
        }
    }

    val input = readInput("day08/Day08")
    part1(input).size.println()
    part2(input).toMutableSet().also {
        it.addAll(part1(input))
    }.size.println()
}

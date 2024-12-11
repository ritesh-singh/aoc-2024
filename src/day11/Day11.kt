package day11

import println
import readInput
import readText
import kotlin.math.abs

fun stones(stones: List<Long>, blink: Int): Long {
    return stones.groupingBy { it }.eachCount()
        .mapValues { it.value.toLong() }
        .let {
            var old = it
            repeat(blink) { _->
                old = old.flatMap { (stone,count) ->
                    when {
                        stone == 0L -> listOf(1L to count)
                        stone.toString().length % 2 == 0 -> {
                            stone.toString().chunked(stone.toString().length / 2)
                                .let { listOf(it.first().toLong() to count, it.last().toLong() to count)  }
                        }
                        else -> listOf(2024 * stone to count)
                    }
                }.groupingBy { it.first }.fold(0L) { acc, pair -> acc + pair.second }
            }
            old
        }.values.sum()
}


fun main() {
    fun part1(input: String): Long = stones(input.split(" ").map { it.toLong() }, 25)
    fun part2(input: String): Long = stones(input.split(" ").map { it.toLong() }, 75)

    val input = readText("day11/Day11")
    part1(input).println()
    part2(input).println()
}

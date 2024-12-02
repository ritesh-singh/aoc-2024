package day02

import println
import readInput

fun main() {

    val isIncreasing:(List<Int>) -> Boolean = { it.zipWithNext().all { (a, b) -> (b - a) in 1..3 } }
    val isDecreasing:(List<Int>) -> Boolean = { it.zipWithNext().all { (a, b) -> (a - b) in 1..3 }}

    fun part1(input: List<String>) =
        input.map { it.split(" ").map { it.trim().toInt() } }.count { isIncreasing(it) || isDecreasing(it) }

    fun part2(input: List<String>): Long {
        return input.map {
            it.split(" ").map { it.trim().toInt() }
        }.sumOf { list ->
            list.indices.any { index -> isIncreasing(list.toMutableList().apply { removeAt(index) }) || isDecreasing(list.toMutableList().apply { removeAt(index) }) }
                .let { if (it) 1L else 0L }
        }
    }

    val inputt = readInput("day02/Day02")
    part1(inputt).println()
    part2(inputt).println()
}

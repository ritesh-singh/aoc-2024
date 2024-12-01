package day01

import println
import readInput
import kotlin.math.abs

fun main() {
    fun part1(input: List<String>): Int {
        val left = input.map { it.substringBefore(" ").toInt() }.sorted()
        val right  = input.map { it.substringAfter(" ").trim().toInt() }.sorted()
        return left.zip(right).sumOf { abs(it.first - it.second) }
    }

    fun part2(input: List<String>): Int {
        val left = input.map { it.substringBefore(" ").toInt() }
        val right  = input.map { it.substringAfter(" ").trim().toInt() }
        return left.sumOf { l -> right.count { it == l } * l }
    }

    val input = readInput("day01/Day01")
    part1(input).println()
    part2(input).println()
}

package day03

import println
import readInput
import readText

fun main() {

    fun part1(input: String):Long {
        return input.let {
            Regex("""mul\((\d{1,3}),(\d{1,3})\)""").findAll(it)
        }.map {
            it.destructured
        }.sumOf {
            it.component1().toLong() * it.component2().toLong()
        }
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    val input = readText("day03/Day03")
    part1(input).println()
//    part2(input).println()
}

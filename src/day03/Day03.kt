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

    fun part2(input: String): Long {
        var isEnabled = true
        return input.let {
            Regex("""do\(\)|don't\(\)|mul\(\d{1,3},\d{1,3}\)""").findAll(it)
        }.filter {
            when {
                it.value.startsWith("don") -> isEnabled = false
                it.value.startsWith("do") -> isEnabled = true
            }
            isEnabled
        }.filter {
            it.value.startsWith("mul")
        }.map {
            it.value.substringAfter("(").substringBefore(")").trim().split(",").map { it.toLong() }
                .reduce { acc, l ->  acc * l }
        }.sum()
    }

    val input = readText("day03/Day03")
    part1(input).println()
    part2(input).println()
}

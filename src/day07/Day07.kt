package day07

import println
import readInput
import kotlin.math.abs

fun main() {

    fun part1(input: List<String>): Long {
        return input.map {
            it.split(":").map { it.trim() }
        }.associate {
            it[0].toLong() to it[1].split(" ").map { it.toLong() }
        }.filter {(target, numbers) ->
            var canMatch = false
            run loop@ {
                val combs = 1 shl (numbers.size-1) // 2^(n-1) total combinations of ops
                for (c in 0 until combs) {
                    var result = numbers[0]
                    var cIdx = 0
                    for (i in 1 until numbers.size) {
                        val op = if ((c shr cIdx) and 1 == 0) "+" else "*"
                        result = if (op == "+") result + numbers[i] else result * numbers[i]
                        cIdx++
                    }
                    if(result == target) {
                        canMatch = true
                        return@loop
                    }
                }
            }
            canMatch
        }.keys.sum()
    }

    fun part2(input:List<String>):Long {
        return input.map {
            it.split(":").map { it.trim() }
        }.associate {
            it[0].toLong() to it[1].split(" ").map { it.toLong() }
        }.filter {(target, numbers) ->
            var canMatch = false
            run loop@ {
                val combinations = Math.pow(3.0, (numbers.size - 1).toDouble()).toInt() // // 3^(n-1) total combinations of ops
                for (c in 0 until combinations) {
                    var result = numbers[0]
                    var tComb = c
                    for (i in 1 until numbers.size) {
                        val operator = when (tComb % 3) {
                            0 -> "+"
                            1 -> "*"
                            2 -> "||"
                            else -> throw IllegalStateException()
                        }
                        tComb /= 3

                        result = when (operator) {
                            "+" -> result + numbers[i]
                            "*" -> result * numbers[i]
                            "||" -> "$result${numbers[i]}".toLong()
                            else -> throw IllegalStateException()
                        }
                    }
                    if (result == target) {
                        canMatch = true
                        return@loop
                    }
                }
            }
            canMatch
        }.keys.sum()
    }

    val input = readInput("day07/Day07")
    part1(input).println()
    part2(input).println()
}

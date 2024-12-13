package day13

import println
import readInput
import readText
import javax.crypto.Mac
import kotlin.math.min

private enum class Env {
    TEST, REAL
}

private class ClawContraption(env: Env = Env.TEST) {
    private val input = if (env == Env.REAL) readText("day13/Day13") else readText("day13/Day13_test")

    private data class Button(val x: Int, val y: Int, val coin: Int)
    private data class Prize(val x: Int, val y: Int)

    private data class Machine(val buttonA: Button, val buttonB: Button)

    private val map = hashMapOf<Machine, Prize>()

    init {
        input.split("\n\n").map {
            val (a, b, p) = it.split("\n")
            map[
                Machine(
                    buttonA = Button(
                        x = a.substringAfter("X+").substringBefore(",").trim().toInt(),
                        y = a.substringAfter("Y+").trim().toInt(),
                        coin = 3
                    ),
                    buttonB = Button(
                        x = b.substringAfter("X+").substringBefore(",").trim().toInt(),
                        y = b.substringAfter("Y+").trim().toInt(),
                        coin = 1
                    )
                )
            ] = Prize(
                x = p.substringAfter("X=").substringBefore(",").trim().toInt(),
                y = p.substringAfter("Y=").trim().toInt()
            )
        }
    }

    fun smallestToken_100_iteration(): Int {
        return map.map {
            var minToken = Int.MAX_VALUE
            val (buttonA, buttonB) = it.key
            val (prizeX, prizeY) = listOf(it.value.x, it.value.y)
            for (i in 1..100) {
                for (j in 1..100) {
                    if (buttonA.x * i + buttonB.x * j == prizeX && buttonA.y * i + buttonB.y * j == prizeY)
                        minToken = min(minToken, i * buttonA.coin + j * buttonB.coin)
                }
            }
            if (minToken == Int.MAX_VALUE) 0 else minToken
        }.sum()
    }

    fun smallestTokenPart2(): Long {
        return map.map {
            val (buttonA, buttonB) = it.key
            val (ax, ay) = listOf(buttonA.x, buttonA.y)
            val (bx, by) = listOf(buttonB.x, buttonB.y)
            val (px, py) = listOf(it.value.x + 10000000000000, it.value.y + 10000000000000)
            var tokens = 0L
            val bACount = ((px * by) - (py * bx)) / ((ax * by) - (ay * bx))
            val bBCount = ((py * ax) - (px * ay)) / ((ax * by) - (ay * bx))
            if ((ax * bACount + bx * bBCount) == px && (ay * bACount + by * bBCount) == py)
                tokens = bACount * buttonA.coin + bBCount * buttonB.coin
            tokens
        }.sum()
    }
}

fun main() {
    fun part1() = ClawContraption(env = Env.REAL).smallestToken_100_iteration()
    part1().println()
    fun part2() = ClawContraption(env = Env.REAL).smallestTokenPart2()
    part2().println()
}
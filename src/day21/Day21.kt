package day21

import println
import readInput
import kotlin.math.abs

import CoordRC
import Env
import all
import com.github.shiguruikai.combinatoricskt.CartesianProductGenerator
import com.github.shiguruikai.combinatoricskt.Combinatorics
import down
import inRange
import left
import println
import readInput
import right
import up
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.collections.List
import kotlin.math.abs
import kotlin.math.sign
import kotlin.time.measureTime


private const val LEFT = '<'
private const val RIGHT = '>'
private const val DOWN = 'v'
private const val UP = '^'
private const val ACTIVATE = 'A'

private class KeypadConundrum(private val env: Env = Env.TEST) {
    private val input = (if (env == Env.REAL) readInput("day21/Day21") else readInput("day21/Day21_test"))

    private val numbericKeypad = listOf(
        listOf('7', '8', '9'),
        listOf('4', '5', '6'),
        listOf('1', '2', '3'),
        listOf('#', '0', 'A'),
    )

    private val numbericKeypadMap = hashMapOf<Char, CoordRC>().also {
        it.put('7', CoordRC(0, 0))
        it.put('8', CoordRC(0, 1))
        it.put('9', CoordRC(0, 2))
        it.put('4', CoordRC(1, 0))
        it.put('5', CoordRC(1, 1))
        it.put('6', CoordRC(1, 2))
        it.put('1', CoordRC(2, 0))
        it.put('2', CoordRC(2, 1))
        it.put('3', CoordRC(2, 2))
        it.put('0', CoordRC(3, 1))
        it.put('A', CoordRC(3, 2))
    }

    private val directionalKeypad = listOf(
        listOf('#', '^', 'A'),
        listOf('<', 'v', '>'),
    )

    private val directionalKeypadMap = hashMapOf<Char, CoordRC>().also {
        it.put('^', CoordRC(0, 1))
        it.put('A', CoordRC(0, 2))
        it.put('<', CoordRC(1, 0))
        it.put('v', CoordRC(1, 1))
        it.put('>', CoordRC(1, 2))
    }

    private fun CoordRC.canMove(map: List<List<Char>>): Boolean =
        this.row in map.indices && this.col in map[0].indices && map[this.row][this.col] != '#'

    private enum class Direction { L, R, U, D }

    private fun String.getNumericCode() = this.trim().dropLast(1).toLong()

    private fun CoordRC.direction(other: CoordRC): Direction {
        return when {
            other.col > this.col -> Direction.R
            other.col < this.col -> Direction.L
            other.row > this.row -> Direction.D
            other.row < this.row -> Direction.U
            else -> throw IllegalStateException()
        }
    }

    private fun allShortestPaths(map: List<List<Char>>, src: CoordRC, dst: CoordRC): List<List<CoordRC>> {
        val queue = ArrayDeque<List<CoordRC>>()
        val sPaths = mutableListOf<List<CoordRC>>()
        var sLenght = Int.MAX_VALUE
        queue.add(listOf(src))
        while (queue.isNotEmpty()) {
            val path = queue.removeFirst()
            val cN = path.last()
            if (path.size > sLenght) continue
            if (cN == dst) {
                if (path.size < sLenght) {
                    sLenght = path.size
                    sPaths.clear()
                }
                sPaths.add(path)
            } else {
                cN.all().filter { it.canMove(map) }.forEach { neighbor ->
                    if (!path.contains(neighbor))
                        queue.add(path + neighbor)
                }
            }
        }
        return sPaths
    }

    private fun List<Direction>.path(): String {
        return buildString {
            this@path.forEach {
                when (it) {
                    Direction.L -> append(LEFT)
                    Direction.R -> append(RIGHT)
                    Direction.U -> append(UP)
                    Direction.D -> append(DOWN)
                }
            }
        }
    }

    private fun keyPadAllMoves(sequence: String, keyPad:Boolean): List<String> {
        val combs = mutableListOf<List<String>>()
        "A${sequence}".zipWithNext().forEach { pair ->
            val all = allShortestPaths(
                src = if (keyPad) numbericKeypadMap[pair.first]!! else directionalKeypadMap[pair.first]!!,
                dst = if (keyPad) numbericKeypadMap[pair.second]!! else directionalKeypadMap[pair.second]!!,
                map = if (keyPad) numbericKeypad else directionalKeypad
            )
            val list = mutableListOf<String>()
            if (all.isNotEmpty()) {
                all.let {
                    it.forEach {
                        it.zipWithNext().map {
                            it.first.direction(it.second)
                        }.also { list.add(it.path()+ ACTIVATE) }
                    }
                }
            } else {
                list.add(ACTIVATE.toString())
            }
            combs.add(list)
        }
        return Combinatorics.cartesianProduct(*combs.toTypedArray()).toList()
            .map { it.joinToString("") }.toList()
    }

    private fun complexities(doorCode: String, nRobots:Int): Long {
        return keyPadAllMoves(doorCode, keyPad = true).let {
            var robotMove = it
            repeat(nRobots) {
                robotMove = robotMove.map { keyPadAllMoves(it,keyPad = false) }.flatten()
            }
            robotMove
        }.minOf { it.length.toLong() }.let { it * doorCode.getNumericCode() }
    }

    fun complexitiesSum() = input.map { complexities(it, 2) }.sum()
}

fun main() {
    val env = Env.REAL
    measureTime {
        KeypadConundrum(env).complexitiesSum().println()
    }.println()
}

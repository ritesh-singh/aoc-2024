package day25

import Env
import com.github.shiguruikai.combinatoricskt.Combinatorics
import println
import readInput
import readText
import java.io.File
import kotlin.math.sign

private class CodeChronicle(env: Env) {
    private val input = if (env == Env.REAL) readText("day25/Day25") else readText("day25/Day25_test")

    private val locksColCount = mutableListOf<String>()
    private val keysColCount = mutableListOf<String>()

    private var rows: Int = 0
    private var cols: Int = 0

    init {
        input.split("\n\n").map {
            it.split("\n").let {
                rows = it.size
                cols = it[0].length
                if (it[0].all { it == '#' }) { // lock
                    buildString {
                        it[0].forEachIndexed { index, _ ->
                            it.map { it[index] }.count { it == '#' }.also { append(it) }
                        }
                    }.let { locksColCount.add(it) }
                } else {
                    buildString {
                        it[0].forEachIndexed { index, _ ->
                            it.map { it[index] }.count { it == '#' }.also { append(it) }
                        }
                    }.let { keysColCount.add(it) }
                }
            }
        }
    }

    fun uniqueKeyLockPair(): Int {
        return Combinatorics.cartesianProduct(locksColCount, keysColCount)
            .count {
                it[0].zip(it[1]).all {
                    (it.first.digitToInt() + it.second.digitToInt()) <= 7
                }
            }
    }
}

fun main() {
    val env = Env.REAL
    CodeChronicle(env).uniqueKeyLockPair().println()
}

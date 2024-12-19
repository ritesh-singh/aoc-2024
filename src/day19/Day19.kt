package day19

import Env
import println
import readInput
import sun.security.krb5.internal.crypto.Des
import java.beans.DesignMode
import kotlin.math.abs

private class LinenLayout(private val env: Env = Env.TEST) {
    private val input = if (env == Env.REAL) readInput("day19/Day19") else readInput("day19/Day19_test")

    private val patterns = input.take(1)[0].split(",").map { it.trim() }
    private val designs = input.drop(2)

    private fun String.exists() = patterns.find { it == this } != null

    private fun ways(design: String, seen:MutableMap<String,Long>): Long {
        if (design.isEmpty()) return 1
        if (seen.containsKey(design)) return seen[design]!!
        var ways = 0L
        for (pattern in patterns) {
            if (design.startsWith(pattern))
                ways += ways(design.removePrefix(pattern),seen)
        }
        seen[design] = ways
        return ways
    }

    fun possibleDesigns() = designs.map { if (ways(it, mutableMapOf()) > 0) 1 else 0 }.sum()
    fun ways() =  designs.map { ways(it, mutableMapOf()) }.sum()
}


fun main() {
    val env = Env.REAL
    LinenLayout(env).possibleDesigns().println()
    LinenLayout(env).ways().println()
}

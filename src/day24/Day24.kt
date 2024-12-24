package day24

import Env
import println
import readInput

private class CrossedWires(env: Env) {
    private val input = if (env == Env.REAL) readInput("day24/Day24") else readInput("day24/Day24_test")

    private val values = buildMap<String, Int> {
        input.takeWhile { it.isNotEmpty() }
            .forEach {
                it.split(":").map { it.trim() }
                    .let { put(it[0], it[1].toInt()) }
            }
    }

    val connections = buildMap<String, Triple<String, String, String>> {
        input.takeLastWhile { it.isNotEmpty() }
            .forEach {
                it.split("->").map { it.trim() }
                    .let {
                        val l = it.first().split(" ").map { it.trim() }
                        put(
                            it.last().trim(),
                            Triple(
                                "${this@CrossedWires.values[l[0]] ?: l[0]}",
                                l[1],
                                "${this@CrossedWires.values[l[2]] ?: l[2]}"
                            )
                        )
                    }
            }
    }

    private fun Triple<Long, String, Long>.output(): Long {
        return when (second) {
            "AND" -> first.and(third)
            "XOR" -> first.xor(third)
            "OR" -> first.or(third)
            else -> throw IllegalArgumentException("Invalid second $second")
        }
    }

    fun decimalNumber(): Long {
        val allOutputs = connections.filter { it.key.startsWith("z") }
            .toSortedMap(compareBy { it.drop(1) }).reversed()

        fun signal(wire: String): Long {
            if (wire.toIntOrNull() != null) return wire.toLong()
            return Triple(
                first = signal(connections[wire]!!.first),
                second = connections[wire]!!.second,
                third = signal(connections[wire]!!.third)
            ).output()
        }

        return allOutputs.map {
            val result = StringBuilder()
            result.append(
                Triple(
                    first = signal(it.value.first),
                    second = it.value.second,
                    third = signal(it.value.third),
                ).output()
            )
            result
        }.joinToString("").toLong(2)
    }
}

fun main() {
    val env = Env.REAL
    CrossedWires(env).decimalNumber().println()
}

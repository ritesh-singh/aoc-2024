package day24

import Env
import com.sun.org.apache.xerces.internal.impl.dv.xs.IntegerDV
import println
import readInput
import java.io.File

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
                                l[0],
                                l[1],
                                l[2]
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

    private val values_map = hashMapOf<String, Long>()

    private var currentZ:String = ""

    fun decimalNumber(): Long {
        val allOutputs = connections.filter { it.key.startsWith("z") }
            .toSortedMap(compareBy { it.drop(1) }).reversed()

        fun signal(wire: String): Long {
            if (wire.toLongOrNull() != null) return wire.toLong()
            if (values[wire] != null) return values[wire]!!.toLong()
            return Triple(
                first = signal(connections[wire]!!.first).also {
                    values_map[connections[wire]!!.first] = it
                },
                second = connections[wire]!!.second,
                third = signal(connections[wire]!!.third).also {
                    values_map[connections[wire]!!.third] = it
                }
            ).output()
        }

        return allOutputs.map { entry ->
            val result = StringBuilder()
            val value = Triple(
                first = signal(entry.value.first).also {
                    values_map[entry.value.first] = it
                },
                second = entry.value.second,
                third = signal(entry.value.third).also {
                    values_map[entry.value.third] = it
                },
            )
            values_map[entry.key] = value.output()
            result.append(value.output())
            result
        }.joinToString("").let {
            currentZ = it
            it
        }.toLong(2)
    }

    fun part2():String{
        decimalNumber()
        val dot = StringBuilder()
        dot.append("digraph G {\n")
        dot.append("node [shape=circle, width=1];\n")
        dot.append("ranksep=0.5;\n")
        dot.append("nodesep=1;\n")
        fun dotFile(){
            connections.forEach {
                dot.append(
                    """
                        ${it.value.first}->${it.key}[taillabel="${values_map[it.value.first]}${it.value.second}"]
                    """.trimIndent()
                )
                dot.append("\n")
                dot.append(
                    """
                        ${it.value.third}->${it.key}[taillabel="${values_map[it.value.third]}${it.value.second}"]
                    """.trimIndent()
                )
                dot.append("\n")
            }
            dot.append("}")
        }
        fun printX_Y_output(){
            val resultX = StringBuilder()
            values.filter { it.key.startsWith("x") }
                .toSortedMap(compareBy { it.drop(1) }).reversed()
                .forEach {
                    resultX.append(it.value)
                }
            val resultY = StringBuilder()
            values.filter { it.key.startsWith("y") }
                .toSortedMap(compareBy { it.drop(1) }).reversed()
                .forEach {
                    resultY.append(it.value)
                }

            val sum = java.lang.Long.parseLong(resultX.toString(),2) + java.lang.Long.parseLong(resultY.toString(),2)
            println("${java.lang.Long.toString(sum,2)}  Expected Result")
            println("$currentZ  Current Result")
            println("===================================================")
            println(" ")
        }
        dotFile()
        File("test.dot").writeText(dot.toString())
        printX_Y_output()

        return "gbf,hdt,jgt,mht,nbf,z05,z09,z30"
    }
}

fun main() {
    val env = Env.REAL
    CrossedWires(env).decimalNumber().println()
    CrossedWires(env).part2().println()
}

/**
 * 10111011111011_10_01011010100001_0_100_0110111_00110  Expected Result
 * 10111011111011_01_01011010100001_1_100_1001000_00110  Current Result
 *       32-45           16-29       12-14          0-4
 *
 *       hdt and z05
 *  1011101111101110010110101000010100011_011100110  Expected Result
 *  1011101111101101010110101000011100100_011100110  Current Result
 *                                          0-8
 *
 *    gbf and z09
 * 1011101111101110010110101000010_100011011100110  Expected Result
 * 1011101111101101010110101000011_100011011100110  Current Result
 *                                      0-14
 * mht and jgt
 * 1011101111101110_010110101000010100011011100110  Expected Result
 * 1011101111101101_010110101000010100011011100110  Current Result
 *
 * z30 and nbf
 * 1011101111101110010110101000010100011011100110  Expected Result
 * 1011101111101110010110101000010100011011100110  Current Result
 */


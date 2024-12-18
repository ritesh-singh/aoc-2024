package day17

import Env
import com.sun.org.apache.xalan.internal.lib.ExsltMath.power
import println
import readInput
import kotlin.math.abs


private class ChronospatialComputer(env: Env = Env.TEST) {
    private val input = if (env == Env.REAL) readInput("day17/Day17") else readInput("day17/Day17_test")

    private val register = hashMapOf<String, Int>()
    private var program: List<Int>

    init {
        input.take(3)
            .map {
                val key = it.substringAfter("Register").substringBefore(":")
                    .trim()
                val value = it.substringAfter(":").trim().toInt()
                register[key] = value
            }
        program = input.takeLast(1).first().substringAfter(":")
            .trim().split(",").map { it.toInt() }
    }

    private fun Int.operand(): Int {
        return when (this) {
            in 0..3 -> this
            4 -> register["A"]!!
            5 -> register["B"]!!
            6 -> register["C"]!!
            else -> this
        }
    }

    fun programOutput():String {
        val result = mutableListOf<Int>()
        var ip = 0
        while (program.size > ip) {
            val opCode = program[ip]
            val operand = program[ip + 1].operand()
            when (opCode) {
                0 -> register["A"] = register["A"]!! / (1 shl operand)
                1 -> register["B"] = register["B"]!! xor operand
                2 -> register["B"] = operand % 8
                3 -> {
                    if (register["A"]!! != 0) {
                        ip = operand
                        continue
                    }
                }
                4 -> register["B"] = register["B"]!! xor register["C"]!!
                5 -> result.add(operand % 8)
                6 -> register["B"] = register["A"]!! / (1 shl operand)
                7 -> register["C"] = register["A"]!! / (1 shl operand)
                else -> throw IllegalStateException("Faulty program")
            }
            ip+=2
        }

        return result.joinToString(",")
    }
}

fun main() {
    val env = Env.REAL
    ChronospatialComputer(env = env).programOutput().println()
}

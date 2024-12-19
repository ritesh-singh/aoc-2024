package day17

import Env
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

    /**
     * Combo operand:
     * 4 -> A
     * 5 -> B
     * 6 -> C
     */

    /**
     * 0,3, 5,4, 3,0
     */
//    private fun reverseEngg(oldA:Int):Pair<Int,Int> {
//        var a = oldA
//        // 0,3
//        a = a shr 3
//
//        // 5,4
//        val out = a and 7
//
//        return Pair(a,out)
//    }


    /**
     * 2,4, 1,7, 7,5, 0,3, 4,0, 1,7, 5,5, 3,0
     * It has to be long for Part2 :D
     */
    private fun reverseEngg(oldA:Long):Pair<Long,Long> {
        var a = oldA

        // 2 & 4
        var b = a and 7L

        // 1 & 7
        b = b xor 7

        // 7 & 5
        var c = a shr b.toInt()

        // 0,3
        a = a shr 3

        // 4,0
        b = b xor c

        // 1,7
        b = b xor 7

        // 5,5
        val out = b and 7

        // 3,0
        return a to out
    }

    fun part2():Long{
        val queue  = ArrayDeque<Pair<Int,Long>>()
        queue.add(program.size to 0L)

        while (queue.isNotEmpty()) {
            val (cindex, cA) = queue.removeFirst()
            if (cindex == 0) return cA
            val instruction = program[cindex - 1].toLong()

            for (i in 0L..7L){
                val (newA, output) = reverseEngg((cA shl 3) or i)
                if (output == instruction && newA == cA)
                    queue.add(cindex - 1 to ((newA shl 3) or i))
            }
        }
        throw IllegalStateException("Should not happen")
    }
}

fun main() {
    val env = Env.REAL
    ChronospatialComputer(env = env).programOutput().println()
    ChronospatialComputer(env = env).part2().println()
}

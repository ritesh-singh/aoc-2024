package day22

import Env
import println
import readInput
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.round
import kotlin.time.measureTime

private class MonkeyMarket(env: Env) {
    private val input = if (env == Env.REAL) readInput("day22/Day22") else readInput("day22/Day22_test")

    private fun Long.mix(value: Long) = this xor value
    private fun Long.prune() = this % 16777216
    private fun Long.mul64() = this * 64
    private fun Long.div32() = this / 32
    private fun Long.mul2048() = this * 2048

    private fun Long.nextSecretNum() = this.mix(this.mul64()).prune()
        .let { it.div32().mix(it).prune() }.let { it.mul2048().mix(it).prune() }

    private fun all2000thSNumber() = input.map { it.toLong() }
        .map {
            buildList {
                var secret = it
                add(secret)
                repeat(2000) {
                    secret = secret.nextSecretNum()
                    add(secret)
                }
            }
        }

    fun sumOf2000thSNumber() = all2000thSNumber().sumOf { it.last() }

    fun bananas():Long {
        return buildList {
            all2000thSNumber().map { it.map { it % 10 } }.forEach { buyer: List<Long> ->
                val map = mutableMapOf<String, Long>()
                buyer.zipWithNext { a, b -> b - a }
                    .mapIndexed { index, priceChange ->
                        Pair(buyer[index + 1], priceChange)
                    }.let {
                        it.windowed(4).map {
                            val pair = it.map { it.second }.joinToString("") to it.last().first
                            if (!map.containsKey(pair.first)) {
                                map[pair.first] = pair.second
                            }
                        }
                    }
                add(map)
            }
        }.let { all ->
            val set = mutableSetOf<String>().also {
                it.addAll(all.map { it.keys }.flatten())
            }.toMutableList()
            var bananas = 0L
            while (set.isNotEmpty()) {
                val toLook = set.removeFirst()
                var total = 0L
                for (b in all) {
                    if (b.containsKey(toLook)) {
                        total += b[toLook]!!
                        continue
                    }
                }
                bananas = max(total, bananas)
            }
            bananas
        }
    }
}

fun main() {
    val env = Env.REAL
    MonkeyMarket(env).sumOf2000thSNumber().println()
    MonkeyMarket(env).bananas().println()
}

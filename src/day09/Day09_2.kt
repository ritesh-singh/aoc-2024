package day09

import println
import readInput
import readText
import java.util.UUID
import kotlin.math.abs

sealed class Block {
    data class File(val id: Int, val count: Int) : Block()
    data class Space(val uId: String, val count: Int) : Block()
}
typealias File = Block.File
typealias Space = Block.Space

fun main() {
    fun part2(input: String): Long {
        val blocks = mutableListOf<Block>()
        var idx = 0

        input.forEachIndexed { index, c ->
            when {
                index % 2 == 0 -> blocks.add(File(id = idx++, count = c.digitToInt()))
                else -> {
                    if (c.digitToInt() != 0)
                        blocks.add(Space(uId = UUID.randomUUID().toString(), count = c.digitToInt()))
                }
            }
        }

        blocks.reversed().filter { it is File }.map { it as File }.dropLast(1)
            .map { toMove ->
                blocks.find { it is Space && it.count >= toMove.count }?.let {
                    val spaceIdx = blocks.indexOf(it)
                    blocks.add(spaceIdx, toMove) // add before space
                    val diff = abs((it as Space).count - toMove.count)
                    if (diff == 0) blocks.removeAt(spaceIdx + 1)
                    else blocks[spaceIdx + 1] = (blocks[spaceIdx + 1] as Space).copy(count = diff)

                    val toMoveIdx = blocks.lastIndexOf(toMove)
                    if (blocks[toMoveIdx - 1] is File)
                        blocks.add(toMoveIdx, Space(uId = UUID.randomUUID().toString(), count = toMove.count))
                    else
                        blocks[toMoveIdx - 1] = (blocks[toMoveIdx - 1] as Space).copy(
                            count = (blocks[toMoveIdx - 1] as Space).count + toMove.count
                        )
                    blocks.removeAt(blocks.lastIndexOf(toMove))
                }
            }


        var total = 0L
        var index = 0
        blocks.forEach {
            if (it is Space) index += it.count
            if (it is File) {
                var (id, count) = it
                while (count-- > 0) total += id * index++
            }
        }

        return total
    }

    val input = readText("day09/Day09")
    part2(input).println()

}
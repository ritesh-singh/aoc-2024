package day09

import println
import readInput
import readText
import kotlin.math.abs

private class DiskFragmenter {
    data class Block(val id: Int, val total: Int, val nxtFreeSpace: Int = 0)

    val diskMap = mutableListOf<Block>()

    init {
        var id = 0
        readText("day09/Day09")
            .forEachIndexed { index, c ->
                when {
                    index % 2 == 0 -> { diskMap.add(Block(id = id++, total = c.digitToInt())) }
                    else -> { diskMap[diskMap.lastIndex] = diskMap.last().copy(nxtFreeSpace = c.digitToInt()) }
                }
            }
    }

    fun compactFs(): Long {
        val newDiskMap = mutableListOf<Block>()
        var prevSpaceFilled = true
        var spaceToBeFilled = 0
        while (diskMap.isNotEmpty()) {
            var total = diskMap.last().total
            if (prevSpaceFilled) {
                if (diskMap.size == 1) {
                    if (newDiskMap.last().id == diskMap.last().id) {
                        newDiskMap[newDiskMap.lastIndex] = newDiskMap.last().copy(total = diskMap.last().total + newDiskMap.last().total)
                    } else {
                        newDiskMap.add(diskMap.last())
                    }
                    break
                }
                spaceToBeFilled = diskMap.first().nxtFreeSpace
                newDiskMap.add(diskMap.removeFirst())
            }
            when {
                total == spaceToBeFilled -> {
                    newDiskMap.add(diskMap.removeLast())
                    prevSpaceFilled = true
                }
                total > spaceToBeFilled -> {
                    newDiskMap.add(diskMap.last().copy(total = spaceToBeFilled))
                    diskMap[diskMap.lastIndex] = diskMap.last().copy(total = total - spaceToBeFilled)
                    prevSpaceFilled = true
                }
                total < spaceToBeFilled -> {
                    prevSpaceFilled = false
                    spaceToBeFilled = spaceToBeFilled - total
                    newDiskMap.add(diskMap.removeLast())
                }
            }
        }

        fun checksum(): Long {
            var id = 0
            var total = 0L
            newDiskMap.forEach {
                val d = it.id
                var t = it.total
                while (t != 0) {
                    total += d * id++
                    --t
                }
            }
            return total
        }

        return checksum()
    }
}

fun main() {

    fun part1(): Long = DiskFragmenter().compactFs()

    part1().println()
}


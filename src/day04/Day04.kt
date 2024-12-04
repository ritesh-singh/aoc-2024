package day04

import println
import readInput

fun main() {

    fun part1(grid: List<String>): Int {
        val directions = listOf(
            Pair(0, 1),
            Pair(1, 0),
            Pair(1, 1),
            Pair(1, -1),
            Pair(0, -1),
            Pair(-1, 0),
            Pair(-1, -1),
            Pair(-1, 1)
        )
        val target = "XMAS"
        val rows = grid.size
        val cols = grid[0].length
        var count = 0

        fun isXmas(x: Int, y: Int, dx: Int, dy: Int): Boolean {
            for (i in target.indices) {
                val nx = x + i * dx
                val ny = y + i * dy
                if (nx !in 0 until rows || ny !in 0 until cols || grid[nx][ny] != target[i]) {
                    return false
                }
            }
            return true
        }

        for (x in 0 until rows) {
            for (y in 0 until cols) {
                for ((dx, dy) in directions) {
                    if (isXmas(x, y, dx, dy)) {
                        count++
                    }
                }
            }
        }
        return count
    }

    fun part2(input: List<String>): Int {
        var count = 0
        val rows = input.size
        val cols = input[0].length

        for (row in 0 until rows ) {
            for (col in 0 until cols) {
                try {
                    if (input[row][col] == 'A') {
                        val patterns = listOf(
                            listOf('M', 'M', 'S', 'S'),
                            listOf('S', 'S', 'M', 'M'),
                            listOf('M', 'S', 'M', 'S'),
                            listOf('S', 'M', 'S', 'M')
                        )

                        val diagonals = listOf(
                            input[row - 1][col - 1],
                            input[row - 1][col + 1],
                            input[row + 1][col - 1],
                            input[row + 1][col + 1]
                        )

                        for (pattern in patterns) {
                            if (diagonals == pattern) {
                                count++
                            }
                        }
                    }
                }catch (e: IndexOutOfBoundsException) {}

            }
        }

        return count
    }

    val input = readInput("day04/Day04")
    part1(input).println()
    part2(input).println()
}

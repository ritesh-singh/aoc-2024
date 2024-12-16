package day15

import Coord
import CoordRC
import Env
import day15.WarehouseWoes.BOXTYPE.Companion.type
import down
import inRange
import left
import println
import readInput
import readText
import right
import up

private const val WALL_C = '#'
private const val BOX_C = 'O'
private const val EMPTY_C = '.'
private const val ROBOT_C = '@'

private const val UP = '^'
private const val DOWN = 'v'
private const val LEFT = '<'
private const val RIGHT = '>'

private class WarehouseWoes(env: Env = Env.TEST) {
    private val input = if (env == Env.REAL) readInput("day15/Day15") else readInput("day15/Day15_test")

    private val wareHouseMap: List<CharArray> = input.takeWhile { it.isNotEmpty() }.map { it.toCharArray() }
    private val instructions = input.takeLastWhile { it.isNotEmpty() }

    private fun CoordRC.value() = wareHouseMap[row][col]

    private fun Char.next(posn: CoordRC): CoordRC {
        return when (this) {
            UP -> posn.up()
            DOWN -> posn.down()
            LEFT -> posn.left()
            RIGHT -> posn.right()
            else -> throw IllegalArgumentException()
        }
    }

    private sealed class BOXTYPE {
        data object WALL : BOXTYPE()
        data object EMPTY : BOXTYPE()
        data object BOX : BOXTYPE()
        companion object {
            fun Char.type() = when (this) {
                WALL_C -> WALL
                EMPTY_C -> EMPTY
                BOX_C -> BOX
                else -> {
                    throw IllegalArgumentException()
                }
            }
        }
    }

    private fun move(dir: Char, cPos: CoordRC): CoordRC {
        return when (dir.next(cPos).value().type()) {
            BOXTYPE.WALL -> {
                cPos
            } // next box is a wall
            BOXTYPE.EMPTY -> { // next box is emtpty
                val nxt = dir.next(cPos)
                wareHouseMap[cPos.row][cPos.col] = EMPTY_C
                wareHouseMap[nxt.row][nxt.col] = ROBOT_C
                nxt
            }
            BOXTYPE.BOX -> {
                val nxt = dir.next(cPos)
                when (dir) {
                    RIGHT, LEFT -> {
                        val dotCoord = wareHouseMap.withIndex().filter {
                            it.index == cPos.row
                        }.let {
                            check(it.size == 1)
                            val list = mutableListOf<Int>()
                            it[0].value.mapIndexed { index, c ->
                                if ((if (dir == RIGHT) index > cPos.col
                                    else index < cPos.col) && c == EMPTY_C
                                ) {
                                    list.add(index)
                                }
                            }
                            if (dir == RIGHT && list.isNotEmpty()
                                && (cPos.col..list.first() - 1).map {
                                    wareHouseMap[cPos.row][it]
                                }.any { it == WALL_C }
                            ) {
                                list.clear()
                            }

                            if (dir == LEFT && list.isNotEmpty() && (list.last() + 1..cPos.col).map {
                                    wareHouseMap[cPos.row][it]
                                }.any { it == WALL_C }) {
                                list.clear()
                            }
                            list
                        }.let {
                            if (it.isEmpty()) cPos
                            else CoordRC(
                                row = cPos.row,
                                col = if (dir == RIGHT) it.first() else it.last()
                            )
                        }
                        if (dotCoord == cPos) cPos
                        else {
                            wareHouseMap[dotCoord.row][dotCoord.col] = BOX_C
                            wareHouseMap[cPos.row][cPos.col] = EMPTY_C
                            wareHouseMap[nxt.row][nxt.col] = ROBOT_C
                            nxt
                        }
                    }

                    DOWN, UP -> {
                        val dotCoord = wareHouseMap.indices.toList()
                            .filter {
                                if (dir == UP) it < cPos.row else it > cPos.row
                            }.filter {
                                wareHouseMap[it][cPos.col] == EMPTY_C
                            }.let {
                                val it = it.toMutableList()
                                if (dir == DOWN && it.isNotEmpty() && (cPos.row..it.first() - 1).map {
                                        wareHouseMap[it][cPos.col]
                                    }.any { it == WALL_C }) {
                                    it.clear()
                                }
                                if (dir == UP && it.isNotEmpty() && (it.last() + 1..cPos.row).map {
                                        wareHouseMap[it][cPos.col]
                                    }.any { it == WALL_C }) {
                                    it.clear()
                                }
                                if (it.isEmpty()) cPos
                                else CoordRC(
                                    row = if (dir == DOWN) it.first() else it.last(),
                                    col = cPos.col
                                )
                            }
                        if (dotCoord == cPos) cPos
                        else {
                            wareHouseMap[dotCoord.row][dotCoord.col] = BOX_C
                            wareHouseMap[cPos.row][cPos.col] = EMPTY_C
                            wareHouseMap[nxt.row][nxt.col] = ROBOT_C
                            nxt
                        }
                    }
                    else -> throw IllegalArgumentException()
                }
            }
        }
    }

    private fun moveRobot() {
        var pos = CoordRC(0, 0)
        wareHouseMap.let {
            it.forEachIndexed { row, chars ->
                if (chars.withIndex().find { it.value == ROBOT_C } != null) {
                    pos = CoordRC(row, chars.withIndex().find { it.value == ROBOT_C }!!.index)
                }
            }
        }
        instructions.map {
            it.toCharArray().map {
                pos = move(dir = it, cPos = pos)
            }
        }
    }

    fun GPScoordinates():Long {
        moveRobot()
        return wareHouseMap.mapIndexed { row, chars ->
            buildList {
                chars.forEachIndexed { col, c ->
                    if (c == BOX_C) add(CoordRC(row = row, col = col))
                }
            }
        }.flatten().map {
            it.row* 100 + it.col.toLong()
        }.sum()
    }
}

fun main() {
    fun part1(): Long = WarehouseWoes(env = Env.REAL).GPScoordinates()
    part1().println()
}

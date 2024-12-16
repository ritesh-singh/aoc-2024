package day15


import Coord
import CoordRC
import Env
import day15.WarehouseWoes_2.BOXTYPE.Companion.type
import down
import inRange
import left
import println
import readInput
import readText
import right
import up
import kotlin.math.abs
import kotlin.math.min

private const val WALL_C = '#'
private const val EMPTY_C = '.'
private const val ROBOT_C = '@'

private const val UP = '^'
private const val DOWN = 'v'
private const val LEFT = '<'
private const val RIGHT = '>'

private class WarehouseWoes_2(env: Env = Env.TEST) {
    private val input = (if (env == Env.REAL) readInput("day15/Day15") else readInput("day15/Day15_test"))
        .map {
            it.replace("#", "##").replace("O", "[]")
                .replace(".", "..")
                .replace("@", "@.")
        }

    private val wareHouseMap = input.takeWhile { it.isNotEmpty() }.toMutableList()
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
                '[', ']' -> BOX
                else -> {
                    throw IllegalArgumentException()
                }
            }
        }
    }

    private fun move(dir: Char, cPos: CoordRC): CoordRC {
        when (dir.next(cPos).value().type()) {
            BOXTYPE.WALL -> return cPos
            BOXTYPE.EMPTY -> {
                val nxt = dir.next(cPos)
                wareHouseMap[cPos.row] = wareHouseMap[cPos.row].replaceRange(cPos.col..cPos.col, ".")
                wareHouseMap[nxt.row] = wareHouseMap[nxt.row].replaceRange(nxt.col..nxt.col, "@")
                return nxt
            }
            BOXTYPE.BOX -> {
                val nxt = dir.next(cPos)
                when (dir) {
                    LEFT -> {
                        val dotCoord = wareHouseMap.withIndex().filter {
                            it.index == cPos.row
                        }.let {
                            val list = mutableListOf<Int>()
                            it[0].value.mapIndexed { index, c ->
                                if (index < cPos.col && c == EMPTY_C)
                                    list.add(index)
                            }
                            if (list.isNotEmpty() && (list.last() + 1..cPos.col).map {
                                    wareHouseMap[cPos.row][it]
                                }.any { it == WALL_C }) {
                                list.clear()
                            }
                            list
                        }
                        if (dotCoord.isEmpty()) return cPos
                        else {
                            wareHouseMap[cPos.row] =
                                wareHouseMap[cPos.row].replaceRange(
                                    dotCoord.last()..cPos.col - 1,
                                    wareHouseMap[cPos.row].substring(dotCoord.last() + 1, cPos.col + 1)
                                )
                            wareHouseMap[cPos.row] =
                                wareHouseMap[cPos.row].replaceRange(cPos.col..cPos.col, ".")
                        }
                        return nxt
                    }

                    RIGHT -> {
                        val dotCoord = wareHouseMap.withIndex().filter {
                            it.index == cPos.row
                        }.let {
                            val list = mutableListOf<Int>()
                            it[0].value.mapIndexed { index, c ->
                                if (index > cPos.col && c == EMPTY_C) {
                                    list.add(index)
                                }
                            }
                            if (list.isNotEmpty()
                                && (cPos.col..list.first() - 1).map {
                                    wareHouseMap[cPos.row][it]
                                }.any { it == WALL_C }
                            ) {
                                list.clear()
                            }
                            list
                        }

                        if (dotCoord.isEmpty()) return cPos
                        else {
                            wareHouseMap[cPos.row] =
                                wareHouseMap[cPos.row].replaceRange(
                                    cPos.col + 1..dotCoord.first(),
                                    wareHouseMap[cPos.row].substring(cPos.col, dotCoord.first())
                                )
                            wareHouseMap[cPos.row] =
                                wareHouseMap[cPos.row].replaceRange(cPos.col..cPos.col, ".")
                        }
                        return nxt
                    }

                    UP -> {
                        val bracketType = wareHouseMap[cPos.row - 1][cPos.col]
                        val (rb_lx, rb_rx) =
                            if (bracketType == '[') Pair(cPos.col, cPos.col + 1)
                            else if (bracketType == ']') Pair(cPos.col - 1, cPos.col)
                            else return cPos

                        val stack = ArrayDeque<List<Pair<Int, Int>>>()
                        stack.add(listOf(Pair(rb_lx, rb_rx)))

                        run {
                            var row = cPos.row - 1
                            while (true) {
                                val set = mutableSetOf<Pair<Int, Int>>()
                                stack.last().forEach {// Pair of left and right col for bracket
                                    val lType = wareHouseMap[row - 1][it.first]
                                    val rType = wareHouseMap[row - 1][it.second]
                                    if (lType == WALL_C || rType == WALL_C)
                                        return cPos
                                    if (lType == '[' && rType == ']') {
                                        set.add(Pair(it.first, it.second))
                                    } else {
                                        if (lType == ']')
                                            set.add(Pair(it.first - 1, it.first))
                                        if (rType == '[')
                                            set.add(Pair(it.second, it.second + 1))
                                    }
                                }
                                if (set.isEmpty()) break
                                stack.add(set.toList())
                                row--
                            }
                        }
                        while (stack.isNotEmpty()) {
                            val curRow = cPos.row - stack.size
                            val curr = stack.removeLast()
                            curr.forEach {
                                val (lc, rc) = it
                                wareHouseMap[curRow] = wareHouseMap[curRow].replaceRange(lc..lc, ".")
                                wareHouseMap[curRow] = wareHouseMap[curRow].replaceRange(rc..rc, ".")
                                wareHouseMap[curRow - 1] = wareHouseMap[curRow - 1].replaceRange(lc..lc, "[")
                                wareHouseMap[curRow - 1] = wareHouseMap[curRow - 1].replaceRange(rc..rc, "]")
                            }
                        }
                        wareHouseMap[cPos.row] = wareHouseMap[cPos.row].replaceRange(cPos.col..cPos.col, ".")
                        wareHouseMap[nxt.row] = wareHouseMap[nxt.row].replaceRange(nxt.col..nxt.col, "@")
                        return nxt
                    }

                    DOWN -> {
                        val bracketType = wareHouseMap[cPos.row + 1][cPos.col]
                        val (rb_lx, rb_rx) = if (bracketType == '[') Pair(cPos.col, cPos.col + 1)
                        else if (bracketType == ']') Pair(cPos.col - 1, cPos.col)
                        else return cPos

                        val stack = ArrayDeque<List<Pair<Int, Int>>>()
                        stack.add(listOf(Pair(rb_lx, rb_rx)))

                        run {
                            var row = cPos.row + 1
                            while (true) {
                                val set = mutableSetOf<Pair<Int, Int>>()
                                stack.last().forEach {
                                    val lType = wareHouseMap[row + 1][it.first]
                                    val rType = wareHouseMap[row + 1][it.second]
                                    if (lType == WALL_C || rType == WALL_C)
                                        return cPos
                                    if (lType == '[' && rType == ']') {
                                        set.add(Pair(it.first, it.second))
                                    } else {
                                        if (lType == ']')
                                            set.add(Pair(it.first - 1, it.first))
                                        if (rType == '[')
                                            set.add(Pair(it.second, it.second + 1))
                                    }
                                }
                                if (set.isEmpty()) break
                                stack.add(set.toList())
                                ++row
                            }
                        }
                        while (stack.isNotEmpty()) {
                            val curRow = stack.size + cPos.row
                            val curr = stack.removeLast()
                            curr.forEach {
                                val (lc, rc) = it
                                wareHouseMap[curRow] = wareHouseMap[curRow].replaceRange(lc..lc, ".")
                                wareHouseMap[curRow] = wareHouseMap[curRow].replaceRange(rc..rc, ".")
                                wareHouseMap[curRow + 1] = wareHouseMap[curRow + 1].replaceRange(lc..lc, "[")
                                wareHouseMap[curRow + 1] = wareHouseMap[curRow + 1].replaceRange(rc..rc, "]")
                            }
                        }
                        wareHouseMap[cPos.row] = wareHouseMap[cPos.row].replaceRange(cPos.col..cPos.col, ".")
                        wareHouseMap[nxt.row] = wareHouseMap[nxt.row].replaceRange(nxt.col..nxt.col, "@")
                        return nxt
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

    fun GPScoordinates(): Long {
        moveRobot()
        return wareHouseMap.mapIndexed { row, chars ->
            buildList {
                chars.forEachIndexed { col, c ->
                    if (c == '[') add(CoordRC(row = row, col = col))
                }
            }
        }.flatten().map { it.row * 100 + it.col.toLong() }.sum()
    }
}

private fun main() {
    fun part2(): Long = WarehouseWoes_2(env = Env.REAL).GPScoordinates()
    part2().println()
}

package day16

import CoordRC
import DirectionLRUD
import DirectionP
import Env
import all
import down
import left
import printGrid
import println
import readInput
import right
import up
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.collections.HashSet
import kotlin.math.abs


private class ReindeerMaze(env: Env = Env.TEST) {
    private val input = if (env == Env.REAL) readInput("day16/Day16") else readInput("day16/Day16_test")

    private lateinit var start: CoordRC
    private lateinit var end: CoordRC

    init {
        input.mapIndexed { row, s ->
            s.withIndex().find { it.value == 'S' }?.let { start = CoordRC(row, it.index) }
            s.withIndex().find { it.value == 'E' }?.let { end = CoordRC(row, it.index) }
        }
    }

    private fun DirectionLRUD.rotates(nextD: DirectionLRUD): Boolean {
        return when (this) {
            DirectionLRUD.L, DirectionLRUD.R -> nextD == DirectionLRUD.D || nextD == DirectionLRUD.U
            DirectionLRUD.U, DirectionLRUD.D -> nextD == DirectionLRUD.L || nextD == DirectionLRUD.R
        }
    }

    private fun DirectionLRUD.nextScore(nextD: DirectionLRUD, curScore: Long): Long {
        return when {
            this.rotates(nextD) -> curScore + 1000 + 1
            else -> curScore + 1
        }
    }

    private data class Node(
        val coordRC: CoordRC,
        val direction: DirectionLRUD
    )

    private fun CoordRC.canMove() = row in input.indices && col in input[0].indices && input[row][col] != '#'

    fun lowestScore(): Long {
        val queue = PriorityQueue<Pair<Node, Long>>(compareBy { it.second })
        val visited = hashSetOf<CoordRC>()
        val scores = hashMapOf<CoordRC, Long>()
        queue.add(Pair(Node(start, DirectionLRUD.R), 0L))
        scores[start] = 0L

        while (queue.isNotEmpty()) {
            val (cNode, cScore) = queue.remove()
            visited.add(cNode.coordRC)

            if (scores.getOrDefault(cNode.coordRC, Long.MAX_VALUE) < cScore) continue
            if (cNode.coordRC == end) break

            val neighbors = cNode.coordRC.let {
                buildList {
                    if (it.up().canMove()) add(Pair(it.up(), DirectionLRUD.U))
                    if (it.down().canMove()) add(Pair(it.down(), DirectionLRUD.D))
                    if (it.left().canMove()) add(Pair(it.left(), DirectionLRUD.L))
                    if (it.right().canMove()) add(Pair(it.right(), DirectionLRUD.R))
                }
            }

            for (neigh in neighbors) {
                if (visited.contains(neigh.first)) continue
                val newScore = cNode.direction.nextScore(nextD = neigh.second, curScore = cScore)
                if (newScore > scores.getOrDefault(neigh.first, Long.MAX_VALUE)) continue
                scores[neigh.first] = newScore
                queue.add(Pair(Node(neigh.first, neigh.second), newScore))
            }
        }
        return scores[end]!!
    }

    fun allTilesMakingBestPath():Long {
        val pQ = PriorityQueue<Pair<Node,Long>>(compareBy { it.second })

        val bestScores = hashMapOf<Node,Long>()
        bestScores[Node(start, DirectionLRUD.R)] = 0L
        pQ.add(Pair(Node(start, DirectionLRUD.R), 0L))

        val backTrack = hashMapOf<Node,MutableSet<Node>>()
        var bestScore = Long.MAX_VALUE
        val endStates = hashSetOf<Node>()

        while (pQ.isNotEmpty()) {
            val (cN,cS) = pQ.remove()
            if (cS > bestScores.getOrDefault(cN, Long.MAX_VALUE)) continue
            if (cN.coordRC == end){
                if (cS > bestScore) break
                bestScore = cS
                endStates.add(cN)
            }
            val neighbors = cN.coordRC.let {
                buildList {
                    if (it.up().canMove()) add(Pair(it.up(), DirectionLRUD.U))
                    if (it.down().canMove()) add(Pair(it.down(), DirectionLRUD.D))
                    if (it.left().canMove()) add(Pair(it.left(), DirectionLRUD.L))
                    if (it.right().canMove()) add(Pair(it.right(), DirectionLRUD.R))
                }
            }
            for (neigh in neighbors) {
                val lowScore = bestScores[Node(neigh.first, neigh.second)] ?: Long.MAX_VALUE
                val newScore = cN.direction.nextScore(nextD = neigh.second, curScore = cS)
                if (newScore > lowScore) continue
                if (newScore < lowScore) {
                    backTrack[Node(neigh.first,neigh.second)] = mutableSetOf()
                    bestScores[Node(neigh.first,neigh.second)] = newScore
                }
                backTrack[Node(neigh.first,neigh.second)]?.add(cN)
                pQ.add(Pair(Node(neigh.first, neigh.second), newScore))
            }
        }

        // Back-track from the set
        val states = ArrayDeque(endStates)
        val seen = hashSetOf(*endStates.toList().toTypedArray())
        while (states.isNotEmpty()) {
            val cur = states.removeFirst()
            for(last in backTrack[cur]?:emptyList()) {
                if (last in seen) continue
                seen.add(last)
                states.add(last)
            }
        }
        return seen.map { it.coordRC }.toSet().size.toLong()
    }
}

fun main() {
    val env = Env.REAL
    ReindeerMaze(env = env).lowestScore().println()
    ReindeerMaze(env = env).allTilesMakingBestPath().println()
}

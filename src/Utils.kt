import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readText

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readText().trim().lines()

fun readText(name: String) = Path("src/$name.txt").readText().trim()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)


data class Coord(val x: Int, val y: Int) {
    override fun toString(): String {
        return "($x, $y)"
    }
}

fun Coord.inRange(grid: List<String>) = x in grid.indices && y in grid[0].indices

fun Coord.up() = Coord(x - 1, y)
fun Coord.down() = Coord(x + 1, y)
fun Coord.left() = Coord(x, y - 1)
fun Coord.right() = Coord(x, y + 1)
fun Coord.all() = listOf(up(),down(),left(),right())


/**
 *  Helper utils for Coords in row and column to avoid confusion
 */
data class CoordRC(val row:Int, val col:Int) {
    override fun toString(): String {
        return "($row, $col)"
    }
}
fun CoordRC.inRange(grid: List<String>) = row in grid.indices && col in grid[0].indices
fun CoordRC.up() = CoordRC(row - 1, col)
fun CoordRC.down() = CoordRC(row + 1, col)
fun CoordRC.left() = CoordRC(row, col - 1)
fun CoordRC.right() = CoordRC(row, col + 1)
fun CoordRC.all() = listOf(up(),down(),left(),right())


enum class Env {
    TEST, REAL
}
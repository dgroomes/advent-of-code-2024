import java.nio.file.Path
import kotlin.io.path.readText

// I do not condone this code. I'm just Kotlin-maxxing for education.
fun main() {
    val wordSearch = wordSearch()
    val matrix = wordSearch.lines().map { it.toCharArray() }
    val max = matrix.size

    fun next(dir: Pair<Int, Int>, x: Int, y: Int): () -> Char {
        var _x = x
        var _y = y

        return fun(): Char = if (_x < 0 || _x >= max || _y < 0 || _y >= max) '-' else matrix[_x][_y].also {
            _x += dir.first
            _y += dir.second
        }
    }

    fun xmas(next: () -> Char) = next() == 'X' && next() == 'M' && next() == 'A' && next() == 'S'

    (0..max).asSequence()
        .flatMap { x ->
            (0..max).asSequence().flatMap { y ->
                (-1..1).asSequence().flatMap { x -> (-1..1).asSequence().map { x to it } }.toSet().minus(0 to 0)
                    .asSequence().map { xmas(next(it, x, y)) }
            }
        }
        .filter { it }.count().also { println(it) }
}

private fun wordSearch() =
    Path.of(
        if (System.getenv().containsKey("EXAMPLE")) "input/day-04-example.txt" else "input/day-04-full.txt"
    ).readText().trimEnd()

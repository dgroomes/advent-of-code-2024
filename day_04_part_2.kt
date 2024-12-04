import java.nio.file.Path
import kotlin.io.path.readText

fun main() {
    val wordSearch = wordSearch()
    val matrix = wordSearch.lines().map { it.toCharArray() }
    val max = matrix.size

    fun xmas(x: Int, y: Int): Boolean {
        val tl = matrix[x - 1][y - 1]
        val tr = matrix[x + 1][y - 1]
        val bl = matrix[x - 1][y + 1]
        val br = matrix[x + 1][y + 1]

        return matrix[x][y] == 'A' &&
                ((tl == 'M' && br == 'S') || (tl == 'S' && br == 'M')) &&
                ((bl == 'M' && tr == 'S') || (bl == 'S' && tr == 'M'))
    }

    var matches = 0
    for (x in 1..<max - 1) {
        for (y in 1..<max - 1) {
            if (xmas(x, y)) matches++
        }
    }
    println(matches)
}

private fun wordSearch(): String {
    val path =
        if (System.getenv().containsKey("EXAMPLE")) "input/day-04-part-2-example.txt" else "input/day-04-full.txt"
    return Path.of(path).readText().trimEnd()
}

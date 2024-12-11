package day_10.part_2

import java.nio.file.Path
import kotlin.io.path.readText


fun main() {
    val topoMap = topoMap()
    val bounds = topoMap.indices

    fun inBound(x: Int, y: Int) = x in bounds && y in bounds
    fun height(x: Int, y: Int) = topoMap[y][x]
    val dirs = setOf(0 to -1, 0 to 1, -1 to 0, 1 to 0)

    fun rating(prevH: Int, x: Int, y: Int): Int {
        if (!inBound(x, y)) return 0
        val hN = height(x, y)
        if (hN - prevH != 1) return 0
        if (hN == 9) return 1

        return dirs.sumOf {
            rating(hN, x + it.first, y + it.second)
        }
    }

    val score = bounds.sumOf { y ->
        bounds.sumOf { x ->
            if (height(x, y) == 0) rating(-1, x, y) else 0
        }
    }
    println(score)
}


fun topoMap(): List<List<Int>> {
    val text = Path.of(
        if (System.getenv().containsKey("EXAMPLE")) "input/day-10-example.txt" else "input/day-10-full.txt"
    ).readText().trimEnd()

    return text.lines().map { it.toList().map(Char::digitToInt) }
}

package day_10.part_1

import java.nio.file.Path
import kotlin.io.path.readText

fun main() {
    val topoMap = topoMap()
    val bounds = topoMap.indices

    fun inBound(x: Int, y: Int) = x in bounds && y in bounds
    fun height(x: Int, y: Int) = topoMap[y][x]
    val dirs = setOf(0 to -1, 0 to 1, -1 to 0, 1 to 0)

    fun climbToPeaks(prevH: Int, x: Int, y: Int): Set<Pair<Int, Int>> {
        if (!inBound(x, y)) return emptySet()
        val hN = height(x, y)
        if (hN - prevH != 1) return emptySet()
        if (hN == 9) return setOf(x to y)

        return dirs.flatMap {
            climbToPeaks(hN, x + it.first, y + it.second)
        }.toSet()
    }

    val score = bounds.sumOf { y ->
        bounds.sumOf { x ->
            if (height(x, y) == 0) climbToPeaks(-1, x, y).size else 0
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

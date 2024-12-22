@file:Suppress("DuplicatedCode")

package day_18.part_1

import java.nio.file.Path
import kotlin.io.path.readText

data class Puzzle(val corrupted: Set<Coord>, val bounds: IntRange)
fun Coord.dirs() : List<Coord> = listOf(Coord(x, y - 1), Coord(x, y + 1), Coord(x - 1, y), Coord(x + 1, y))

fun main(vararg args: String) {
    val (corrupted, bounds) = input(*args)

    val cost = mutableMapOf<Coord, Int>()

    fun minPaths(space: Coord, steps: Int) {
        if (space.x !in bounds || space.y !in bounds) return
        if (space in corrupted) return
        cost[space]?.let { if (it <= steps) return }
        cost[space] = steps

        space.dirs().forEach{ minPaths(it, steps + 1) }
    }

    minPaths(Coord(0, 0), 0)

    println(cost[Coord(bounds.last, bounds.last)])
}

data class Coord(val x: Int, val y: Int)

fun input(vararg args: String): Puzzle {
    val variation = args.getOrElse(0) { "full" }
    val bounds = if (variation == "full") 0..70 else 0..6
    val limit = if (variation == "full") 1024 else 12
    val corrupted = Path.of("input/day-18-$variation.txt").readText().trim().lines().take(limit).map {
        val (x, y) = it.split(",")
        Coord(x.toInt(), y.toInt())
    }

    return Puzzle(corrupted.toSet(), bounds)
}

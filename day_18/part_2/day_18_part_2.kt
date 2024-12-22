@file:Suppress("DuplicatedCode")

package day_18.part_2

import java.nio.file.Path
import kotlin.io.path.readText

data class Puzzle(val corrupted: List<Coord>, val bounds: IntRange)

data class Coord(val x: Int, val y: Int)

fun Coord.dirs(): List<Coord> = listOf(Coord(x, y - 1), Coord(x, y + 1), Coord(x - 1, y), Coord(x + 1, y))

fun main(vararg args: String) {
    val (corrupted, bounds) = input(*args)
    val origin = Coord(0, 0)

    val spread = mutableSetOf<Coord>()
    val stillCorrupted = corrupted.toMutableSet()

    fun creep(space: Coord) {
        if (space.x !in bounds || space.y !in bounds || space in spread || space in stillCorrupted) return
        spread.add(space)
        space.dirs().forEach(::creep)
    }

    creep(Coord(bounds.last, bounds.last))

    for (space in corrupted.reversed()) {
        stillCorrupted.remove(space)
        if (space.dirs().any(spread::contains)) {
            creep(space)
            if (origin in spread) {
                println("${space.x},${space.y}")
                return
            }
        }
    }

    println("Unexpected. Did not resolve.")
}

fun input(vararg args: String): Puzzle {
    val variation = args.getOrElse(0) { "full" }
    val bounds = if (variation == "full") 0..70 else 0..6
    val corrupted = Path.of("input/day-18-$variation.txt").readText().trim().lines().map {
        val (x, y) = it.split(",")
        Coord(x.toInt(), y.toInt())
    }

    return Puzzle(corrupted, bounds)
}

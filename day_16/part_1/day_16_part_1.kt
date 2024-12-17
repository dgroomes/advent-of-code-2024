@file:Suppress("DuplicatedCode")

package day_16.part_1

import day_16.part_1.Dir.*
import day_16.part_1.Move.*
import kotlin.io.path.readText

const val SAFETY = 1_000_000

enum class Dir { NORTH, EAST, SOUTH, WEST }
enum class Move { STEP, R_CLOCKWISE, R_COUNTER_CLOCKWISE }
data class Coord(val x: Int, val y: Int)
data class Position(val coord: Coord, val dir: Dir)
data class Path(val moves: List<Move>, val score: Int, val pos: Position)
data class Map(val spots: List<List<Boolean>>)
data class Puzzle(val map: Map, val start: Coord, val end: Coord)

fun main(vararg args: String) {
    val puzzle = input(*args)
    val spots = puzzle.map.spots

    // Paths and how we got there. Cheaper paths replace more expensive paths as they are discovered.
    val paths = mutableMapOf<Position, Path>()

    val queue = ArrayDeque<Path>()
    queue.add(Path(emptyList(), 0, Position(puzzle.start, EAST)))
    var ops = 0
    while (queue.isNotEmpty()) {
        if (ops++ > SAFETY) throw IllegalStateException("SAFETY TRIPPED")
        val path = queue.removeFirst()
        val pos = path.pos
        val coord = pos.coord
        val moves = path.moves
        val score = path.score
        val (x, y) = pos.coord
        if (x !in spots.indices || y !in spots.indices || !spots[y][x]) continue

        val bPath = paths[pos]
        if (bPath != null && bPath.score <= score) continue

        paths[pos] = path

        val stepCoord: Coord
        val clockDir: Dir
        val counterDir: Dir

        when (pos.dir) {
            NORTH -> {
                stepCoord = coord.copy(y = y - 1)
                clockDir = EAST
                counterDir = WEST
            }

            EAST -> {
                stepCoord = coord.copy(x = x + 1)
                clockDir = SOUTH
                counterDir = NORTH
            }

            SOUTH -> {
                stepCoord = coord.copy(y = y + 1)
                clockDir = WEST
                counterDir = EAST
            }

            WEST -> {
                stepCoord = coord.copy(x = x - 1)
                clockDir = NORTH
                counterDir = SOUTH
            }
        }

        val step = path.copy(moves = moves.plus(STEP), score = score + 1, pos = pos.copy(coord = stepCoord))
        val clock = path.copy(moves = moves.plus(R_CLOCKWISE), score = score + 1000, pos = pos.copy(dir = clockDir))
        val counter = path.copy(
            moves = moves.plus(R_COUNTER_CLOCKWISE), score = score + 1000,
            pos = pos.copy(dir = counterDir)
        )
        queue.add(step)
        queue.add(clock)
        queue.add(counter)
    }

    val best = Dir.entries.mapNotNull { paths[Position(puzzle.end, it)] }.minBy { it.score }
    println("Best path has a score of ${best.score}")
}

fun input(vararg args: String): Puzzle {
    val variation = args.getOrElse(0) { "full" }

    var start: Coord? = null
    var end: Coord? = null
    val spots: List<MutableList<Boolean>> = java.nio.file.Path.of("input/day-16-$variation.txt")
        .readText()
        .trim()
        .lines()
        .map { it.toCharArray() }
        .mapIndexed { y, row ->
            row.mapIndexed { x, c ->
                when (c) {
                    '#' -> false
                    'S' -> {
                        start = Coord(x, y)
                        true
                    }

                    'E' -> {
                        end = Coord(x, y)
                        true
                    }

                    else -> true
                }
            }.toMutableList()
        }

    return Puzzle(Map(spots), start!!, end!!)
}

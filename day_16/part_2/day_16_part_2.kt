@file:Suppress("DuplicatedCode")

package day_16.part_2

import day_16.part_2.Dir.*
import java.time.Duration
import java.time.Instant
import kotlin.collections.ArrayDeque
import kotlin.io.path.readText

const val SAFETY = 1_000_000

enum class Dir { NORTH, EAST, SOUTH, WEST }
data class Coord(val x: Int, val y: Int)
data class Position(val coord: Coord, val dir: Dir)
data class Path(val score: Int, val head: Position, val leaders: Set<Position>)
data class Puzzle(val map: List<List<Boolean>>, val start: Coord, val end: Coord)

fun main(vararg args: String) {
    val start = Instant.now()
    val puzzle = input(*args)
    val spots = puzzle.map

    // Positions and how we got there. Cheaper paths replace more expensive paths as they are discovered.
    val paths = mutableMapOf<Position, Path>()

    val queue = mutableListOf(Path(0, Position(puzzle.start, EAST), emptySet()))
    var ops = 0
    while (queue.isNotEmpty()) {
        if (ops++ > SAFETY) throw IllegalStateException("SAFETY TRIPPED")
        val path = queue.removeFirst()
        val (score, head, leaders) = path
        val (coord, dir) = head
        val (x, y) = coord
        if (x !in spots.indices || y !in spots.indices || !spots[y][x]) continue

        val bPath = paths[head]
        when {
            // A first path for this position or a better path than the previously found one.
            bPath == null || score < bPath.score -> paths[head] = path

            // A worse path. Move on.
            score > bPath.score -> continue

            else -> {
                // An equally scoring path. Merge its leaders into the existing path and move on.
                paths[head] = bPath.copy(leaders = bPath.leaders.plus(leaders))
                continue
            }
        }

        val stepCoord: Coord
        val clockDir: Dir
        val counterDir: Dir

        when (dir) {
            NORTH -> {
                stepCoord = Coord(x, y - 1)
                clockDir = EAST
                counterDir = WEST
            }

            EAST -> {
                stepCoord = Coord(x + 1, y)
                clockDir = SOUTH
                counterDir = NORTH
            }

            SOUTH -> {
                stepCoord = Coord(x, y + 1)
                clockDir = WEST
                counterDir = EAST
            }

            WEST -> {
                stepCoord = Coord(x - 1, y)
                clockDir = NORTH
                counterDir = SOUTH
            }
        }

        val step = path.copy(score = score + 1, head = head.copy(coord = stepCoord))
        val clock = path.copy(score = score + 1000, head = head.copy(dir = clockDir))
        val counter = path.copy(score = score + 1000, head = head.copy(dir = counterDir))
        listOf(step, clock, counter).forEach { queue.add(it.copy(leaders = setOf(head))) }
    }

    val endings = Dir.entries.mapNotNull { paths[Position(puzzle.end, it)] }
    val best = endings.minOf { it.score }
    val dur = Duration.between(start, Instant.now())

    val traceBack = ArrayDeque(endings.filter { it.score == best })
    val bestSpots = mutableSetOf<Coord>()
    while (traceBack.isNotEmpty()) {
        if (ops++ > SAFETY) throw IllegalStateException("SAFETY TRIPPED")
        val end = traceBack.removeFirst()
        bestSpots.add(end.head.coord)
        end.leaders.forEach { traceBack.add(paths[it]!!) }
    }

    println("Best path has a score of %,d ($best). Calculated after %,d operations in $dur".format(best, ops))
    println("There are ${bestSpots.size} spots on the best paths")
}

fun input(vararg args: String): Puzzle {
    val variation = args.getOrElse(0) { "full" }
    var start: Coord? = null
    var end: Coord? = null
    val spots: List<MutableList<Boolean>> = java.nio.file.Path.of("input/day-16-$variation.txt")
        .readText().trim().lines()
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

    return Puzzle(spots, start!!, end!!)
}

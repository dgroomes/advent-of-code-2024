@file:Suppress("DuplicatedCode")

package day_15.part_1

import day_15.part_1.Item.*
import day_15.part_1.Dir.*
import java.nio.file.Path
import kotlin.io.path.readText

data class Coord(val x: Int, val y: Int)

fun Coord.next(dir: Dir): Coord {
    val (x, y) = this
    return when (dir) {
        UP -> Coord(x, y - 1)
        DOWN -> Coord(x, y + 1)
        LEFT -> Coord(x - 1, y)
        RIGHT -> Coord(x + 1, y)
    }
}

enum class Item { WALL, BOX, BLANK }
enum class Dir { UP, DOWN, LEFT, RIGHT }
data class Map(val spots: List<MutableList<Item>>) {
    val xBounds: IntRange
    val yBounds: IntRange

    init {
        val height = spots.size
        val width = spots[0].size
        xBounds = 0..<width
        yBounds = 0..<height
    }

    fun item(c: Coord) = spots[c.y][c.x]

    fun inBound(c: Coord) = c.x in xBounds && c.y in yBounds

    fun item(c: Coord, item: Item) {
        spots[c.y][c.x] = item
    }
}

data class Puzzle(val map: Map, val robot: Coord, val moves: List<Dir>)

fun main(vararg args: String) {
    val puzzle = input(*args)

    val map = puzzle.map
    var robot = puzzle.robot
    val moves = puzzle.moves

    fun printMap() {
        map.spots.forEachIndexed { y, row ->
            row.forEachIndexed { x, item ->
                val c = if (x == robot.x && y == robot.y) '@' else when (item) {
                    WALL -> '#'
                    BOX -> 'O'
                    BLANK -> '.'
                }
                print(c)
            }
            println()
        }
    }

    for (dir in moves) {
//        println("Robot is trying to move $dir...")
//        printMap()
        fun push(coord: Coord): Boolean {
            if (!map.inBound(coord) || map.item(coord) == WALL) return false
            if (map.item(coord) == BLANK) return true

            val next = coord.next(dir)
            val pushed = push(next)
            if (pushed) {
                map.item(coord, BLANK) // Leave a blank spot
                map.item(next, BOX)
                return true
            }
            return false
        }

        val next = robot.next(dir)
        if (push(next)) {
            robot = next
//            println("Robot moved $dir.")
//        } else println("Robot was blocked.")
        }
//        println()
    }

    val gpsSum = map.yBounds.sumOf { y ->
        map.xBounds.sumOf { x ->
            if (map.spots[y][x] == BOX) 100 * y + x else 0
        }
    }

    println("GPS sum: %,d ($gpsSum)".format(gpsSum))
//    println("Final map")
//    printMap()
}

fun input(vararg args: String): Puzzle {
    val variation = args.getOrElse(0) { "full" }
    val (mapStr, movesStr) = Path.of("input/day-15-$variation.txt").readText().trim().split("\n\n")

    val map: List<CharArray> = mapStr.lines().map { it.toCharArray() }
    var robot: Coord? = null
    val mapBoxed: List<MutableList<Item>> = map.mapIndexed { y, row ->
        row.mapIndexed { x, c ->
            when (c) {
                '#' -> WALL
                'O' -> BOX
                '@' -> {
                    robot = Coord(x, y)
                    BLANK
                }

                else -> BLANK
            }
        }.toMutableList()
    }

    val moves = movesStr.replace("\n", "").toCharArray().map {
        when (it) {
            '^' -> UP
            'v' -> DOWN
            '<' -> LEFT
            '>' -> RIGHT
            else -> throw IllegalStateException("Unrecognized move: $it")
        }
    }

    return Puzzle(Map(mapBoxed), robot!!, moves)
}

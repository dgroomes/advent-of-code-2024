@file:Suppress("DuplicatedCode")

package day_15.part_2

import day_15.part_2.Dir.*
import day_15.part_2.Item.*
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

enum class Item { WALL, BOX_L, BOX_R, BLANK }
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
                    BOX_L -> '['
                    BOX_R -> ']'
                    BLANK -> '.'
                }
                print(c)
            }
            println()
        }
    }

//    println("Initial state:")
//    printMap()
//    println()

    for (dir in moves) {
        fun pushHorizontal(coord: Coord): Boolean {
            // Note: left-right movement is easier because the push can't span fanning out boxes. Let's implement it
            // first.
            val item = map.item(coord)
            if (!map.inBound(coord) || item == WALL) return false
            if (item == BLANK) return true

            val next = coord.next(dir)
            val pushed = pushHorizontal(next)
            if (pushed) {
                map.item(coord, BLANK) // Leave a blank spot
                map.item(next, item)
                return true
            }
            return false
        }

        fun canPushVertical(coord: Coord): Boolean {
            val item = map.item(coord)
            if (!map.inBound(coord) || item == WALL) return false
            if (item == BLANK) return true

            val shift = if (item == BOX_L) 1 else -1
            val side = coord.copy(x = coord.x + shift)

            val next = coord.next(dir)
            val sideNext = side.next(dir)

            return canPushVertical(next) && canPushVertical(sideNext)
        }

        fun pushVertical(coord: Coord) {
            val item = map.item(coord)
            if (!map.inBound(coord) || item == WALL) return
            if (item == BLANK) return

            // Push this side of box
            val next = coord.next(dir)
            pushVertical(next)
            map.item(coord, BLANK) // Leave a blank spot
            map.item(next, item)

            // Push other side of box
            val shift: Int
            val sItem: Item
            if (item == BOX_L) {
                sItem = BOX_R
                shift = 1
            } else {
                sItem = BOX_L
                shift = -1
            }

            val sCoord = coord.copy(x = coord.x + shift)
            val sNext = sCoord.next(dir)
            pushVertical(sNext)
            map.item(sCoord, BLANK) // Leave a blank spot
            map.item(sNext, sItem)
        }

        val next = robot.next(dir)

        val pushed: Boolean
        if (dir == UP || dir == DOWN) {
            if (canPushVertical(next)) {
                pushVertical(next)
                pushed = true
            } else {
                pushed = false
            }
        } else {
            pushed = pushHorizontal(next)
        }

        if (pushed) {
            robot = next
//            println("Moved $dir.")
        } else {
//            println("Move attempt $dir blocked")
        }

//        printMap()
//        println()
    }

    val gpsSum = map.yBounds.sumOf { y ->
        map.xBounds.sumOf { x ->
            if (map.spots[y][x] == BOX_L) 100 * y + x else 0
        }
    }

    println("GPS sum: %,d ($gpsSum)".format(gpsSum))
    println("Final map")
    printMap()
}

fun input(vararg args: String): Puzzle {
    val variation = args.getOrElse(0) { "full" }
    val (mapStr, movesStr) = Path.of("input/day-15-$variation.txt").readText().trim().split("\n\n")

    val map: List<CharArray> = mapStr.lines().map { it.toCharArray() }
    var robot: Coord? = null
    val mapBoxed: List<MutableList<Item>> = map.mapIndexed { y, row ->
        row.flatMapIndexed { x, c ->
            when (c) {
                '#' -> listOf(WALL, WALL)
                'O' -> listOf(BOX_L, BOX_R)
                '@' -> {
                    robot = Coord(x * 2, y)
                    listOf(BLANK, BLANK)
                }

                else -> listOf(BLANK, BLANK)
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

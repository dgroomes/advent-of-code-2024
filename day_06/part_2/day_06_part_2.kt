package day_06.part_2

import day_06.part_2.Dir.*
import day_06.part_2.Sym.*
import java.nio.file.Path
import kotlin.io.path.readText

data class Coord(val x: Int, val y: Int)

enum class Dir { UP, DOWN, LEFT, RIGHT }
enum class Sym { OBSTACLE, EMPTY }

fun main() {
    val puzzle = puzzle()
    val (map, guardCoord) = puzzle

    val height = map.size
    val width = map.first().size
    val size = width * height

    var stuckPositions = 0
    for (y in 0..<height) {
        for (x in 0..<width) {
            val c = Coord(x, y)
            if (c != guardCoord && map[y][x] != OBSTACLE) {
                map[y][x] = OBSTACLE
                if (loops(puzzle, size)) stuckPositions++
                map[y][x] = EMPTY
            }
        }
    }

    println(stuckPositions)
}

fun loops(puzzle: Puzzle, size: Int) : Boolean {
    val map = puzzle.map
    val maxX = map.first().size - 1
    val maxY = map.size - 1

    var guardCoord = puzzle.guardCoord
    var guardDir = puzzle.guardDir

    var loops = 0
    while (true) {
        if (loops++ > size) return true
        val aheadCoord = when (guardDir) {
            UP -> Coord(guardCoord.x, guardCoord.y - 1)
            DOWN -> Coord(guardCoord.x, guardCoord.y + 1)
            LEFT -> Coord(guardCoord.x - 1, guardCoord.y)
            RIGHT -> Coord(guardCoord.x + 1, guardCoord.y)
        }
        val (aX, aY) = aheadCoord

        if (aX < 0 || aX > maxX || aY < 0 || aY > maxY) break

        val aheadSym = map[aY][aX]
        when (aheadSym) {
            OBSTACLE -> guardDir = when (guardDir) {
                UP -> RIGHT
                DOWN -> LEFT
                LEFT -> UP
                RIGHT -> DOWN
            }
            EMPTY -> guardCoord = aheadCoord
        }
    }

    return false
}

data class Puzzle(val map: List<MutableList<Sym>>, val guardCoord: Coord, val guardDir: Dir)

fun puzzle(): Puzzle {
    val text = Path.of(
        if (System.getenv().containsKey("EXAMPLE")) "input/day-06-example.txt" else "input/day-06-full.txt"
    ).readText().trimEnd()

    var guardCoord: Coord? = null
    var guardDir: Dir? = null
    val grid = text.lines().mapIndexed { y, row ->
        row.toCharArray().mapIndexed { x, space ->

            fun guard(dir: Dir) = EMPTY.also {
                guardCoord = Coord(x, y)
                guardDir = dir
            }

            when (space) {
                '.' -> EMPTY
                '#' -> OBSTACLE
                '^' -> guard(UP)
                'v' -> guard(DOWN)
                '<' -> guard(LEFT)
                '>' -> guard(RIGHT)
                else -> throw IllegalArgumentException("Unknown symbol: $space")
            }
        }.toMutableList()
    }

    return Puzzle(grid, guardCoord!!, guardDir!!)
}

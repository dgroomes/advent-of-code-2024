package day_06.part_1

import day_06.part_1.Dir.*
import day_06.part_1.Sym.*
import java.io.File
import java.nio.file.Path
import kotlin.io.path.readText

data class Coord(val x: Int, val y: Int)

enum class Dir { UP, DOWN, LEFT, RIGHT }
enum class Sym { OBSTACLE, VISITED, UNVISITED }

fun main() {
    val (map, _guardCoord, _guardDir) = puzzle()
    val maxX = map.first().size - 1
    val maxY = map.size - 1

    var guardCoord = _guardCoord
    var guardDir = _guardDir
    var visited = 1

    val debugF = File("debug.txt").also { it.writeText("") }
    fun debug() {
        for ((y, rows) in map.withIndex()) {
            for ((x, sym) in rows.withIndex()) {
                val c = if (x == guardCoord.x && y == guardCoord.y) {
                    when (guardDir) {
                        UP -> '^'
                        DOWN -> 'v'
                        LEFT -> '<'
                        RIGHT -> '>'
                    }
                } else {
                    when (sym) {
                        OBSTACLE -> '#'
                        VISITED -> 'X'
                        UNVISITED -> '.'
                    }
                }
                debugF.appendText(c.toString())
            }
            debugF.appendText(System.lineSeparator())
        }
        debugF.appendText(System.lineSeparator())
    }

    while (true) {
//        debug()
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
            OBSTACLE -> {
                guardDir = when(guardDir) {
                    UP -> RIGHT
                    DOWN -> LEFT
                    LEFT -> UP
                    RIGHT -> DOWN
                }
            }
            VISITED -> guardCoord = aheadCoord
            UNVISITED -> {
                guardCoord = aheadCoord
                map[aY][aX] = VISITED
                visited++
            }
        }
    }

    println(visited)
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

            fun guard(dir: Dir) = VISITED.also {
                guardCoord = Coord(x, y)
                guardDir = dir
            }

            when (space) {
                '.' -> UNVISITED
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

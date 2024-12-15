@file:Suppress("DuplicatedCode")

package day_14.part_2

import java.io.File
import java.nio.file.Path
import kotlin.io.path.readText
import kotlin.math.absoluteValue

data class Coord(var x: Int, var y: Int)
data class Robot(val pos: Coord, val vel: Coord)
typealias Grid = List<List<Boolean>>

fun hashCode(grid: Grid): Int = grid.map { it.hashCode() }.hashCode()

const val seconds = 10_500
//const val seconds = 100

//const val width = 11
const val width = 101

const val width_mid = width / 2
const val widest_i = width - 1

//const val height = 7
const val height = 103
const val tallest_i = height - 1

/**
 * Doesn't work. The tree is not centered so symmetric over the middle doesn't matter. I found the solution in my
 * "entropy" solution.
 */
fun main(vararg args: String) {
    val variation = args.getOrElse(0) { "full" }
    val robots = Path.of("../../input/day-14-$variation.txt").readText().trim().lines().map { line ->
        val (p, v) = line
            .replace("p=", "")
            .replace("v=", "")
            .split(" ")
            .map { it.split(",").map(String::toInt) }

        val (pX, pY) = p
        val (vX, vY) = v

        Robot(Coord(pX, pY), Coord(vX, vY))
    }

    // Track the "highest symmetry" grid
    var hSym = 0
    var hGrid: List<List<Boolean>>? = null
    var hGridS = 0

    var uniqueC = 0 // Well this isn't guaranteed to be unique because some may hash to the same value but w/e
    val configs = mutableSetOf<Int>()

    println("Finding highest symmetry grid among $seconds seconds...")
    val grid: MutableList<MutableList<Boolean>> = MutableList(height) { MutableList(width) { false } }
    repeat(seconds) { second ->
        if (second % 10_000 == 0) println(
            "%,d/%,d and %,d unique configurations found...".format(
                second,
                seconds,
                uniqueC
            )
        )
        var sym = 0

        for (robot in robots) {
            var (pX, pY) = robot.pos
            grid[pY][pX] = false // Clear it out

            val xDir = robot.vel.x.coerceIn(-1, 1)
            val yDir = robot.vel.y.coerceIn(-1, 1)
            repeat(robot.vel.x.absoluteValue) {
                pX += xDir
                if (pX == width) pX = 0 else if (pX == -1) pX = widest_i
            }
            repeat(robot.vel.y.absoluteValue) {
                pY += yDir
                if (pY == height) pY = 0 else if (pY == -1) pY = tallest_i
            }
            robot.pos.x = pX
            robot.pos.y = pY

            grid[pY][pX] = true
        }

        if (configs.add(hashCode(grid))) uniqueC++

        val symmetricPositions = robots.filter { robot ->
            val (pX, pY) = robot.pos
            (pX != width_mid) && grid[pY][widest_i - pX]
        }.map { it.pos }.distinct()

        sym += symmetricPositions.size

        if (sym > hSym) {
            hSym = sym
            hGrid = grid.map { it.map { it } }
            hGridS = second
            println("New score achieved. symmetry=$hSym @ second=$hGridS")
        }
    }


    println("Highest symmetry grid has a score of $hSym and it occurred at second $hGridS. Unique configurations: $uniqueC")
    File("grid.txt").writeText(gridStr(hGrid!!))
}


fun gridStr(grid: List<List<Boolean>>): String {
    val sb = StringBuilder()
    for (y in (0..tallest_i)) {
        for (x in 0..widest_i) {
            val c = if (grid[y][x]) {
                if (x == width_mid) 'X' else if (grid[y][widest_i - x]) 'O' else 'X'
            } else '.'
            sb.append(c)
        }
        sb.appendLine()
    }
    return sb.toString()
}


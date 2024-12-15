@file:Suppress("DuplicatedCode")

package day_14.part_2_alt

import java.io.File
import java.nio.file.Path
import kotlin.io.path.readText
import kotlin.math.absoluteValue

data class Coord(var x: Int, var y: Int)
data class Robot(val pos: Coord, val vel: Coord)
typealias Grid = List<List<Boolean>>
val dirs : List<Pair<Int, Int>> = (-1..1).flatMap { x -> (-1..1).map { y -> x to y } }.filter { it != 0 to 0 }

fun hashCode(grid: Grid): Int = grid.map { it.hashCode() }.hashCode()

const val seconds = 10_500
//const val seconds = 100

//const val width = 11
const val width = 101

const val width_mid = width / 2
const val widest_i = width - 1
val x_bounds = 0..widest_i

//const val height = 7
const val height = 103
const val tallest_i = height - 1
val y_bounds = 0..tallest_i

/**
 * So close. 8005 is too low but I see the tree.
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

    // Track the "lowest entropy" grid
    var lEnt = robots.size
    var lGrid: List<List<Boolean>>? = null
    var lGridS = 0

    var uniqueC = 0 // Well this isn't guaranteed to be unique because some may hash to the same value but w/e
    val configs = mutableSetOf<Int>()

    println("Finding lowest entropy grid among $seconds seconds...")
    val grid: MutableList<MutableList<Boolean>> = MutableList(height) { MutableList(width) { false } }
    repeat(seconds) { second ->
        if (second % 10_000 == 0) println(
            "%,d/%,d and %,d unique configurations found...".format(
                second,
                seconds,
                uniqueC
            )
        )
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

        val entropyPositions = robots.filter { robot ->
            val (pX, pY) = robot.pos

            dirs.all {
                val nX = pX + it.first
                val nY = pY + it.second
                nX !in x_bounds || nY !in y_bounds || !grid[nY][nX]
            }
        }.map { it.pos }.distinct()

        val ent = entropyPositions.size

        if (ent < lEnt) {
            lEnt = ent
            lGrid = grid.map { it.map { it } }
            lGridS = second
            println("New score achieved. entropy=$lEnt @ second=$lGridS")
        }
    }

    println("Lowest entropy grid has a score of $lEnt and it occurred at second $lGridS. Unique configurations: $uniqueC")
    File("grid.txt").writeText(gridStr(lGrid!!))
}


fun gridStr(grid: List<List<Boolean>>): String {
    val sb = StringBuilder()
    for (y in (0..tallest_i)) {
        for (x in 0..widest_i) {
            val c = if (grid[y][x]) { 'X' } else '.'
            sb.append(c)
        }
        sb.appendLine()
    }
    return sb.toString()
}


package day_14.part_1

import java.nio.file.Path
import kotlin.io.path.readText
import kotlin.math.absoluteValue

data class Coord(var x: Int, var y: Int)
data class Robot(val pos: Coord, val vel: Coord)

val seconds = 100
val width = 101
val width_mid = width / 2
val widest_i = width - 1
val height = 103
val height_mid = height / 2
val tallest_i = height - 1

fun main(vararg args: String) {
    val variation = args.getOrElse(0, { "full" })
    val robots = Path.of("input/day-14-$variation.txt").readText().trim().lines().map {
        val (p, v) = it
            .replace("p=", "")
            .replace("v=", "")
            .split(" ")
            .map { it.split(",").map(String::toInt) }

        val (pX, pY) = p
        val (vX, vY) = v

        Robot(Coord(pX, pY), Coord(vX, vY))
    }
//    robots = robots.filter { it.pos == Coord(2, 4) }
//    println(robots)

    for (robot in robots) {
        var (pX, pY) = robot.pos
        val xDir = robot.vel.x.coerceIn(-1, 1)
        val yDir = robot.vel.y.coerceIn(-1, 1)
        repeat(seconds) {
            repeat(robot.vel.x.absoluteValue) {
                pX += xDir
                if (pX == width) pX = 0 else if (pX == -1) pX = widest_i
            }
            repeat(robot.vel.y.absoluteValue) {
                pY += yDir
                if (pY == height) pY = 0 else if (pY == -1) pY = tallest_i
            }
        }
        robot.pos.x = pX
        robot.pos.y = pY
    }

    val quads = mutableMapOf<Int, Long>()
    for (robot in robots) {
        val (x, y) = robot.pos

        var q = 0
        if (x == width_mid || y == height_mid) continue
        if (x > width_mid) q++
        if (y > height_mid) q += 2
        quads.merge(q, 1L, Long::plus)
    }

    println(quads.values.reduce(Long::times))
//    println(robots)
}



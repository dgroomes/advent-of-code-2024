package day_13.part_2

import java.nio.file.Path
import java.time.Duration
import java.time.Instant
import kotlin.io.path.readText

data class Coord(val x: Long, val y: Long)
data class Machine(val a: Coord, val b: Coord, val prize: Coord)

fun main(vararg args: String) {
    val variation = args.getOrElse(0, { "full" })
    val machines = Path.of("input/day-13-$variation.txt").readText().trim().lines().chunked(4).map {
        fun button(line: String) = line
            .removePrefix("Button A: X+")
            .removePrefix("Button B: X+")
            .split(", Y+")
            .map(String::toLong).let { Coord(it[0], it[1]) }

        val (x, y) = it[2].removePrefix("Prize: X=").split(", Y=")
//            .map(String::toLong)
            .map { it.toLong() + 10_000_000_000_000 }
        Machine(button(it[0]), button(it[1]), Coord(x, y))
    }

    var start = Instant.now()

    val cost = machines.sumOf {
        println(it)
        val solution = solve(it)
        println(solution)
        if (solution is Solution.Possible) {
            solution.aPresses * 3 + solution.bPresses
        } else {
            0L
        }
    }
    println(cost)
    println(Duration.between(start, Instant.now()))
}

fun Coord.slope() = slope(x, y)
fun slope(x :Long, y: Long) = y.toDouble() / x

fun solve(machine: Machine): Solution {
    val aSlope = machine.a.slope()
    val bSlope = machine.b.slope()

    val steepSlope : Double
    val shallowCoord : Coord
    val steepCoord : Coord

    if (aSlope > bSlope) {
        steepSlope = aSlope
        steepCoord = machine.a
        shallowCoord = machine.b
    } else {
        steepSlope = bSlope
        steepCoord = machine.b
        shallowCoord = machine.a
    }

    val (shallowX, shallowY) = shallowCoord
    val (steepX, steepY) = steepCoord
    val (pX, pY) = machine.prize

    var floor = 0L
    // how many shallows does it take to span at least the full width of the prize width?
    var ceil = (machine.prize.x / shallowX) + 1
    var mid = ceil / 2

    var safety = 100
    while (true) {
        safety--
        if (safety <= 0) throw IllegalStateException("Tripped the safety at $mid")

        val xTravelled = shallowX * mid
        val yTravelled = shallowY * mid

        var xToGo = pX - xTravelled
        var yToGo = pY - yTravelled

        val steepSteps = xToGo / steepX
        xToGo -= steepSteps * steepX
        yToGo -= steepSteps * steepY

        if (xToGo == 0L && yToGo == 0L) {
            val aPresses: Long
            val bPresses: Long
            if (shallowCoord == machine.a) {
                aPresses = mid
                bPresses = steepSteps
            } else {
                bPresses = mid
                aPresses = steepSteps
            }
            return Solution.Possible(aPresses, bPresses)
        }

        val remainder = slope(xToGo, yToGo)
        if (remainder > steepSlope) {
            // A remainder slope that is steeper than the steep slope means we went too far on the shallow travel.
            // Lower the ceiling so that we never travel that far again.
            ceil = mid
        } else {
            // Raise the floor
            floor = mid
        }
        val newMid = ((ceil - floor) / 2) + floor
        if (newMid == mid) {
            println("Made no progress. We've bottom out the search. The mid hasn't changed.")
            return Solution.Impossible
        }

        mid = newMid
    }
}

sealed interface Solution {
    data object Impossible : Solution
    data class Possible(val aPresses: Long, val bPresses: Long) : Solution
}



package day_13.part_1

import java.nio.file.Path
import kotlin.io.path.readText

data class Coord(val x: Int, val y: Int)
data class Machine(val a: Coord, val b: Coord, val prize: Coord)

fun main(vararg args: String) {
    val variation = args.getOrElse(0, { "full" })
    val machines = Path.of("input/day-13-$variation.txt").readText().trim().lines().chunked(4).map {
        fun button(line: String) = line
            .removePrefix("Button A: X+")
            .removePrefix("Button B: X+")
            .split(", Y+")
            .map(String::toInt).let { Coord(it[0], it[1]) }

        val (x, y) = it[2].removePrefix("Prize: X=").split(", Y=").map(String::toInt)
        Machine(button(it[0]), button(it[1]), Coord(x, y))
    }

    // There's two slopes. They may both be more steep, or both more shallow, or one of each compared to the slope to
    // the prize. If they are a combination, then there are two symmetric paths to the prize, so we can focus on one.
    // But the path will only stick the landing if the divisibility is there. But because I dont' know what part 2 is
    // going to be, I'm just going to brute force this.

    val cost = machines.sumOf {
//        println(it)
        val solution = solve(it)
        if (solution is Solution.Possible) {
            solution.aPresses * 3 + solution.bPresses
        } else {
            0
        }
//        println(solution)
    }
    println(cost)
}

fun Coord.slope() = slope(x, y)
fun slope(x :Int, y: Int) = y.toDouble() / x

fun solve(machine: Machine): Solution {
    val aSlope = machine.a.slope()
    val bSlope = machine.b.slope()
    val prizeSlope = machine.prize.slope()
//    println("aSlope: $aSlope\tbSlope: $bSlope\tprizeSlope: $prizeSlope")

    // A to B to get P
    // 1 A times ? Bs = P
    //  Ay/Ax By/Bx = Py/Px
    //  .75 ? .25 = .5
    //  (.75 + .25) / 2 = .5
    //  (.75p + .25q)  = .5

    // 1.43    .40   =    .56
    // (1.43 +  p * .40) / (1 + p)   =    .56
    // (1.43 + p * .40) / (1 + p)   =    .56
    // 1.43 / (1 + p) + (p * .40)/(1 + p)   =    .56

    // (1.43 + p * .40) / (1 + p)   =    .56
    // 1.43 + p * .40 = .56 + .56p
    // p * .40 = .56 - 1.43 + .56p
    // p = (.56 / .40) - (1.43 / .40) + (.56p / .40)
    // p * .40 - .56p = .56 - 1.43
    // p (.40 - .56) = .56 - 1.43
    // p = (.56 - 1.43) / (.40 - .56)      /////   1steep:Rshallow can be computed by R = (prizeSlope - steepSlope) / (shallowSlope - prizeSlope)
    // p = 5.4375

    // (1.43 + .40 * 5.4375) / (1 + 5.4375)

    val shallowSlope : Double
    val steepSlope : Double
    val shallowCoord : Coord
    val steepCoord : Coord

    var aPresses = 0
    var bPresses = 0
    val pressSteep : ()  -> Unit
    val pressShallow : ()  -> Unit

    if (aSlope > bSlope) {
        steepSlope = aSlope
        steepCoord = machine.a
        shallowSlope = bSlope
        shallowCoord = machine.b
        pressSteep = { aPresses++ }
        pressShallow = { bPresses++ }
    } else {
        steepSlope = bSlope
        steepCoord = machine.b
        shallowSlope = aSlope
        shallowCoord = machine.a
        pressSteep = { bPresses++ }
        pressShallow = { aPresses++ }
    }

    val r = (prizeSlope - steepSlope) / (shallowSlope - prizeSlope)
//    println("shallow:steep r:1 ratio: $r")
//    println("1 part steep to $r parts shallow")
    if (shallowSlope > prizeSlope || steepSlope < prizeSlope) return Solution.Impossible

    // I give up on math for now. Let's do a naive thing.
    val (pX, pY) = machine.prize
    var x = 0
    var y = 0
    while (x < pX && y < pY) {
        val s = slope(x, y)
        val c = if (s < prizeSlope) {
            pressSteep()
            steepCoord
        } else {
            pressShallow()
            shallowCoord
        }
        x += c.x
        y += c.y
    }

    return if (x == pX && y == pY) Solution.Possible(aPresses, bPresses) else Solution.Impossible
}

sealed interface Solution {
    data object Impossible : Solution
    data class Possible(val aPresses: Int, val bPresses: Int) : Solution
}



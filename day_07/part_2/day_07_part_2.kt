package day_07.part_2

import day_07.part_2.Op.*
import java.nio.file.Path
import java.time.Duration
import java.time.Instant
import kotlin.io.path.readText

enum class Op { ADD, MUL, CONCAT }

fun main() {
    val equations = equations()
    val start = Instant.now()

    var calibration = 0L
    eq@ for (eq in equations) {
        val operands = eq.operands

        val numOps = operands.size - 1
        val numCombos = 3.toBigInteger().pow(numOps).toInt()
        for (combo in combos(numCombos, numOps)) {
            var r = operands.first().toLong()
            for (i in 0..<numOps) {
                val operator = combo[i]
                val operand = operands[i + 1]
                r = when (operator) {
                    ADD -> r + operand
                    MUL -> r * operand
                    CONCAT -> (r.toString() + operand.toString()).toLong()
                }
            }

            if (r == eq.testVal) {
                calibration += eq.testVal
                continue@eq
            }
        }
    }

    val dur = Duration.between(start, Instant.now())
    println("Calculated calibration $calibration in $dur")
}

fun combos(numCombos: Int, numOps: Int): Sequence<List<Op>> = (0..<numCombos).asSequence().map { i ->
    val binaryStr = i.toString(3).padStart(numOps, '0')
    binaryStr.map {
        when (it) {
            '0' -> MUL
            '1' -> ADD
            else -> CONCAT
        }
    }
}

data class Equation(val testVal: Long, val operands: List<Int>)

fun equations(): List<Equation> {
    val text = Path.of(
        if (System.getenv().containsKey("EXAMPLE")) "input/day-07-example.txt" else "input/day-07-full.txt"
    ).readText().trimEnd()

    return text.lines().map {
        val splits = it.split(":")
        val result = splits[0].toLong()
        val operands = splits[1].trim().split(" ").map { it.toInt() }
        Equation(result, operands)
    }
}

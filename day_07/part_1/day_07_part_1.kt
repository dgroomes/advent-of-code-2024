package day_07.part_1

import day_07.part_1.Op.ADD
import day_07.part_1.Op.MUL
import java.nio.file.Path
import kotlin.io.path.readText

enum class Op { ADD, MUL }

fun main() {
    val equations = equations()

    var calibration = 0L
    eq@ for (eq in equations) {
//        println("Solving equation: $eq")
        val operands = eq.operands
        val maxRes = maxRes(eq)
        if (maxRes < eq.testVal) continue

        val numOps = operands.size - 1
        val numCombos = 2.toBigInteger().pow(numOps).toInt()
//        println("Trying %,d combos".format(numCombos))
//        var progress = 0
        for (combo in combos(numCombos, numOps)) {
            var r = operands.first().toLong()
            for (i in 0..<numOps) {
                val operator = combo[i]
                val operand = operands[i + 1]
                r = if (operator == ADD) {
                    r + operand
                } else {
                    r * operand
                }
            }
//            if (++progress % 10_000_000 == 0) { println("Progress: %,d".format(progress)) }

            if (r == eq.testVal) {
                calibration += eq.testVal
                continue@eq
            }
        }
    }

    println(calibration)
}

fun maxRes(eq: Equation): Long {
    val operands = eq.operands
    var r = eq.operands.first().toLong()
    val numOps = eq.operands.size - 1
    for (i in 0..<numOps) {
        val operand = operands[i + 1]
        if (operand == 1) r++ else r *= operand
    }
    return r
}

fun combos(numCombos: Int, numOps: Int): Sequence<List<Op>> = (0..<numCombos).asSequence().map { i ->
    val binaryStr = i.toString(2).padStart(numOps, '0')
    binaryStr.map { if (it == '0') MUL else ADD }
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

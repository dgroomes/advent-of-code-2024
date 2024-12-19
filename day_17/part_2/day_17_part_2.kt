@file:Suppress("DuplicatedCode")

package day_17.part_2

import java.io.File
import java.math.BigInteger

/**
 * I need some code that tries a narrow range of 'A' values, executes one loop of the program (i.e. skip the jump
 * instruction at the end) and finds which 'A' outputs a desired value.
 *
 * For example, among candidate 'A' values 1-7, which of them cause the 'out' instruction to output 0?
 *
 * The implementation just inlines instructions 1 through 6 because the sixth instruction is 'out'.
 */
fun legalAValuesForOneProgramRun(candidates: ULongRange, desiredOutput: Int) : List<ULong> {
    println("[legalAValuesForOneProgramRun] Computing legal 'A' values among $candidates that produce any of program output $desiredOutput ...")
    val found = candidates.filter {
        val a = it
        var b = a % 8.toULong()
        b = b xor 4.toULong()
        val c = a / 2.toBigInteger().pow(b.toInt()).toLong().toUInt()
        b = b xor c
        b = b xor 4.toULong()
        b %= 8.toULong()
        println("[legalAValuesForOneProgramRun] Candidate A $a produced output $b")
        b == desiredOutput.toULong()
    }
    return found
}

fun run3BitComputerProgram(initA: Long, program: List<Int>): List<Int> {
    var a = initA
    var b = 0L
    var c = 0L
    var ip = 0
    val output = mutableListOf<Int>()

    fun lit(): Int {
        return program[ip + 1]
    }

    fun combo(): Long {
        val combo = when (val lit = program[ip + 1]) {
            0, 1, 2, 3 -> lit.toLong()
            4 -> a
            5 -> b
            6 -> c
            7 -> throw IllegalStateException("7 is reserved. Illegal")
            else -> throw IllegalStateException("$lit not a legal operand")
        }
        return combo
    }

    fun adv() {
        val oprnd = combo()
        val den = BigInteger.valueOf(2).pow(oprnd.toInt())
        a /= den.toInt()
    }

    fun bxl() {
        b = b xor lit().toLong()
    }

    fun bst() {
        b = combo() % 8
    }

    fun jnz() {
        if (a == 0L) return else ip = lit() - 2
    }

    fun bxc() {
        b = b xor c
    }

    fun out(): Boolean {
        output.add((combo() % 8).toInt())
        return output == program.subList(0, output.size)
    }

    fun bdv() {
        val oprnd = combo()
        val den = BigInteger.valueOf(2).pow(oprnd.toInt())
        b = a / den.toInt()
    }

    fun cdv() {
        val oprnd = combo()
        val den = BigInteger.valueOf(2).pow(oprnd.toInt())
        c = a / den.toInt()
    }

    while (ip < program.size) {
        val inst = program[ip]
        when (inst) {
            0 -> adv()
            1 -> bxl()
            2 -> bst()
            3 -> jnz()
            4 -> bxc()
            5 -> out()
            6 -> bdv()
            7 -> cdv()
        }
        ip += 2
    }

    return output
}

fun main1() {
    val program = program()
    println(run3BitComputerProgram(19623166388831L, program))
}

fun main() {
    val program = program()

    val asFor16 = legalAValuesForOneProgramRun(1.toULong()..7.toULong(), program[program.size-1])

    val asFor15 = asFor16.flatMap { a ->
        val min = a * 8.toULong()
        val max = min + 7.toULong()
        val range = min..max
        legalAValuesForOneProgramRun(range, program[program.size - 2])
    }
    println("Found 'A' values for 15: $asFor15")

    val asFor14 = asFor15.flatMap { a ->
        val min = a * 8.toULong()
        val max = min + 7.toULong()
        val range = min..max
        legalAValuesForOneProgramRun(range, program[program.size - 3])
    }
    println("Found 'A' values for 14: $asFor14")

    val asFor13 = asFor14.flatMap { a ->
        val min = a * 8.toULong()
        val max = min + 7.toULong()
        val range = min..max
        legalAValuesForOneProgramRun(range, program[program.size - 4])
    }
    println("Found 'A' values for 13: $asFor13")

    val asFor12 = asFor13.flatMap { a ->
        val min = a * 8.toULong()
        val max = min + 7.toULong()
        val range = min..max
        legalAValuesForOneProgramRun(range, program[program.size - 5])
    }
    println("Found 'A' values for 12: $asFor12")

    val asFor11 = asFor12.flatMap { a ->
        val min = a * 8.toULong()
        val max = min + 7.toULong()
        val range = min..max
        legalAValuesForOneProgramRun(range, program[program.size - 6])
    }
    println("Found 'A' values for 11: $asFor11")

    val asFor10 = asFor11.flatMap { a ->
        val min = a * 8.toULong()
        val max = min + 7.toULong()
        val range = min..max
        legalAValuesForOneProgramRun(range, program[program.size - 7])
    }
    println("Found 'A' values for 10: $asFor10")

    val asFor9 = asFor10.flatMap { a ->
        val min = a * 8.toULong()
        val max = min + 7.toULong()
        val range = min..max
        legalAValuesForOneProgramRun(range, program[program.size - 8])
    }
    println("Found 'A' values for 9: $asFor9")

    val asFor8 = asFor9.flatMap { a ->
        val min = a * 8.toULong()
        val max = min + 7.toULong()
        val range = min..max
        legalAValuesForOneProgramRun(range, program[program.size - 9])
    }
    println("Found 'A' values for 8: $asFor8")

    val asFor7 = asFor8.flatMap { a ->
        val min = a * 8.toULong()
        val max = min + 7.toULong()
        val range = min..max
        legalAValuesForOneProgramRun(range, program[program.size - 10])
    }
    println("Found 'A' values for 7: $asFor7")

    val asFor6 = asFor7.flatMap { a ->
        val min = a * 8.toULong()
        val max = min + 7.toULong()
        val range = min..max
        legalAValuesForOneProgramRun(range, program[program.size - 11])
    }
    println("Found 'A' values for 6: $asFor6")

    val asFor5 = asFor6.flatMap { a ->
        val min = a * 8.toULong()
        val max = min + 7.toULong()
        val range = min..max
        legalAValuesForOneProgramRun(range, program[program.size - 12])
    }
    println("Found 'A' values for 5: $asFor5")

    val asFor4 = asFor5.flatMap { a ->
        val min = a * 8.toULong()
        val max = min + 7.toULong()
        val range = min..max
        legalAValuesForOneProgramRun(range, program[program.size - 13])
    }
    println("Found 'A' values for 4: $asFor4")

    val asFor3 = asFor4.flatMap { a ->
        val min = a * 8.toULong()
        val max = min + 7.toULong()
        val range = min..max
        legalAValuesForOneProgramRun(range, program[program.size - 14])
    }
    println("Found 'A' values for 3: $asFor3")

    val asFor2 = asFor3.flatMap { a ->
        val min = a * 8.toULong()
        val max = min + 7.toULong()
        val range = min..max
        legalAValuesForOneProgramRun(range, program[program.size - 15])
    }
    println("Found 'A' values for 2: $asFor2")

    val asFor1 = asFor2.flatMap { a ->
        val min = a * 8.toULong()
        val max = min + 7.toULong()
        val range = min..max
        legalAValuesForOneProgramRun(range, program[program.size - 16])
    }
    println("Found 'A' values for 1: $asFor1")
    println("Found 'A' values for 1 sorted: ${asFor1.sorted()}")
    println("Found 'A' values for 1 min: ${asFor1.min()}")
}

fun program(): List<Int> {
    return File("input/day-17-full.txt").readText().lines()[4].split(": ")[1].split(",").map { it.toInt() }
}

/*
NOTES (lots omitted to avoid including the instructions and puzzle input)

# Observations
There is no bdv in the program.

The program has a simple repeating pattern because the only jump instruction is the last instruction and it jumps
to the beginning.

'A' only shrinks. The only instruction that writes 'A' is the 'adv' instruction which divides it by 2^3 == 8.

There must be a range of values for 'A' that could ever cause the program to repeat exactly 15 times and thus
produce a 16-length output, which is the length of the program itself (our goal). What's the lowest 'A' for that
and the highest? Is that range small enough to narrow the work?

Ok so that narrows it down but the range is still huge: ~250 trillion.

How far can we jump the As between attempts?

After the first run, the bxl operations are a no-op because they are consecutive.

B can't be divisible by 8 until the 13th run

Do we actually now all the A's after the first?

Do we work backward (like we often need to?)? Start with 0?
  - A must be ending at 1-7 after the 15th go. So with that... what does that do?
  - B must be divisible by 8 at the out. B must end in 000.
  - So undoing the xor 4 means that B needs to end in 111 before the bxl.
  - What do we do at C?

B is re-derived from A on every loop. B has no statefulness in a sense. C is derived from B (and therefore A) and
A on every loop.

Ok I ran some code. A has to be 4 on the 16th loop because I know it has to be between 1-7 and 4 is the only one
that outputs 0, and also we know we isolated B and C because they are derived from A on every loop.

So that means we just multiply by 8???
- On the 16th iteration, A must be 4
- Therefore on the 15th iteration, A was 4 * 8 = 32
- Therefore on the 14th iteration, A was 4 * 8 * 8 = 256
- Therefore on the ith iteration, A was  4 * 8 ^ (16 - i)
- Therefore on the 1st iteration, A was 4 * 8 ^ (16 - 1) = 4 * 8^15 = 140737488355328 (140,737,488,355,328)

Well so that's a new minimum right? It didn't work, but we can just add 1s for a bit and get some different
numbers?

(35,184,372,088,832) min for producing 16 output
(281,474,976,710,655) max for producing 16 output
(140,737,488,355,328) is a higher floor, needed to produce a final 0 in the output. I suppose we could find the
max? or solve for the 15th place (3).

If we need 4 on the 16th loop, then we can have between 4 * 8 and 4 * 8 + 7 on the 15th loop? Ok looks like A can
be 35 or 39 to yield [3, 0]. I'm seeing the trend. I need to write more code.]

If we need 35 or 39 on the 15th loop, then we need (between 35 * 8 and 35 * 8 + 7) OR (between 39 * 8 and 39 * 8 + 7)
on the 14th loop. Ok found 285, 312 and 317.

I'm having integer problems. Getting negative numbers when that should be impossible. I could get away with using int and
long but those are fundamentally signed types and on large longs, I'm hitting negative numbers, probably because of the
division??. Simple fix, just use the right APIs for unsigned longs.

# Misc
We can't quite solve for A like this:

   A / 8^16 = 0

Because the solution is zero. But we can solve for

   A / 8^15 = 1

Which gets us an 'A' that survives even after 15 divisions. It will be divided one more time to get to zero
and then the program ends after 16 loops of the program's instructions.

I'm not really sure how to find all the next 'A' values that work because I'm confused how to do math that
deals with the truncating effect of integer division. But we might not need it.
*/

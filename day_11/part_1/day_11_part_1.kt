package day_11.part_1

import java.nio.file.Path
import kotlin.io.path.readText

const val blinks = 75

// Note: This file is called "part 1" but it also worked for part 2, I just upped the blink count.
fun main() {
    val input = input()

    // A map of engravings to the number of occurrences of this engraving among the stones.
    // For example, the stone series:
    //
    //     0 1 124 124 0
    //
    // Would be the map:
    //     0 -> 2
    //     1 -> 1
    //     124 -> 2
    //
    var groups: Map<Long, Long> = input.groupBy { it.toLong() }.mapValues { it.value.size.toLong() }

    for (i in 0..<blinks) {
        val nGroups = mutableMapOf<Long, Long>()
        for ((eng, occ) in groups.entries) {
            fun merge(i: Long) {
                nGroups.merge(i, occ, Long::plus)
            }

            if (eng == 0L) {
                merge(1L)
                continue
            }

            val engStr = eng.toString()
            if (engStr.length % 2 == 0) {
                val mid = engStr.length / 2
                merge(engStr.take(mid).toLong())
                merge(engStr.takeLast(mid).toLong())
                continue
            }

            merge(eng * 2024L)
        }
        groups = nGroups
    }

    val stones = groups.values.sum()
    println(groups.size)
    println(stones)
}


fun input(): List<Int> {
    val text: List<String> = Path.of(
        if (System.getenv().containsKey("EXAMPLE")) "input/day-11-example.txt" else "input/day-11-full.txt"
    ).readText().trim().split(" ")

    return text.map(String::toInt)
}

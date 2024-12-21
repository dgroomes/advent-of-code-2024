@file:Suppress("DuplicatedCode")

package day_19.part_2

import java.nio.file.Path
import kotlin.io.path.readText

data class Puzzle(val patterns: List<String>, val designs: List<String>)

fun main(vararg args: String) {
    val (patterns, designs) = input(*args)

    val cache = mutableMapOf<String, Long>()

    fun matches(design: String): Long {
        if (design.isEmpty()) return 1L
        cache[design]?.let { return it }

        val matches = patterns
            .filter(design::startsWith)
            .map(design::removePrefix)
            .sumOf(::matches)

        cache[design] = matches
        return matches
    }

    println(designs.sumOf(::matches))
}

fun input(vararg args: String): Puzzle {
    val variation = args.getOrElse(0) { "full" }
    val (top, bottom) = Path.of("input/day-19-$variation.txt").readText().trim().split("\n\n")
    val patterns = top.split(", ")
    val designs = bottom.lines();
    return Puzzle(patterns, designs)
}

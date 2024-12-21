@file:Suppress("DuplicatedCode")

package day_19.part_1

import java.nio.file.Path
import kotlin.io.path.readText

data class Puzzle(val patterns: List<String>, val designs: List<String>)

fun main(vararg args: String) {
    val (patterns, designs) = input(*args)

    val cache = mutableMapOf<String, Boolean>()

    fun matches(design: String): Boolean {
        val c = cache[design]
        if (c != null) return c

        val m = patterns.any {
            design.isEmpty() || design.startsWith(it) && matches(design.removePrefix(it))
        }

        cache[design] = m
        return m
    }

    println(designs.count(::matches))
}

fun input(vararg args: String): Puzzle {
    val variation = args.getOrElse(0) { "full" }
    val (top, bottom) = Path.of("input/day-19-$variation.txt").readText().trim().split("\n\n")
    val patterns = top.split(", ")
    val designs = bottom.lines();
    return Puzzle(patterns, designs)
}

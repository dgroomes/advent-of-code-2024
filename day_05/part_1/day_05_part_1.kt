package day_05.part_1

import java.nio.file.Path
import kotlin.io.path.readText

data class Rule(val before: Int, val after: Int)
data class Instructions(val rules: List<Rule>, val updates: List<List<Int>>)

fun main() {
    val (rules, updates) = instructions()

    var sum = 0
    updates@ for (pages in updates) {
        rule@ for ((before, after) in rules) {
            var befEncount = false
            var aftEncount = false
            for (page in pages) {
                if (page == after) {
                    if (befEncount) continue@rule
                    aftEncount = true
                } else if (page == before) {
                    if (aftEncount) continue@updates // This is a bad update. We encountered the "after" page earlier.
                    befEncount = true
                }
            }
        }
        sum+=  pages[pages.size / 2]
    }

    println(sum)
}

private fun instructions(): Instructions {
    val text = Path.of(
        if (System.getenv().containsKey("EXAMPLE")) "input/day-05-example.txt" else "input/day-05-full.txt"
    ).readText().trimEnd()

    val textSplits = text.split("\n\n")
    val rules = textSplits[0].lines().map {
        val splits = it.split("|")
        Rule(splits[0].toInt(), splits[1].toInt())
    }
    val updates: List<List<Int>> = textSplits[1].lines().map { it.split(",").map { it.toInt() } }
    return Instructions(rules, updates)
}

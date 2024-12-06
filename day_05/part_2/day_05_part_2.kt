package day_05.part_2

import java.nio.file.Path
import kotlin.io.path.readText

data class Rule(val before: Int, val after: Int)
data class Instructions(val rules: List<Rule>, val updates: List<List<Int>>)

fun main() {
    val (rules, updates) = instructions()

    var sum = 0
    for (update in updates) {
        val pages = update.toSet()
        val inScopeRules = rules.filter { it.before in pages && it.after in pages }
        val sorted = outline(inScopeRules)

        if (sorted != update) sum+=  sorted[sorted.size / 2]
    }
    println(sum)
}

/**
 * Sort the numbers represented in the rules into an ordered list that satisfies each rules.
 */
fun outline(rules: List<Rule>) : List<Int> {
    val outline = mutableListOf<Int>()
    val items = rules.flatMap { listOf(it.before, it.after) }.toMutableSet()
    val r = rules.toMutableSet()
    while (r.isNotEmpty()) {
        val constrainedItems = r.map { it.after }.toSet()
        val bottomedRules = r.filter {  it.before !in constrainedItems }.toSet()
        if (bottomedRules.isEmpty()) throw IllegalStateException("No rules bottomed out. The algorithm can't make progress.")
        r.removeAll(bottomedRules)
        val leaders = bottomedRules.map { it.before }.distinct()
        leaders.forEach(items::remove)
        leaders.forEach(outline::add)
    }

    items.forEach(outline::add)
    return outline
}

fun instructions(): Instructions {
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

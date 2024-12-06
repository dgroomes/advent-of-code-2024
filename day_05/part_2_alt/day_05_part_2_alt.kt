package day_05.part_2_alt

import java.nio.file.Path
import kotlin.io.path.readText

data class Rule(val leader: Int, val follower: Int)
data class Instructions(val rules: List<Rule>, val updates: List<List<Int>>)
class Node(val number: Int) {
    val followers: MutableSet<Node> = mutableSetOf()
    val leaders: MutableSet<Node> = mutableSetOf()
}

fun main() {
    val (rules, updates) = instructions()

    var sum = 0
    for (update in updates) {
        val pages = update.toSet()
        val inScopeRules = rules.filter { it.leader in pages && it.follower in pages }
        val sorted = sort(inScopeRules)

        if (sorted != update) sum += sorted[sorted.size / 2]
    }
    println(sum)
}

/**
 * Sort the numbers represented in the rules into an ordered list that satisfies each rules.
 */
fun sort(rules: List<Rule>): List<Int> {
    val nodes = mutableMapOf<Int, Node>()

    val leaders = mutableSetOf<Node>()
    for (rule in rules) {
        val leader = nodes.computeIfAbsent(rule.leader) { Node(it).also(leaders::add) }
        val follower = nodes.computeIfAbsent(rule.follower, ::Node).also(leaders::remove)

        leader.followers.add(follower)
        follower.leaders.add(leader)
    }

    val sorted = mutableListOf<Int>()
    while (leaders.isNotEmpty()) {
        val l = leaders.first()
        leaders.remove(l)
        sorted.add(l.number)
        l.followers.forEach {
            it.leaders.remove(l)
            if (it.leaders.isEmpty()) leaders.add(it)
        }
    }

    return sorted
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

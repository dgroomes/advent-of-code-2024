package day_09.part_2_alt

import java.nio.file.Path
import java.util.LinkedList
import kotlin.io.path.readText

sealed interface Segment {
    val id: Int
    val length: Int
}

data class File(override val id: Int, override val length: Int) : Segment {
    override fun toString() = "f$id/$length"
}

data class Blank(override val id: Int, override val length: Int) : Segment {
    override fun toString() = "b$id/$length"
}

fun main() {
    val input = input()

    val disk = LinkedList<Segment>()

    for ((index, segments) in input.chunked(2).withIndex()) {
        val length = segments[0]
        disk.add(File(index, length))

        if (segments.size == 1) continue

        segments.getOrNull(1)?.let {
            disk.add(Blank(index, it))
        }
    }

    val ddisk = defrag(disk)
    println(checkSum(ddisk))
}

fun defrag(disk: List<Segment>): List<Segment> {
    class Node(
        var segment: Segment,
        var leader: Node? = null,
        var follower: Node? = null
    ) {
        override fun toString() = "Node(segment=$segment)"
    }

    val nodes = disk.map(::Node)
    nodes.windowed(size = 2, step = 1).forEach {
        val (l, r) = it
        l.follower = r
        r.leader = l
    }

    fun listify(): List<Segment> {
        val list = mutableListOf<Segment>()
        var l: Node? = nodes.first()
        while (l != null) {
            list.add(l.segment)
            l = l.follower
        }

        return list
    }

    // Queues of blank segments that are available to be occupied.
    val blanks = nodes
        .filter { it.segment is Blank }
        .groupBy { it.segment.length }
        .mapValues { LinkedList(it.value) }
        .toMutableMap()

    val solidified = mutableSetOf<Int>()
    var right: Node = nodes.last()
    while (true) {
//        println(listify())
//        blanks.forEach(::println)
        val leader = right.leader ?: break

        val rightSeg = right.segment
        if (rightSeg is Blank) {
            right = leader
            solidified.add(rightSeg.id)
            continue
        }

        val file = right.segment

        val queueEntry = blanks.entries
            .filter { it.key >= file.length }
            .minByOrNull { it.value.first().segment.id }

        if (queueEntry == null) {
            // The file is too large for any available space.
            right = leader
            continue
        }

        val key = queueEntry.key
        val queue = queueEntry.value
        val left = queue.removeFirst()

        if (queue.isEmpty()) {
            blanks.remove(key)
        }

        if (left.segment.id in solidified) {
            continue
        }

        val blank = left.segment
        right.segment = Blank(-1, file.length)
        left.segment = file

        val diff = blank.length - file.length
        if (diff == 0) {
            right = leader
            continue
        }

        // The file slots in but leaves more space. We need to splice in a new, smaller, blank segment.
        val adj = left.follower
        val nBlank = Node(Blank(blank.id, diff))
        left.follower = nBlank
        nBlank.leader = left
        nBlank.follower = adj
        adj?.leader = nBlank

        // Re-rank the shrunken blank segment
        val queue2 = blanks.getOrPut(diff, ::LinkedList)

        // Darn... this is where the algorithm breaks down. I wanted to search the already sorted list (o(log n)) and
        // then insert into it (o(1)). I can't search a linked list in o(log n). But if I use an array list, I can't
        // insert into it in o(1). So I think I need a red-black tree or jump points or something but at this point
        // I give up. At least satisfied that I persevered this long.
        val i = queue2.indexOfFirst { it.segment.id > blank.id }
        if (i == -1) {
            queue2.addLast(nBlank)
        } else {
            queue2.add(i, nBlank)
        }
        right = leader
    }

    val ddisk = listify()
    return ddisk
}

fun checkSum(disk: List<Segment>): Long {
    var pos = 0
    var sum = 0L
    for (segment in disk) {
        if (segment is Blank) {
            pos += segment.length
            continue
        }

        val id = segment.id
        for (i in 0 until segment.length) {
            sum += pos * id
            pos++
        }
    }

    return sum
}

fun input(): List<Int> {
    val text = Path.of(
        if (System.getenv().containsKey("EXAMPLE")) "input/day-09-example.txt" else "input/day-09-full.txt"
    ).readText().trim()

    return text.toList().map(Char::digitToInt)
}

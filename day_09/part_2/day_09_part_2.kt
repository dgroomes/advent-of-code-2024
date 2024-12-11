package day_09.part_2

import java.nio.file.Path
import java.util.LinkedList
import kotlin.io.path.readText

sealed interface Segment {
    val length: Int
}

data class File(val id: Int, override val length: Int) : Segment {
    override fun toString() = "f$id/$length"
}

data class Blank(override val length: Int) : Segment {
    override fun toString() = "b/$length"
}

/**
 * THIS DOES NOT WORK. I interpreted the algorithm wrong despite spending so much time on this problem. Woops!
 */
fun main() {
    val input = input()

    val disk = LinkedList<Segment>()

    for ((index, segments) in input.chunked(2).withIndex()) {
        val file = segments[0]
        disk.add(File(index, file))
        if (segments.size == 1) continue

        val blank = segments[1]
        disk.add(Blank(blank))
    }

    println(disk)
    val ddisk = defrag(disk)
    println(ddisk)
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

    class Ranked(val node: Node, val rank: Int)

    // Queues of file segments that need to be compacted into blank spaces (if possible).
    val work = nodes.reversed()
        .filter { it.segment is File }
        .mapIndexed { idx, it -> Ranked(it, idx) }
        .groupBy { it.node.segment.length }
        .mapValues { ArrayDeque(it.value) }
        .toMutableMap()

    val complete = mutableSetOf<Int>()
    var left: Node = nodes.first()
    while (true) {
        val follower = left.follower ?: break

        val leftSeg = left.segment
        if (leftSeg is File) {
            left = follower
            complete.add(leftSeg.id)
            continue
        }

        val blank = left.segment

        val queueEntry = work.entries
            .filter { it.key <= blank.length }
            .minByOrNull { it.value.first().rank }

        if (queueEntry == null) {
            // The space is too small. No file segments can fit.
            left = follower
            continue
        }

        val value = queueEntry.value
        val right = value.removeFirst().node

        if (queueEntry.value.isEmpty()) {
            work.remove(queueEntry.key)
        }

        if ((right.segment as File).id in complete) {
            continue
        }

        val file = right.segment

        val diff = blank.length - file.length
        if (diff == 0) {
            // A perfect fit.
            left.segment = file
            right.segment = blank
        } else {
            // The file slots in but leaves more space. We need to splice in a new, smaller, blank segment.
            left.segment = file
            val ahead = left.follower
            val n = Node(Blank(diff))
            left.follower = n
            n.leader = left
            n.follower = ahead
            right.segment = Blank(file.length)
        }

        // The right side is newly vacant. Merge the blank space with blank space that might exist to either side.
        val blankL = right.leader!!
        val blankF = right.follower
        val blankN = if (blankL.segment is Blank) {
            blankL.segment = Blank(blankL.segment.length + right.segment.length)
            blankL.follower = blankF
            blankF?.leader = blankL
            blankL
        } else {
            right
        }

        if (blankF != null && blankF.segment is Blank) {
            blankN.segment = Blank(blankN.segment.length + blankF.segment.length)
            blankN.follower = blankF.follower
            blankF.follower?.leader = blankN
        }
    }

    val ddisk = mutableListOf<Segment>()
    var l: Node? = nodes.first()
    while (l != null) {
        ddisk.add(l.segment)
        l = l.follower
    }

    return ddisk
}

fun checkSum(disk: List<Segment>): Int {
    TODO()
}

fun input(): List<Int> {
    val text = Path.of(
        if (System.getenv().containsKey("EXAMPLE")) "input/day-09-example.txt" else "input/day-09-full.txt"
    ).readText().trim()

    return text.toList().map(Char::digitToInt)
}

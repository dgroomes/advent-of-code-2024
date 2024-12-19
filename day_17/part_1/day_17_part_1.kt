@file:Suppress("DuplicatedCode")

package day_17.part_1

import java.math.BigInteger

fun main() {
    val insts = input()

    var a = 0
    var b = 0
    var c = 0
    var ip = 0
    val output = mutableListOf<Int>()

    fun lit(): Int {
        return insts[ip + 1]
    }

    fun combo(): Int {
        val lit = insts[ip + 1]
        val combo = when (lit) {
            0, 1, 2, 3 -> lit
            4 -> a
            5 -> b
            6 -> c
            7 -> throw IllegalStateException("7 is reserved. Illegal")
            else -> throw IllegalStateException("$lit not a legal operand")
        }
//        println("[combo] $lit yield $combo")
        return combo
    }

    fun adv() {
        val oprnd = combo()
        val den = BigInteger.valueOf(2).pow(oprnd)
//        println("[adv] Dividing $a by $den")
        a /= den.toInt()
    }

    fun bxl() {
        b = b xor lit()
    }

    fun bst() {
        b = combo() % 8
    }

    fun jnz() {
        if (a == 0) return else ip = lit() - 2
    }

    fun bxc() {
        b = b xor c
    }

    fun out() {
        output.add(combo() % 8)
    }

    fun bdv() {
        val oprnd = combo()
        val den = BigInteger.valueOf(2).pow(oprnd)
        b = a / den.toInt()
    }

    fun cdv() {
        val oprnd = combo()
        val den = BigInteger.valueOf(2).pow(oprnd)
        c = a / den.toInt()
    }

    fun printState() {
        println("""
        a: $a
        b: $b
        c: $c
        output: $output
        ip: $ip
        
        """.trimIndent())
    }

    while (ip < insts.size) {
        val inst = insts[ip]
//        println("Running instruction $inst at pointer $ip ...")
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

    println(output.joinToString(","))
}

fun input(): List<Int> {
    TODO("omitted")
}

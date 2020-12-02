package com.ae.aoc2019.day02

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.BufferedReader
import java.io.InputStreamReader


@SpringBootApplication
class Aoc2020ApplicationDay02 : CommandLineRunner {

	private val logger = LoggerFactory.getLogger(javaClass)

	data class Entry(val lb: Int,
									 val up: Int,
									 val char: Char,
									 val password: String) {
		fun valid() : Boolean {
			val amount = password.toCharArray().filter { it == char }.count()
			return amount in lb..up
		}

		fun valid2() : Boolean {
			val left = password.toCharArray()[lb - 1]
			val right = password.toCharArray()[up - 1]
			return (left == char).xor(right == char)
		}
	}

	private fun line2Entry(line: String) : Entry {
		val regex = Regex("(\\d+)-(\\d+) (\\w): (\\w+)") //2-9 c: ccccccccc
		val (lb, up, char, password) =  regex.find(line)!!.destructured
		return Entry(lb.toInt(), up.toInt(), char.toCharArray().first(), password)
	}

	private fun solve1(totalFile: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay02::class.java.getResourceAsStream("/$totalFile")))

		bufferedReader.useLines {
			val passwords = it.map { line2Entry(it) }.toList()
			logger.info("According to the policies, ${passwords.filter { it.valid() }.count()} are valid")
		}
	}

	private fun solve2(totalFile: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay02::class.java.getResourceAsStream("/$totalFile")))

		bufferedReader.useLines {
			val passwords = it.map { line2Entry(it) }.toList()
			logger.info("According to the new policies, ${passwords.filter { it.valid2() }.count()} are valid")
		}
	}

	override fun run(vararg args: String?) {
		solve1(args[0]!!)
		solve2(args[0]!!)
	}
}

fun main(args: Array<String>) {
	runApplication<Aoc2020ApplicationDay02>(*args)
}

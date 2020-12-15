package com.ae.aoc2020.day15

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

@SpringBootApplication
class Aoc2020ApplicationDay15 : CommandLineRunner {

	private val logger = LoggerFactory.getLogger(javaClass)

	private fun play(numbers: List<Int>) : Int {
		val spokenNumbers = HashMap<Int, Set<Int>>()
		var turn  = 1
		var spoken = -1
		numbers.forEach {
			spoken = it
			spokenNumbers[spoken] = spokenNumbers.getOrDefault(spoken, emptySet()).plus(turn)
			turn++
		}
		while (turn <= 2020) {
			val timesSpokenBefore = spokenNumbers[spoken]!!.filter { it != (turn - 1) }
			spoken = if (timesSpokenBefore.isEmpty()) 0
			else (turn - 1) - timesSpokenBefore.max()!!
			spokenNumbers[spoken] = spokenNumbers.getOrDefault(spoken, emptySet()).plus(turn)
			turn++
		}
		return spoken
	}

	private fun playQuick(numbers: List<Int>) : Int {
		val spoken2turn = HashMap<Int, Int>() // Times, Turn
		var turn  = 1
		var spoken = -1
		numbers.forEach {
			if (turn > 1) {
				spoken2turn[spoken] = turn - 1
			}
			spoken = it
			turn++
		}
		while (turn <= 30000000) {
			val spoken_ = if (spoken2turn.containsKey(spoken)) {
				            (turn - 1) - spoken2turn[spoken]!!
			              } else {
			               	0
						  }
			spoken2turn[spoken] = turn - 1
			spoken = spoken_
			turn++
		}
		return spoken
	}

	private fun solve1(totalFile: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay15::class.java.getResourceAsStream("/$totalFile")))
		bufferedReader.useLines { input ->
			input.forEach { line ->
				logger.info("${play(line.split(",").map { it.toInt() })} will be the 2020th number spoken")
			}
		}
	}

	
	private fun solve2(totalFile: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay15::class.java.getResourceAsStream("/$totalFile")))
		bufferedReader.useLines { input ->
			input.forEach { line ->
				logger.info("${playQuick(line.split(",").map { it.toInt() })} will be the 30000000th number spoken")
			}
		}
	}

	override fun run(vararg args: String?) {
		solve1(args[0]!!)
		solve2(args[0]!!)
	}
}

fun main(args: Array<String>) {
	runApplication<Aoc2020ApplicationDay15>(*args)
}

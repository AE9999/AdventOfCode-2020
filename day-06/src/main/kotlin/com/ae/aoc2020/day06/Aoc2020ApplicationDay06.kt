package com.ae.aoc2020.day06

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.BufferedReader
import java.io.InputStreamReader


@SpringBootApplication
class Aoc2020ApplicationDay06 : CommandLineRunner {

	private val logger = LoggerFactory.getLogger(javaClass)

	data class GroupAnswers(val personalAnswers: List<String>) {
		fun countToYes() : Int =
			personalAnswers.map { it.toCharArray().toSet() }.reduce { acc, set -> acc.plus(set)  }.size

		fun countToAllYes(): Int =
			personalAnswers.map { it.toCharArray().toSet() }.reduce { acc, set -> acc.intersect(set)  }.size
	}

	private fun readGroupAnswers(it: Sequence<String>): List<GroupAnswers> {
		var currentAnswers = ArrayList<String>()
		val groupAnswers = ArrayList<GroupAnswers>()
		it.forEach {line ->
			if (line.isNullOrEmpty()) {
				groupAnswers.add(GroupAnswers(currentAnswers))
				currentAnswers = ArrayList()
			}
			else {
				currentAnswers.add(line)
			}
		}
		if (!currentAnswers.isEmpty()) { // don't forget trailing one
			groupAnswers.add(GroupAnswers(currentAnswers))
		}
		return groupAnswers
	}

	private fun solve1(totalFile: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay06::class.java.getResourceAsStream("/$totalFile")))
		bufferedReader.useLines {
			val sum = readGroupAnswers(it).onEach {
				logger.info("Group $it has ${it.countToYes()} answers ..")
			}.sumBy { it.countToYes() }
			logger.info("A total sum of $sum ..")

		}
	}

	private fun solve2(totalFile: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay06::class.java.getResourceAsStream("/$totalFile")))
		bufferedReader.useLines {
			val sum = readGroupAnswers(it).sumBy { it.countToAllYes() }
			logger.info("A total sum of (all) $sum ..")
		}
	}

	override fun run(vararg args: String?) {
		solve1(args[0]!!)
		solve2(args[0]!!)
	}
}

fun main(args: Array<String>) {
	runApplication<Aoc2020ApplicationDay06>(*args)
}

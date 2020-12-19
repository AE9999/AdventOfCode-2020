package com.ae.aoc2020.day19

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.RuntimeException

@SpringBootApplication
class Aoc2020ApplicationDay19 : CommandLineRunner {

	private val logger = LoggerFactory.getLogger(javaClass)

	class System (val id2terminal : Map<Int, Char>,
				  val id2rules: Map<Int, List<List<Int>>>)  {

		val acceptableWords: Set<String>

		init {
			acceptableWords = expandRules(listOf(0))
		}

		fun expandRules(rules: List<Int>) : Set<String> {
			val nonTerminalPositions = rules.withIndex().filter { !id2terminal.containsKey(it.value) }.toList()
			if (nonTerminalPositions.isEmpty()) {
				return setOf(rules.map { id2terminal[it] }.joinToString(""))
			}
			val indexToExpand = nonTerminalPositions.first().index
			val expansions = id2rules[nonTerminalPositions.first().value]!!

			val rvalue = HashSet<String>()
			expansions.map { extension ->
				val head  = if (indexToExpand > 0) rules.take(indexToExpand) else emptyList()
				val middle = extension
				val tail =  if (indexToExpand < rules.size - 1) rules.drop(indexToExpand + 1)
					            else emptyList()
				head + middle + tail
			}.forEach {
				rvalue.addAll(expandRules(it))
			}
			return rvalue
		}

		fun matchesRule0(message: String): Boolean {
			return acceptableWords.contains(message)
		}
	}

	fun parseRule(systemInput: List<String>, fast: Boolean) : System {
		val terminalRegex = Regex("(\\d+): \"(\\w)\"")
		val idRegex = Regex("(\\d+):")

		val id2terminal = systemInput.filter { line -> terminalRegex.matches(line) }
				                     .map { line ->
			                                val (id, terminal) = terminalRegex.find(line)!!.destructured
			                                Pair(id.toInt(), terminal[0])
		                                  }.toMap()
		val id2rules = systemInput.filterNot { line -> terminalRegex.matches(line) }
								   .map { line ->
									val split = line.split(" ")
									val (id) = idRegex.find(split[0])!!.destructured
									val rulMatches = split.drop(1)
													      .joinToString( " ")
														  .split("|")
														  .map {
															  it.trim().split(" ").map { it.toInt() }
														  }
									Pair(id.toInt(), rulMatches)
								   }.toMap().toMutableMap()

		return System(id2terminal, id2rules)
	}

	private fun solve1(file: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay19::class.java.getResourceAsStream("/$file")))
		bufferedReader.useLines { input ->
			val storedInput = input.toList()
			val systemInput =  storedInput.filter { line -> Regex("^\\d+:").find(line) != null }
			val system = parseRule(systemInput, false)
			val messages = storedInput.filter { line ->
				Regex("^\\d+:").find(line) == null && !line.isNullOrEmpty()
			}
			logger.info("${messages.filter { system.matchesRule0(it) }.size} many messages completely match rule 0")
		}
	}

	private fun solve2(file: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay19::class.java.getResourceAsStream("/$file")))
		bufferedReader.useLines { input ->
			val storedInput = input.toList()
			val systemInput =  storedInput.filter { line -> Regex("^\\d+:").find(line) != null }
			val system = parseRule(systemInput, true)
			val messages = storedInput.filter { line ->
				Regex("^\\d+:").find(line) == null && !line.isNullOrEmpty()
			}
		}
	}

	override fun run(vararg args: String?) {
//		solve1(args[0]!!)
		solve2(args[0]!!)
	}
}

fun main(args: Array<String>) {
	runApplication<Aoc2020ApplicationDay19>(*args)
}

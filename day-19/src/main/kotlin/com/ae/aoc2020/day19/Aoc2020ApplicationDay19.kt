package com.ae.aoc2020.day19

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.BufferedReader
import java.io.InputStreamReader

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

	inner class RecurrentSystem(val id2terminal : MutableMap<Int, Char>,
						  val id2rules: MutableMap<Int, List<List<Int>>>) {

		val reachableFromRecurring = HashMap<Int, Set<String>>()

		val calculatedStuff: Set<List<Int>>

		val lenghtOf: Map<Int,Int>

		init {
			val reachableFrom8 = naiveExpansion(listOf(8)).map {
				it.map { id2terminal[it]!! }.joinToString("")
			}.toSet()
			val reachableFrom42 = naiveExpansion(listOf(42)).map {
				it.map { id2terminal[it]!! }.joinToString("")
			}.toSet()
			val reachableFrom31 = naiveExpansion(listOf(31)).map {
				it.map { id2terminal[it]!! }.joinToString("")
			}.toSet()
			// Hack to not to have to re-write naiveExpansion
			reachableFromRecurring[8] = reachableFrom8
			reachableFromRecurring[31] = reachableFrom31
			reachableFromRecurring[42] = reachableFrom42

			lenghtOf = listOf(8, 31, 42).map { Pair(it, reachableFromRecurring[it]!!.map { it.length }.min()!!) }
					                          .toMap()
			calculatedStuff = naiveExpansion(listOf(0)).toSet()

			id2rules[8] = listOf(listOf(42), listOf(42, 8))
			id2rules[11] = listOf(listOf(42,31), listOf(42, 11, 31))
		}

		fun naiveExpansion(rules: List<Int>) : Set<List<Int>> {
			val nonTerminalPositions = rules.withIndex().filter {
				!id2terminal.containsKey(it.value)
				&& !reachableFromRecurring.containsKey(it.value)
			}.toList()

			if (nonTerminalPositions.isEmpty()) {
				return setOf(rules)
			}
			val indexToExpand = nonTerminalPositions.first().index
			val expansions = id2rules[nonTerminalPositions.first().value]!!

			val rvalue = HashSet<List<Int>>()
			expansions.map { extension ->
				val head  = if (indexToExpand > 0) rules.take(indexToExpand) else emptyList()
				val middle = extension
				val tail =  if (indexToExpand < rules.size - 1) rules.drop(indexToExpand + 1)
				else emptyList()
				head + middle + tail
			}.forEach {
				rvalue.addAll(naiveExpansion(it))
			}
			return rvalue
		}

		fun matchesRule0(message: String): Boolean {
			//
			// According to the debugger we work with 8,11
			// that rewrites to 42+ (42, 31)+
			// So numbers of 42 is at least 1 highter than number of 31s
			//
			val lenght11 = (lenghtOf[42]!! + lenghtOf[31]!!)
			val max31s = (message.length - lenghtOf[42]!!) / lenght11
			(1..max31s).forEach { times31 ->
				val maxAdditional42s = (message.length - (times31 * lenght11)) / lenghtOf[42]!!
				val max42s = max31s + maxAdditional42s
				(2..max42s).forEach lit@ { times42 ->
					if ((times31 * lenghtOf[31]!!) + times42 * lenghtOf[42]!! != message.length) {
						// impossible to construct candidate with these numbers
						return@lit
					}
					(0 until times42).forEach { n42 ->
						val currentIndex = (n42 * lenghtOf[42]!!)
						val currentCandidate = message.drop(currentIndex).take(lenghtOf[42]!!)
						if (!reachableFromRecurring[42]!!.contains(currentCandidate)) {
							// can no longer construct candidate
							return@lit
						}
					}

					(0 until times31).forEach { n31 ->
						val currentIndex = (times42 *  lenghtOf[42]!!) + (n31 * lenghtOf[31]!!)
						val currentCandidate = message.drop(currentIndex).take(lenghtOf[31]!!)
						if (!reachableFromRecurring[31]!!.contains(currentCandidate)) {
							// can no longer construct candidate
							return@lit
						}
					}

					return true // We've constructed the message
				}
			}
			return false
		}
	}

	fun parseRule(systemInput: List<String>, fast: Boolean) : Pair<Map<Int, Char>, MutableMap<Int, List<List<Int>>>> {
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

		return Pair(id2terminal, id2rules)
	}

	private fun solve1(file: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay19::class.java.getResourceAsStream("/$file")))
		bufferedReader.useLines { input ->
			val storedInput = input.toList()
			val systemInput =  storedInput.filter { line -> Regex("^\\d+:").find(line) != null }
			val stuff =  parseRule(systemInput, true)
			val system = System(stuff.first, stuff.second)
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
			val stuff =  parseRule(systemInput, true)
			val system = RecurrentSystem(stuff.first.toMutableMap(), stuff.second)
			val messages = storedInput.filter { line ->
				Regex("^\\d+:").find(line) == null && !line.isNullOrEmpty()
			}
			logger.info("${messages.filter { system.matchesRule0(it) }.size} many messages completely match rule 0")
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

package com.ae.aoc2020.day07

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.BufferedReader
import java.io.InputStreamReader


@SpringBootApplication
class Aoc2020ApplicationDay07 : CommandLineRunner {

	private val logger = LoggerFactory.getLogger(javaClass)

	data class BagRequirement(val id: Pair<String, String>,
							  val amount: Int)

	data class Bag(val id: Pair<String, String>,
				   val bagRequirements: Set<BagRequirement>) {

		fun containsShinyGold(id2Bag: Map<Pair<String,String>, Bag>) : Int {
			if (id == Pair("shiny", "gold")) return 1;
			bagRequirements.forEach { bagRequirement ->
				if (id2Bag[bagRequirement.id]!!.containsShinyGold(id2Bag) > 0) {
					return 1
				}
			}
			return 0
		}

		fun bagsNeededInsideMe(id2Bag: Map<Pair<String,String>, Bag>) : Int {
			return bagRequirements.map { bagRequirement ->
				(bagRequirement.amount * (1 + id2Bag[bagRequirement.id]!!.bagsNeededInsideMe(id2Bag)))
			}.sum()
		}
	}

	private fun parseBagLine(line: String) : Bag {
		val idMatch = Regex("(\\w+) (\\w+) bags contain").find(line)
		val (left, right) = idMatch!!.destructured
		val id = Pair(left, right)
		val rulesLine = line.drop("$left $right bags contain".length)
		val rulesRegex = Regex(" (\\d+) (\\w+) (\\w+) bags?[,.]")
		val bagEntries = rulesRegex.findAll(rulesLine).map {
			val (amount, left, right) = it.destructured
			BagRequirement(Pair(left, right), amount.toInt())
		}.toSet()
		return Bag(id, bagEntries)
	}

	private fun solve1(totalFile: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay07::class.java.getResourceAsStream("/$totalFile")))
		bufferedReader.useLines {
			val bags = it.map { line -> parseBagLine(line) }.toList()
			val id2Bag = bags.map { bag -> Pair(bag.id, bag) }.toMap()
			val sum = bags.filter { it.id != Pair("shiny", "gold") } // We need to exclude itself.
					      .map { bag -> bag.containsShinyGold(id2Bag) }.sum()
			logger.info("$sum bag colors can eventually contain at least one shiny gold bag")
		}
	}

	private fun solve2(totalFile: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay07::class.java.getResourceAsStream("/$totalFile")))
		bufferedReader.useLines {
			val bags = it.map { line -> parseBagLine(line) }.toList()
			val id2Bag = bags.map { bag -> Pair(bag.id, bag) }.toMap()
			val sum = bags.filter { it.id == Pair("shiny", "gold") } // We only need the answer for this one
					      .map { bag -> bag.bagsNeededInsideMe(id2Bag) }
					      .sum()
			logger.info("$sum individual bags are required inside your single shiny gold bag")
		}
	}

	override fun run(vararg args: String?) {
		solve1(args[0]!!)
		solve2(args[0]!!)
	}
}

fun main(args: Array<String>) {
	runApplication<Aoc2020ApplicationDay07>(*args)
}

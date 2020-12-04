package com.ae.aoc2019.day04

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.BufferedReader
import java.io.InputStreamReader


@SpringBootApplication
class Aoc2020ApplicationDay04 : CommandLineRunner {

	private val logger = LoggerFactory.getLogger(javaClass)

	/*byr
	iyr
	eyr
	hgt
	hcl
	ecl
	pid
	cid*/
	class Passport(val map: Map<String, String>) {
		fun valid() : Boolean {
			val requiredIs = listOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid")
			return requiredIs.map { id -> if (map.containsKey(id)) 1 else 0  }.sum() == requiredIs.size
		}

		fun valid2() : Boolean {
			val requiredIs = listOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid")
			requiredIs.forEach {key ->
				val value = map[key]!!
				val validInput: Boolean = when (key) {
					"byr" -> Regex("\\d{4}").matchEntire(value) != null && value.toInt() in 1920..2002
					"iyr" -> Regex("\\d{4}").matchEntire(value) != null && value.toInt() in 2010..2020
					"eyr" -> Regex("\\d{4}").matchEntire(value) != null && value.toInt() in 2020..2030
					"hgt" ->
						when {
							Regex("(\\d+)cm").matchEntire(value) != null -> {
								val (value: String) = Regex("(\\d+)cm").matchEntire(value)!!.destructured
								value.toInt() in 150..193
							}
							Regex("(\\d+)in").matchEntire(value) != null -> {
								val (value: String) = Regex("(\\d+)in").matchEntire(value)!!.destructured
								value.toInt() in 59..76
							}
							else -> false
						}
					"hcl" -> Regex("#[0-9a-f]{6}").matchEntire(value) != null
					"ecl" -> listOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth").contains(value)
					"pid" -> Regex("\\d{9}").matchEntire(value) != null
					"cid" -> true
					else -> true
				}
				if (!validInput) {
					return false
				}
			}
			return true
		}
	}

	private fun readPassports(it: Sequence<String>): List<Passport> {
		var currentMap = HashMap<String, String>()
		val passports = ArrayList<Passport>()
		it.forEach {line ->
			if (line.isNullOrEmpty()) {
				passports.add(Passport(currentMap))
				currentMap = HashMap()
			}
			else {
				val regex = Regex("(\\S+):(\\S+)")
				regex.findAll(line).forEach { matchResult ->
					val (key, value) =  matchResult.destructured
					currentMap[key] = value
				}
			}
		}
		if (!currentMap.isEmpty()) { // don't forget trailing one
			passports.add(Passport(currentMap))
		}
		return passports
	}

	private fun solve1(totalFile: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay04::class.java.getResourceAsStream("/$totalFile")))
		bufferedReader.useLines {
			 val passports  = readPassports(it)
			  logger.info("My file contains ${passports.filter { it.valid() }.size} valid passports")
		}
	}

	private fun solve2(totalFile: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay04::class.java.getResourceAsStream("/$totalFile")))
		bufferedReader.useLines {
			val passports  = readPassports(it)
			logger.info("My file contains ${passports.filter { it.valid() && it.valid2() }.size} valid passports with valid values")
		}
	}


	override fun run(vararg args: String?) {
		solve1(args[0]!!)
		solve2(args[0]!!)
	}
}

fun main(args: Array<String>) {
	runApplication<Aoc2020ApplicationDay04>(*args)
}

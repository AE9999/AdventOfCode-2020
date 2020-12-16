package com.ae.aoc2020.day16

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

@SpringBootApplication
class Aoc2020ApplicationDay16 : CommandLineRunner {

	private val logger = LoggerFactory.getLogger(javaClass)

	data class Ticket(val ints: List<Int>)

	data class Attribute(val name: String, val options: List<IntRange>) {
		fun hasValidOption(value: Int) : Boolean {
			options.forEach { option ->  if (value in option ) return true }
			return false
		}
	}

	class Station(val attibutes: List<Attribute>,
	              val myTicket: Ticket,
	              val nearbyTickets: List<Ticket>) {

		fun validAttrributes(value: Int) : List<Attribute> {
			return attibutes.filter { attribute -> attribute.hasValidOption(value) }
		}

		fun findInvalidValues(): List<Int> {
			val invalidValues = ArrayList<Int>()
			nearbyTickets.forEach {
				ticket ->
				ticket.ints.forEach { value ->
					if (validAttrributes(value).isEmpty()) {
						invalidValues.add(value)
					}
				}
			}
			return invalidValues
		}
	}

	fun parseAttribute(line: String) : Attribute {
		val split = line.split(":")
		val name = split[0]
		val options =split[1]. split("or").map { it.trim() }.map {
			val s = it.split("-")
			s[0].toInt()..s[1].toInt()
		}
		return Attribute(name, 	options)
	}

	fun parseStation(input: List<String>) : Station {

		val attibutes = input.subList(0, input.indexOf("")).map {
			 parseAttribute(it)
		}

		val myTicket =  Ticket(input.listIterator(input.indexOf("your ticket:") + 1).next().split(",").map { it.toInt() })

		val nearbyTickets = input.drop(input.indexOf("nearby tickets:") + 1).map { Ticket(it.split(",").map { it.toInt() }) }

		return Station(attibutes, myTicket, nearbyTickets)
	}


	private fun solve1(totalFile: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay16::class.java.getResourceAsStream("/$totalFile")))
		bufferedReader.useLines { input ->
			val station = parseStation(input.toList())
			logger.info("${station.findInvalidValues().sum()} is your ticket scanning error rate")
		}
	}

	
	private fun solve2(totalFile: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay16::class.java.getResourceAsStream("/$totalFile")))
		bufferedReader.useLines { input ->
		}
	}

	override fun run(vararg args: String?) {
		solve1(args[0]!!) // 15063 too low
		solve2(args[0]!!)
	}
}

fun main(args: Array<String>) {
	runApplication<Aoc2020ApplicationDay16>(*args)
}

package com.ae.aoc2020.day16

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.RuntimeException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
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

		fun fitsAttribute(values: List<Int>): Boolean {
			return values.filter { value -> options.filter {  option -> value in option }.isNotEmpty() }.size == values.size
		}
	}

	inner class Station(val attibutes: List<Attribute>,
	                    val myTicket: Ticket,
	                    val nearbyTickets: List<Ticket>) {

		private val validTickets: List<Ticket>

		init {
			validTickets = nearbyTickets.filter {
				ticket ->
				ticket.ints.filter { value -> validAttrributes(value).isEmpty()  }.isEmpty()
			}
		}

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

		private fun valuesOfColumn(column: Int) : List<Int> = validTickets.map { it.ints[column] }

		private  fun ticketSize() = myTicket.ints.size

		fun doMatchingStuff() : Long {
			val column2attribute = HashMap<Attribute, Int>()
			val currentState = (0 until ticketSize()).map {
				Pair(it, attibutes.filter { attribute -> attribute.fitsAttribute(valuesOfColumn(it) ) })
			}.toMap().toMutableMap()

			while(currentState.isNotEmpty()) {
				val foundNecessities = currentState.filter { it.value.size == 1 }
				if (foundNecessities.isEmpty()) throw RuntimeException("Logic failure")
				foundNecessities.forEach {
					val value = it.value.first()
					column2attribute[value] = it.key
					currentState.remove(it.key)
					currentState.keys.forEach {
						currentState[it] = currentState[it]!!.minus(value)
					}
				}
			}

			val departureColumns = column2attribute.filter { it.key.name.startsWith("departure") }

			return if (departureColumns.isEmpty()) 0
			       else departureColumns.map { myTicket.ints[it.value].toLong() }
					                    .reduce(Long::times)
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
			val station = parseStation(input.toList())
			logger.info("${station.doMatchingStuff()} do you get if you multiply those six values together")
		}
	}

	override fun run(vararg args: String?) {
		solve1(args[0]!!)
		solve2(args[0]!!)
	}
}

fun main(args: Array<String>) {
	runApplication<Aoc2020ApplicationDay16>(*args)
}

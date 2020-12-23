package com.ae.aoc2020.day23

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.BufferedReader
import java.io.InputStreamReader

@SpringBootApplication
class Aoc2020ApplicationDay23 : CommandLineRunner {

	private val logger = LoggerFactory.getLogger(javaClass)

	data class CupNode(val id: Int, var nextCup: CupNode?) {
		fun removeNext(amount: Int) : List<CupNode> {
			return (0 until amount).map {
				val toRemove = nextCup!!
				nextCup = nextCup!!.nextCup
				toRemove
			}.toList()
		}

		fun insertNext(nodes: List<CupNode>) {
			nodes.last().nextCup = nextCup
			nextCup = nodes[0]
		}

		override fun toString(): String {
			val nextCupId = if (nextCup == null) "X" else nextCup!!.id
			return "[id: ${id} next: -> ${nextCupId}]"
		}

		fun findId(id_: Int) : CupNode {
			if (id_ == id) return this
			return nextCup!!.findId(id_)
		}

		fun next() : CupNode {
			return nextCup!!
		}
	}

	private fun solve(file: String) {
		val bufferedReader = BufferedReader(InputStreamReader(Aoc2020ApplicationDay23::class.java.getResourceAsStream("/$file")))
		bufferedReader.useLines { input ->
			val line = input.first().toCharArray()

			var pCupNode: CupNode? = null
			var selectedCupNode: CupNode? = null

			line.withIndex().forEach {
				val nextNode = CupNode(it.value.toString().toInt(), null)
				if (it.index == 0) {
					selectedCupNode = nextNode
				}
				if (pCupNode != null) {
					pCupNode!!.nextCup = nextNode
				}
				pCupNode = nextNode
			}
			pCupNode!!.nextCup = selectedCupNode // completing the circle

			(0 until 100).forEach {
			    logger.info("Starting round ***************************************")
				logger.info("Selected Node: ${selectedCupNode!!.id}")
				val pickedUp = selectedCupNode!!.removeNext(3)
			     logger.info("Picked up ${pickedUp}")
				 val possibleDestionations = (1..9).filter {  cupId ->
					                                !pickedUp.map { it.id }.contains(cupId)
										     }
				val lowerDestinations = possibleDestionations.filter { it < selectedCupNode!!.id }.sortedByDescending { it }
				val destinationId =  if (lowerDestinations.isNotEmpty()) lowerDestinations.first()
				                     else possibleDestionations.filter { it > selectedCupNode!!.id }.sortedByDescending { it }.first()
				logger.info("destinationId: $destinationId")
				val destination =  selectedCupNode!!.findId(destinationId)
				destination.insertNext(pickedUp)
				selectedCupNode = selectedCupNode!!.next()
			}

			var nodeToPrint = selectedCupNode!!.findId(1).nextCup!!
			val nestNodes = ArrayList<Int>()
			while (nodeToPrint.id != 1) {
				nestNodes.add(nodeToPrint.id)
				nodeToPrint = nodeToPrint.nextCup!!
			}
			logger.info("${nestNodes.joinToString("")} are the labels on the cups after cup 1")
		}
	}

	override fun run(vararg args: String?) {
			solve(args[0]!!)
	}
}


fun main(args: Array<String>) {
	runApplication<Aoc2020ApplicationDay23>(*args)
}

package by.varyvoda.matvey.averagek.domain

import by.varyvoda.matvey.averagek.util.getRandom
import by.varyvoda.matvey.averagek.util.nextUnsignedInt
import java.util.Comparator.comparing
import kotlin.concurrent.thread
import kotlin.random.Random

private fun <T> toImmutableResult(mutableResult: List<MutableKClass<T>>): List<KClass<T>> {
    return mutableResult.map { mutableKClass -> KClass(mutableKClass.core, mutableKClass.items) }
}

private data class MutableKClass<Item>(val core: Item, val items: MutableList<Item> = ArrayList())

class KAverage<T, Criteria : Comparable<Criteria>>(
    private val comparator: (kClass: T, item: T) -> Criteria,
    private val centerSelector: (items: List<T>) -> T
) {

    private var workingThread: Thread? = null

    fun calculate(
        kClassesCount: Int,
        items: List<T>,
        stepSleep: Int = 0,
        stepResultNotifier: (stepResult: List<KClass<T>>) -> Unit
    ) {
        workingThread = thread {
            var kClasses: List<T> = getFirstKClasses(kClassesCount, items)
            var prevKClasses: List<T>
            var result: List<MutableKClass<T>>

            do {
                prevKClasses = kClasses
                result = calculateResult(kClasses, items, comparator)

                stepResultNotifier(toImmutableResult(result))
                try {
                    Thread.sleep(stepSleep.toLong())
                } catch (e: InterruptedException) {
                    return@thread
                }

                kClasses = result.map { kClass -> centerSelector(kClass.items) }
            } while (!prevKClasses.containsAll(kClasses))
        }
    }

    fun stop() {
        workingThread?.interrupt()
        workingThread = null
    }

    private fun getFirstKClasses(kClassesCount: Int, items: List<T>): List<T> {
        val random: Random = getRandom()
        val kClasses: MutableList<T> = ArrayList()
        for (i in 0 until kClassesCount) {
            kClasses.add(items[random.nextUnsignedInt() % items.size])
        }
        return kClasses
    }

    private fun calculateResult(
        kClasses: List<T>,
        items: List<T>,
        comparator: (kClass: T, item: T) -> Criteria
    ): List<MutableKClass<T>> {
        return items
            .fold(
                kClasses.map { kClass -> MutableKClass(kClass) }
            ) { acc, item ->
                acc.stream()
                    .min(comparing { o -> comparator(o.core, item) })
                    .ifPresent { kClass -> kClass.items.add(item) }
                acc
            }
    }
}
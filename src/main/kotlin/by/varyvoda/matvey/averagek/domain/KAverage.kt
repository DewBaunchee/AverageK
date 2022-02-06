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
    val comparator: (kClass: T, item: T) -> Criteria,
    private val centerSelector: (items: List<T>) -> T
) {

    private var workingThread: Thread? = null

    fun calculate(
        kClassesCount: Int,
        items: List<T>,
        stepResultNotifier: (stepResult: List<KClass<T>>) -> Unit
    ) {
        workingThread = thread {
            redistribute(getFirstKClasses(kClassesCount, items), items, stepResultNotifier)
        }
    }

    fun redistribute(
        cores: List<T>,
        items: List<T>,
        stepResultNotifier: (stepResult: List<KClass<T>>) -> Unit
    ): List<KClass<T>> {
        var kClasses: List<T> = cores
        var prevKClasses: List<T>
        var result: List<KClass<T>>

        do {
            prevKClasses = kClasses
            result = calculateResult(kClasses, items, comparator)
            stepResultNotifier(result)
            kClasses = result.map { kClass -> centerSelector(kClass.items) }
        } while (!prevKClasses.containsAll(kClasses))
        return result
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
    ): List<KClass<T>> {
        return toImmutableResult(items
            .fold(
                kClasses.map { kClass -> MutableKClass(kClass) }
            ) { acc, item ->
                acc.stream()
                    .min(comparing { o -> comparator(o.core, item) })
                    .ifPresent { kClass -> kClass.items.add(item) }
                acc
            })
    }
}
package com.soulradio.soulradio

import java.util.Calendar

data class NowPlaying(val work: String, val performer: String)

data class Frequency(
    val key: String,
    val label: String,
    val title: String,
    val isCompanion: Boolean = false,
    val nowPlaying: NowPlaying? = null,
) {
    val assetFolder: String get() = "audio/$key"
}

object Frequencies {
    val dial = listOf(
        Frequency("174", "174", "the foundation"),
        Frequency("285", "285", "the slow turn"),
        Frequency("396", "396", "the morning gate"),
        Frequency("417", "417", "the dissolver"),
        Frequency(
            "528", "528", "the centre",
            nowPlaying = NowPlaying(
                work = "Bach · Cello Suite No. 1, Prelude",
                performer = "Matthieu Fontana · CC BY-NC-ND 3.0",
            ),
        ),
        Frequency("639", "639", "the table"),
        Frequency(
            "741", "741", "the clearing",
            nowPlaying = NowPlaying(
                work = "Bach · Goldberg Variations, Aria",
                performer = "Kimiko Ishizaka · CC0 (Open Goldberg)",
            ),
        ),
        Frequency("852", "852", "the high window"),
        Frequency("963", "963", "the crown"),
    )

    val companions = listOf(
        Frequency("432", "432", "Verdi's A", isCompanion = true),
        Frequency("7.83", "7.83", "Schumann", isCompanion = true),
    )

    val all = dial + companions

    fun byKey(key: String): Frequency? = all.firstOrNull { it.key == key }

    /**
     * The 24-hour auto-loop schedule, transcribed from FREQUENCIES.md § Quick map.
     * Returns the frequency the loop would be playing right now.
     */
    fun forHour(hour: Int): Frequency = when (hour) {
        in 6..8   -> byKey("396")!!
        in 9..11  -> byKey("741")!!
        in 12..15 -> byKey("528")!!
        in 16..17 -> byKey("639")!!
        in 18..19 -> byKey("417")!!
        in 20..21 -> byKey("285")!!
        22        -> byKey("174")!!
        else      -> byKey("7.83")!!
    }

    fun forNow(): Frequency = forHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))

    fun currentHour(): Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
}

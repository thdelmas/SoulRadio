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
        Frequency(
            "174", "174", "the foundation",
            nowPlaying = NowPlaying(
                work = "Tchaikovsky · Pilgrim's Song",
                performer = "Feodor Chaliapin (1924) · public domain",
            ),
        ),
        Frequency(
            "285", "285", "the slow turn",
            nowPlaying = NowPlaying(
                work = "Bach · Air on the G String, BWV 1068",
                performer = "Joel Belov · Robert Gayler (1920) · public domain",
            ),
        ),
        Frequency(
            "396", "396", "the morning gate",
            nowPlaying = NowPlaying(
                work = "Bach · Wachet auf, BWV 140 — opening chorus",
                performer = "MIT Concert Choir, William Cutter · CC BY-SA 2.0",
            ),
        ),
        Frequency(
            "417", "417", "the dissolver",
            nowPlaying = NowPlaying(
                work = "Allegri · Miserere mei, Deus",
                performer = "Ensamble Escénico Vocal · CC BY 3.0",
            ),
        ),
        Frequency(
            "528", "528", "the centre",
            nowPlaying = NowPlaying(
                work = "Bach · Cello Suite No. 1, Prelude",
                performer = "Matthieu Fontana · CC BY-NC-ND 3.0",
            ),
        ),
        Frequency(
            "639", "639", "the table",
            nowPlaying = NowPlaying(
                work = "Bach · Brandenburg Concerto No. 3, BWV 1048 — Allegro",
                performer = "Advent Chamber Orchestra · CC BY-SA 2.0",
            ),
        ),
        Frequency(
            "741", "741", "the clearing",
            nowPlaying = NowPlaying(
                work = "Bach · Goldberg Variations, Aria",
                performer = "Kimiko Ishizaka · CC0 (Open Goldberg)",
            ),
        ),
        Frequency(
            "852", "852", "the high window",
            nowPlaying = NowPlaying(
                work = "Beethoven · Moonlight Sonata, Adagio sostenuto",
                performer = "Paul Pitman · public domain (Musopen)",
            ),
        ),
        Frequency(
            "963", "963", "the crown",
            nowPlaying = NowPlaying(
                work = "Bach · Mass in B minor, Agnus Dei",
                performer = "Drozd · Titiajev · Ostapovych · CC BY-SA 3.0",
            ),
        ),
    )

    val companions = listOf(
        Frequency("432", "432", "Verdi's A", isCompanion = true),
        Frequency(
            "7.83", "7.83", "Schumann", isCompanion = true,
            nowPlaying = NowPlaying(
                work = "Gregorian chant — recorded inside the Abbey of Sant'Antimo",
                performer = "Zyance (2008) · CC BY-SA 3.0",
            ),
        ),
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
        else      -> byKey("7.83")!! // 23 and 0..5 — Sant'Antimo chant under stone reverb
    }

    fun forNow(): Frequency = forHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))

    fun currentHour(): Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
}

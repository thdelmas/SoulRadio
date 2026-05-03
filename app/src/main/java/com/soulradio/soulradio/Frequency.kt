package com.soulradio.soulradio

import android.content.Context
import java.util.Calendar

/**
 * One bundled recording on a band. [asset] is the filename inside the
 * frequency's `audio/<key>/` folder; the rest is metadata for the
 * now-playing card.
 */
data class NowPlaying(val asset: String, val work: String, val performer: String)

data class Frequency(
    val key: String,
    val label: String,
    val title: String,
    val isCompanion: Boolean = false,
    val tracks: List<NowPlaying> = emptyList(),
) {
    val assetFolder: String get() = "audio/$key"
    fun assetPath(track: NowPlaying): String = "$assetFolder/${track.asset}"
}

object Frequencies {
    val dial = listOf(
        Frequency(
            "174", "174", "the foundation",
            tracks = listOf(
                NowPlaying(
                    asset = "pilgrims_song_chaliapin.flac",
                    work = "Tchaikovsky · Pilgrim's Song",
                    performer = "Feodor Chaliapin (1924) · public domain",
                ),
                NowPlaying(
                    asset = "kyrie_schola_gregoriana.ogg",
                    work = "Gregorian Kyrie eleison",
                    performer = "Schola Gregoriana · CC BY-SA 3.0",
                ),
                NowPlaying(
                    asset = "bach_cello_suite_1_prelude_michel.ogg",
                    work = "Bach · Cello Suite No. 1 in G, Prelude (BWV 1007)",
                    performer = "John Michel · CC BY-SA 3.0",
                ),
                NowPlaying(
                    asset = "chopin_berceuse_vanderknaap.ogg",
                    work = "Chopin · Berceuse, Op. 57",
                    performer = "Veronica van der Knaap (live, Christchurch 2002) · public domain",
                ),
                NowPlaying(
                    asset = "zabriskie_cylinder_eight.ogg",
                    work = "Chris Zabriskie · Cylinder Eight",
                    performer = "Chris Zabriskie (Cylinders, 2014) · CC BY 4.0",
                ),
            ),
        ),
        Frequency(
            "285", "285", "the slow turn",
            tracks = listOf(
                NowPlaying(
                    asset = "bach_air_belov.ogg",
                    work = "Bach · Air on the G String, BWV 1068",
                    performer = "Joel Belov · Robert Gayler (1920) · public domain",
                ),
                NowPlaying(
                    asset = "tallis_if_ye_love_me_santa_pazienza.ogg",
                    work = "Tallis · If ye love me",
                    performer = "Coro Santa Pazienza · CC BY 3.0",
                ),
                NowPlaying(
                    asset = "byrd_4part_mass_agnus_dei.ogg",
                    work = "Byrd · Mass for Four Voices — Agnus Dei",
                    performer = "Ensemble Morales · CC BY 3.0",
                ),
                NowPlaying(
                    asset = "satie_gymnopedie_1_musopen.ogg",
                    work = "Satie · Gymnopédie No. 1",
                    performer = "Musopen · public domain",
                ),
                NowPlaying(
                    asset = "brahms_intermezzo_117_1.ogg",
                    work = "Brahms · Intermezzo, Op. 117 No. 1",
                    performer = "La Pianista · CC BY-SA 3.0",
                ),
            ),
        ),
        Frequency(
            "396", "396", "the morning gate",
            tracks = listOf(
                NowPlaying(
                    asset = "bach_wachet_auf_mit.ogg",
                    work = "Bach · Wachet auf, BWV 140 — opening chorus",
                    performer = "MIT Concert Choir, William Cutter · CC BY-SA 2.0",
                ),
                NowPlaying(
                    asset = "mozart_ave_verum_senftenberg.ogg",
                    work = "Mozart · Ave verum corpus, K. 618",
                    performer = "Senftenberg choir (2008) · CC BY-SA 3.0",
                ),
                NowPlaying(
                    asset = "mozart_laudate_dominum_mit.ogg",
                    work = "Mozart · Laudate Dominum (Vesperae de Dominica, K. 321)",
                    performer = "MIT Concert Choir, William Cutter · CC BY-SA 2.0",
                ),
                NowPlaying(
                    asset = "raga_bhairavi_morley.opus",
                    work = "Raga Bhairavi (morning raga) — sitar",
                    performer = "Reshma Srivastava · attribution: Larry Morley (2012) · CC BY 3.0",
                ),
            ),
        ),
        Frequency(
            "417", "417", "the dissolver",
            tracks = listOf(
                NowPlaying(
                    asset = "allegri_miserere_escenico.ogg",
                    work = "Allegri · Miserere mei, Deus",
                    performer = "Ensamble Escénico Vocal · CC BY 3.0",
                ),
                NowPlaying(
                    asset = "victoria_o_vos_omnes_tudor.ogg",
                    work = "Victoria · O vos omnes",
                    performer = "The Tudor Consort · CC BY 2.5",
                ),
                NowPlaying(
                    asset = "rore_agnus_praeter_tudor.ogg",
                    work = "Cipriano de Rore · Missa Praeter Rerum Seriem — Agnus Dei",
                    performer = "The Tudor Consort · CC BY 3.0",
                ),
                NowPlaying(
                    asset = "faure_cantique_passy.ogg",
                    work = "Fauré · Cantique de Jean Racine, Op. 11",
                    performer = "Petits Chanteurs de Passy · CC BY-SA 3.0",
                ),
                NowPlaying(
                    asset = "buckley_penumbra.mp3",
                    work = "Scott Buckley · Penumbra",
                    performer = "Scott Buckley · CC BY 4.0 (scottbuckley.com.au)",
                ),
            ),
        ),
        Frequency(
            "528", "528", "the centre",
            tracks = listOf(
                NowPlaying(
                    asset = "vivaldi_spring_harrison.ogg",
                    work = "Vivaldi · The Four Seasons — Spring, mvt. I",
                    performer = "John Harrison · Wichita State Chamber Players · CC BY-SA 3.0",
                ),
                NowPlaying(
                    asset = "pachelbel_canon_1694.mp3",
                    work = "Pachelbel · Canon in D (1694 arrangement)",
                    performer = "Period arrangement · CC BY 4.0",
                ),
                NowPlaying(
                    asset = "mozart_clarinet_adagio_mccoll.ogg",
                    work = "Mozart · Clarinet Concerto, K. 622 — Adagio",
                    performer = "William McColl · UW Symphony, Abraham Kaplan · CC BY-SA 2.0",
                ),
                NowPlaying(
                    asset = "schumann_traumerei_betts.ogg",
                    work = "Schumann · Träumerei (Kinderszenen, Op. 15 No. 7)",
                    performer = "Donald Betts · public domain (Musopen)",
                ),
                NowPlaying(
                    asset = "chopin_nocturne_9_2_goldstein.ogg",
                    work = "Chopin · Nocturne in E♭, Op. 9 No. 2",
                    performer = "Martha Goldstein · CC BY-SA 2.0",
                ),
            ),
        ),
        Frequency(
            "639", "639", "the table",
            tracks = listOf(
                NowPlaying(
                    asset = "brandenburg3_advent.ogg",
                    work = "Bach · Brandenburg Concerto No. 3, BWV 1048 — Allegro",
                    performer = "Advent Chamber Orchestra · CC BY-SA 2.0",
                ),
                NowPlaying(
                    asset = "palestrina_sicut_lilium_tudor.ogg",
                    work = "Palestrina · Sicut lilium inter spinas",
                    performer = "The Tudor Consort · CC BY 3.0",
                ),
                NowPlaying(
                    asset = "monteverdi_laudate_pueri_cantica.ogg",
                    work = "Monteverdi · Laudate pueri (Vespro della Beata Vergine, 1610)",
                    performer = "Cantica Symphonia, Giuseppe Maletto · CC BY-SA 3.0",
                ),
                NowPlaying(
                    asset = "lautenbacher_simard_cedar_flute.ogg",
                    work = "Lautenbacher · Native American flute composition",
                    performer = "Guy Simard (flute), Marc Lautenbacher (composition) · CC BY-SA 4.0",
                ),
            ),
        ),
        Frequency(
            "741", "741", "the clearing",
            tracks = listOf(
                NowPlaying(
                    asset = "goldberg_aria_ishizaka.flac",
                    work = "Bach · Goldberg Variations, Aria",
                    performer = "Kimiko Ishizaka · CC0 (Open Goldberg)",
                ),
                NowPlaying(
                    asset = "couperin_barricades_pracchia.ogg",
                    work = "Couperin · Les Barricades Mystérieuses",
                    performer = "Pracchia-78 · public domain",
                ),
                NowPlaying(
                    asset = "scarlatti_k87_b_minor.ogg",
                    work = "Scarlatti · Sonata in B minor, K. 87",
                    performer = "Harpsichord (Kirnberger temperament) · CC0",
                ),
                NowPlaying(
                    asset = "mozart_k545_allegro_musopen.ogg",
                    work = "Mozart · Sonata in C, K. 545 (\"facile\") — Allegro",
                    performer = "Musopen · public domain",
                ),
            ),
        ),
        Frequency(
            "852", "852", "the high window",
            tracks = listOf(
                NowPlaying(
                    asset = "beethoven_moonlight_pitman.ogg",
                    work = "Beethoven · Moonlight Sonata, Adagio sostenuto",
                    performer = "Paul Pitman · public domain (Musopen)",
                ),
                NowPlaying(
                    asset = "debussy_clair_de_lune_1905.opus",
                    work = "Debussy · Clair de Lune (Suite bergamasque)",
                    performer = "1905 piano-roll · public domain",
                ),
                NowPlaying(
                    asset = "beethoven_hammerklavier_adagio_pitman.ogg",
                    work = "Beethoven · Hammerklavier Sonata, Op. 106 — Adagio sostenuto",
                    performer = "Paul Pitman · CC0 (Musopen)",
                ),
                NowPlaying(
                    asset = "satie_gnossienne_1.ogg",
                    work = "Satie · Gnossienne No. 1",
                    performer = "La Pianista · CC BY-SA 3.0",
                ),
                NowPlaying(
                    asset = "mendelssohn_gondellied_membeth.ogg",
                    work = "Mendelssohn · Venetianisches Gondellied, Op. 30 No. 6",
                    performer = "Membeth · CC0",
                ),
                NowPlaying(
                    asset = "ravel_pavane_dussaut.ogg",
                    work = "Ravel · Pavane pour une infante défunte",
                    performer = "Thérèse Dussaut · CC BY-SA 2.0",
                ),
                NowPlaying(
                    asset = "buckley_meanwhile.mp3",
                    work = "Scott Buckley · Meanwhile",
                    performer = "Scott Buckley · CC BY 4.0 (scottbuckley.com.au)",
                ),
            ),
        ),
        Frequency(
            "963", "963", "the crown",
            tracks = listOf(
                NowPlaying(
                    asset = "bach_bminor_agnus_drozd.ogg",
                    work = "Bach · Mass in B minor, Agnus Dei",
                    performer = "Drozd · Titiajev · Ostapovych · CC BY-SA 3.0",
                ),
                NowPlaying(
                    asset = "hildegard_o_frondens_kadel.ogg",
                    work = "Hildegard von Bingen · O frondens virga",
                    performer = "Magdalen Kadel · CC BY-SA 3.0",
                ),
                NowPlaying(
                    asset = "tallis_videte_miraculum_tudor.ogg",
                    work = "Tallis · Videte miraculum",
                    performer = "The Tudor Consort · CC BY-SA 3.0",
                ),
                NowPlaying(
                    asset = "byzantine_hymn_part1.ogg",
                    work = "Byzantine ecclesiastical hymn",
                    performer = "Greek Orthodox cantor · CC0",
                ),
                NowPlaying(
                    asset = "guqin_liu_shui_huang.ogg",
                    work = "Guqin · Liu Shui (Flowing Water) — on the Voyager Golden Record",
                    performer = "Charlie Huang · CC BY-SA 3.0",
                ),
            ),
        ),
    )

    val companions = listOf(
        Frequency(
            "432", "432", "Verdi's A", isCompanion = true,
            tracks = listOf(
                NowPlaying(
                    asset = "celeste-aida-caruso-1904.flac",
                    work = "Verdi · Celeste Aida (Aida)",
                    performer = "Enrico Caruso (1904) · public domain",
                ),
                NowPlaying(
                    asset = "caruso_una_furtiva_lagrima_1911.ogg",
                    work = "Donizetti · Una furtiva lagrima (L'elisir d'amore)",
                    performer = "Enrico Caruso (1911) · public domain",
                ),
                NowPlaying(
                    asset = "patti_home_sweet_home_1905.flac",
                    work = "Bishop · Home, Sweet Home",
                    performer = "Adelina Patti (1905) · public domain",
                ),
                NowPlaying(
                    asset = "tamagno_niun_mi_tema_1903.ogg",
                    work = "Verdi · Niun mi tema (Otello, Death of Otello)",
                    performer = "Francesco Tamagno (1903) · public domain",
                ),
                NowPlaying(
                    asset = "schumann_heink_wiegenlied_1906.mp3",
                    work = "Brahms · Wiegenlied (Cradle Song), Op. 49 No. 4",
                    performer = "Ernestine Schumann-Heink · Victor Orchestra (1906) · public domain",
                ),
            ),
        ),
        Frequency(
            "7.83", "7.83", "Schumann", isCompanion = true,
            tracks = listOf(
                NowPlaying(
                    asset = "sanantimo_gregorian_zyance.ogg",
                    work = "Gregorian chant — recorded inside the Abbey of Sant'Antimo",
                    performer = "Zyance (2008) · CC BY-SA 3.0",
                ),
                NowPlaying(
                    asset = "improperia_membeth.ogg",
                    work = "Gregorian Improperia (Good Friday reproaches)",
                    performer = "Membeth (2010) · public domain",
                ),
                NowPlaying(
                    asset = "compline_trinity_boston_2016.ogg",
                    work = "Choral Compline — Trinity Church, Boston",
                    performer = "Trinity Church choir (2016) · CC BY 3.0",
                ),
                NowPlaying(
                    asset = "shika_no_tone_kodo.ogg",
                    work = "Shika no Tōne (Distant Cry of Deer) — shakuhachi honkyoku",
                    performer = "Araki Kodō III (Victor 13029, 1925–37) · public domain",
                ),
                NowPlaying(
                    asset = "tibetan_gyuto_chant_corwin.ogg",
                    work = "Tibetan monks chanting — Gyuto Branch Monastery, McLeod Ganj",
                    performer = "Field recording: Samuel Corwin (2016) · CC BY 4.0",
                ),
                NowPlaying(
                    asset = "macleod_tranquility.mp3",
                    work = "Kevin MacLeod · Tranquility (16-min long-form ambient)",
                    performer = "Kevin MacLeod (incompetech.com) · CC BY 4.0",
                ),
                NowPlaying(
                    asset = "raga_yaman_innokino.opus",
                    work = "Raga Yaman (evening / first-watch raga) — sitar, tanpura, tabla",
                    performer = "Filmed in Benares by INNOKINO medialab (2017) · CC BY 3.0",
                ),
            ),
        ),
    )

    val all = dial + companions

    fun byKey(key: String): Frequency? = all.firstOrNull { it.key == key }

    /** Look up the metadata entry whose [NowPlaying.asset] matches [asset]. */
    fun trackByAsset(freq: Frequency, asset: String): NowPlaying? =
        freq.tracks.firstOrNull { it.asset == asset }

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

    /**
     * Solar-aware variant. When the user has set a location via [LocationStore],
     * returns the band whose solar phase contains "now" — so the dawn band
     * actually arrives at dawn instead of always at 06:00. Falls back to the
     * clock-based [forNow] when no location is set, or in polar regions where
     * the algorithm has no sunrise to anchor to.
     */
    fun forNow(context: Context): Frequency {
        val loc = LocationStore.get(context) ?: return forNow()
        val now = Calendar.getInstance()
        val sun = SolarCalculator.compute(
            year = now.get(Calendar.YEAR),
            month = now.get(Calendar.MONTH) + 1,
            dayOfMonth = now.get(Calendar.DAY_OF_MONTH),
            loc = loc,
            zone = now.timeZone,
        ) ?: return forNow()
        return SolarSchedule.bandAt(now.timeInMillis, sun)
    }

    fun currentHour(): Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
}

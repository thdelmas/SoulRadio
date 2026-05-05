package com.soulradio.soulradio

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * A user-imported audio file. The user's library lives parallel to
 * [Frequencies.all] — the curated catalogue is never mutated. Each track
 * carries the auto-profile [AudioProfiler] read at import plus the bands
 * the listener has filed it under (multi-band: a Tibetan overtone chant
 * could sit on 396 for its tonic *and* on 7.83 for its sub-60 drone).
 *
 * - [sourceUri]: the SAF Uri the system picker returned. The import flow
 *   takes persistable read permission so the file remains accessible
 *   after process restart.
 * - [displayName]: short label for the UI, taken from the document's
 *   metadata at import time.
 * - [profile]: the auto-profile read by [AudioProfiler] at import.
 * - [assignedBands]: the bands the listener has filed this track under.
 *   Defaults to the top auto-match's bandKey at import; empty when the
 *   auto-profile yielded no Solfeggio match (the ancestral case — the
 *   listener files manually).
 * - [manualOverride]: true once the listener has changed [assignedBands]
 *   away from the auto-suggestion. UI uses it to mark which files the
 *   listener has chosen vs. accepted-as-suggested.
 */
data class UserTrack(
    val id: String,
    val sourceUri: String,
    val displayName: String,
    val addedAtMs: Long,
    val profile: BandAssignment,
    val assignedBands: Set<String>,
    val manualOverride: Boolean,
)

/**
 * Persistent store for user-imported tracks. JSON-serialised list inside
 * the same `soulradio.state` SharedPreferences as the rest of the
 * single-fact stores; uses [org.json] (built-in to Android) so no new
 * dependency is added — see CLAUDE.md.
 */
object UserTracksStore {
    private const val PREFS = "soulradio.state"
    private const val KEY = "user_tracks_json"

    fun all(context: Context): List<UserTrack> {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY, null) ?: return emptyList()
        return runCatching { decode(raw) }.getOrDefault(emptyList())
    }

    fun byBand(context: Context, bandKey: String): List<UserTrack> =
        all(context).filter { bandKey in it.assignedBands }

    fun add(context: Context, track: UserTrack) {
        val updated = all(context).filterNot { it.id == track.id } + track
        write(context, updated)
    }

    fun update(context: Context, id: String, transform: (UserTrack) -> UserTrack) {
        val updated = all(context).map { if (it.id == id) transform(it) else it }
        write(context, updated)
    }

    fun remove(context: Context, id: String) {
        write(context, all(context).filterNot { it.id == id })
    }

    fun clear(context: Context) {
        write(context, emptyList())
    }

    private fun write(context: Context, tracks: List<UserTrack>) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY, encode(tracks))
            .apply()
    }

    // -- JSON round-trip (org.json, built-in) --

    internal fun encode(tracks: List<UserTrack>): String {
        val arr = JSONArray()
        for (t in tracks) {
            val o = JSONObject()
            o.put("id", t.id)
            o.put("uri", t.sourceUri)
            o.put("name", t.displayName)
            o.put("addedAt", t.addedAtMs)
            o.put("override", t.manualOverride)
            o.put("bands", JSONArray(t.assignedBands.toList()))
            o.put("profile", profileToJson(t.profile))
            arr.put(o)
        }
        return arr.toString()
    }

    internal fun decode(raw: String): List<UserTrack> {
        val arr = JSONArray(raw)
        val out = mutableListOf<UserTrack>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            val bandsArr = o.getJSONArray("bands")
            val bands = mutableSetOf<String>()
            for (j in 0 until bandsArr.length()) bands += bandsArr.getString(j)
            out += UserTrack(
                id = o.getString("id"),
                sourceUri = o.getString("uri"),
                displayName = o.getString("name"),
                addedAtMs = o.getLong("addedAt"),
                profile = profileFromJson(o.getJSONObject("profile")),
                assignedBands = bands,
                manualOverride = o.optBoolean("override", false),
            )
        }
        return out
    }

    private fun profileToJson(p: BandAssignment): JSONObject {
        val o = JSONObject()
        val matches = JSONArray()
        for (m in p.matches) {
            matches.put(JSONObject().apply {
                put("k", m.bandKey)
                put("c", m.confidence.toDouble())
                put("r", m.reason)
            })
        }
        o.put("matches", matches)
        val s = JSONObject()
        p.signals.bpm?.let { s.put("bpm", it.toDouble()) }
        s.put("tilt", p.signals.spectralTiltDbPerOctave.toDouble())
        s.put("sub60", p.signals.sub60HzEnergyFraction.toDouble())
        p.signals.dominantHz?.let { s.put("dom", it.toDouble()) }
        o.put("signals", s)
        return o
    }

    private fun profileFromJson(o: JSONObject): BandAssignment {
        val matchesArr = o.getJSONArray("matches")
        val matches = mutableListOf<BandMatch>()
        for (i in 0 until matchesArr.length()) {
            val m = matchesArr.getJSONObject(i)
            matches += BandMatch(
                bandKey = m.getString("k"),
                confidence = m.getDouble("c").toFloat(),
                reason = m.getString("r"),
            )
        }
        val s = o.getJSONObject("signals")
        return BandAssignment(
            matches = matches,
            signals = ProfileSignals(
                bpm = if (s.has("bpm")) s.getDouble("bpm").toFloat() else null,
                spectralTiltDbPerOctave = s.getDouble("tilt").toFloat(),
                sub60HzEnergyFraction = s.getDouble("sub60").toFloat(),
                dominantHz = if (s.has("dom")) s.getDouble("dom").toFloat() else null,
            ),
        )
    }
}

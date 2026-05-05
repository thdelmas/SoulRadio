package com.soulradio.soulradio

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class UserTrack(
    val id: String,
    val sourceUri: String,
    val displayName: String,
    val addedAtMs: Long,
    val profile: BandAssignment,
    val assignedBands: Set<String>,
    val manualOverride: Boolean,
)

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

    fun add(context: Context, track: UserTrack): List<UserTrack> {
        val updated = all(context).filterNot { it.id == track.id } + track
        write(context, updated)
        return updated
    }

    fun update(
        context: Context,
        id: String,
        transform: (UserTrack) -> UserTrack,
    ): List<UserTrack> {
        val updated = all(context).map { if (it.id == id) transform(it) else it }
        write(context, updated)
        return updated
    }

    fun remove(context: Context, id: String): List<UserTrack> {
        val updated = all(context).filterNot { it.id == id }
        write(context, updated)
        return updated
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

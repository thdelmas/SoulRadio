package com.soulradio.soulradio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Locks the Radio mode catalogue against the contract: every entry has
 * non-empty content; entries do not collide with dial keys; every documented
 * group is non-empty.
 */
class CatalogueTest {

    @Test
    fun catalogue_isNonEmpty() {
        assertTrue(
            "Catalogue must have at least one entry to justify Radio mode existing",
            Catalogue.entries.isNotEmpty(),
        )
    }

    @Test
    fun every_entry_hasNonEmptyFields() {
        for (entry in Catalogue.entries) {
            assertTrue("hz blank for entry $entry", entry.hz.isNotBlank())
            assertTrue("title blank for entry $entry", entry.title.isNotBlank())
            // Radio's product is the four sections — each one shipping
            // empty would leave the listener with a header and a void.
            assertTrue("history blank for ${entry.hz}", entry.history.isNotBlank())
            assertTrue("believed blank for ${entry.hz}", entry.believed.isNotBlank())
            assertTrue("studies blank for ${entry.hz}", entry.studies.isNotBlank())
            assertTrue("references blank for ${entry.hz}", entry.references.isNotBlank())
        }
    }

    @Test
    fun catalogue_doesNotOverlapWithDial() {
        // Radio is the wider catalogue — frequencies *not* on the dial.
        // Numeric Hz values that match a dial key would muddle the modes.
        val dialKeys = Frequencies.all.map { it.key }.toSet()
        for (entry in Catalogue.entries) {
            assertFalse(
                "Catalogue entry ${entry.hz} collides with dial key — Radio is for the wider field, not duplicates",
                entry.hz in dialKeys,
            )
        }
    }

    @Test
    fun every_group_hasAtLeastOneEntry() {
        for (group in CatalogueGroup.entries) {
            val groupEntries = Catalogue.byGroup[group]
            assertNotNull("Group $group missing from byGroup map", groupEntries)
            assertTrue(
                "Group $group has zero entries — either populate it or remove the group",
                groupEntries!!.isNotEmpty(),
            )
        }
    }

    @Test
    fun audibleHzFor_parsesPlainNumeric() {
        assertEquals(528.0, Catalogue.audibleHzFor("528"))
        assertEquals(136.1, Catalogue.audibleHzFor("136.1"))
        assertEquals(1122.0, Catalogue.audibleHzFor("1122"))
    }

    @Test
    fun audibleHzFor_extractsFromReferencePitch() {
        assertEquals(440.0, Catalogue.audibleHzFor("A=440"))
        assertEquals(444.0, Catalogue.audibleHzFor("A=444 / 449"))
        assertEquals(256.0, Catalogue.audibleHzFor("C=256"))
    }

    @Test
    fun audibleHzFor_returnsNullForNonNumeric() {
        assertEquals(null, Catalogue.audibleHzFor("Delta"))
        assertEquals(null, Catalogue.audibleHzFor("Alpha"))
        assertEquals(null, Catalogue.audibleHzFor(""))
    }

    @Test
    fun audibleHzFor_returnsNullForSubAudible() {
        // 7.83 won't show up in the catalogue (it's on the dial), but the
        // Schumann harmonics 14.3 / 20.8 are catalogue entries; the lower
        // ones must be gated out so a tap doesn't pretend to play silence.
        assertEquals(null, Catalogue.audibleHzFor("14.3"))
        assertEquals(null, Catalogue.audibleHzFor("0.5"))
        // Boundary: 20 Hz is the cutoff, inclusive (audible).
        assertEquals(20.0, Catalogue.audibleHzFor("20"))
        // 19.9 must be rejected.
        assertEquals(null, Catalogue.audibleHzFor("19.9"))
    }

    @Test
    fun audibleHzFor_picksFirstNumericForMultiValue() {
        // "111 … 999" is a single catalogue row representing the whole
        // angel-numbers set; playing the first numeric (111) is the
        // documented behaviour and is honest — 111 IS one of the angel
        // numbers, the listener hears one example of the set.
        assertEquals(111.0, Catalogue.audibleHzFor("111 … 999"))
    }

    @Test
    fun no_radio_voice_makes_a_medical_claim() {
        // MANIFESTO §5 forbids medical/health claims about any frequency.
        // The radio's *own* voice — history, studies, references — must
        // never describe what the tone does to the listener's body. The
        // `believed` field is exempt: its job is to report folklore as
        // folklore (proponents say X), and the field name itself does the
        // framing work. Banned verbs below catch the most common ways a
        // claim sneaks into copy; this is a drift guardrail, not a
        // comprehensive linter.
        val bannedTerms = listOf(
            "cures", "cure ", " cure.", "cure,",
            "heals", "heal ", "healing",
            "treats ", "treatment of",
            "induces", "induce ", "induction of",
            "balances your", "balances the body",
            "repairs ", "repair the",
            "DNA repair", "cellular repair",
            "anxiety relief", "pain relief", "depression relief",
        )
        for (entry in Catalogue.entries) {
            val radioVoiceFields = mapOf(
                "history" to entry.history,
                "studies" to entry.studies,
                "references" to entry.references,
            )
            for ((fieldName, content) in radioVoiceFields) {
                for (term in bannedTerms) {
                    assertFalse(
                        "Catalogue $fieldName for ${entry.hz} contains banned medical-claim term \"$term\". " +
                            "The radio's own voice (history/studies/references) must not describe what the tone does to the body. " +
                            "See MANIFESTO.md §5.",
                        content.lowercase().contains(term.lowercase()),
                    )
                }
            }
        }
    }

    @Test
    fun cousto_set_isComplete() {
        // The Cousto cosmic octave is documented as a *set*; partial inclusion
        // would imply curation we have not done. tunables.md: "adding one
        // means adding the set." If we ship Cousto, we ship at least the
        // documented planetary tones.
        val cousto = Catalogue.byGroup[CatalogueGroup.COUSTO].orEmpty()
        val expected = setOf("136.1", "194.18", "126.22", "210.42", "144.72", "183.58", "147.85")
        assertEquals(
            "Cousto entries must match the documented planetary set",
            expected,
            cousto.map { it.hz }.toSet(),
        )
    }
}

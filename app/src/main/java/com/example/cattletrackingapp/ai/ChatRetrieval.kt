package com.example.cattletrackingapp.ai

import com.example.cattletrackingapp.data.repository.BullsRepository
import com.example.cattletrackingapp.data.repository.CalvesRepository
import com.example.cattletrackingapp.data.repository.CowsRepository
import javax.inject.Inject

/**
 * Natural-language retrieval. Produces:
 *  - a compact string context (for Gemini / fallback text answers)
 *  - a structured EntityResult for deterministic answers
 *
 * Adds tag normalization ("7", "#7", "007") and DOES NOT emit farmer_id in the context.
 */
class ChatRetrieval @Inject constructor(
    private val cows: CowsRepository,
    private val bulls: BullsRepository,
    private val calves: CalvesRepository
) {
    enum class Entity { Cow, Bull, Calf }

    data class ParsedQuery(
        val tag: String? = null,
        val id: String? = null,
        val entity: Entity? = null
    )

    sealed class EntityResult {
        data class Cow(
            val farmer_id: String,
            val tag_number: String,
            val dam_number: String?,
            val sire_number: String?,
            val birth_date: String,
            val created_at: String?
        ) : EntityResult()

        data class Bull(
            val farmer_id: String,
            val tag_number: String,
            val bull_name: String?,
            val date_in: String?,
            val date_out: String?,
            val created_at: String?
        ) : EntityResult()

        data class Calf(
            val farmer_id: String,
            val tag_number: String,
            val dam_number: String,
            val sire_number: String,
            val birth_date: String,
            val sex: String,
            val current_weight: Double?,
            val avg_gain: Double?,
            val created_at: String?
        ) : EntityResult()
    }

    suspend fun fetchEntityFor(userText: String): EntityResult? {
        val q = parse(userText)
        if (q.tag == null && q.id == null) return null

        val order = q.entity?.let { listOf(it) } ?: listOf(Entity.Cow, Entity.Bull, Entity.Calf)

        for (e in order) {
            when (e) {
                Entity.Cow -> {
                    if (q.tag != null) {
                        val candidates = normalizeTagInput(q.tag)
                        for (cand in candidates) {
                            try {
                                val found = cows.searchCowByTag(cand).firstOrNull()
                                if (found != null) {
                                    return EntityResult.Cow(
                                        farmer_id = found.farmer_id,
                                        tag_number = found.tag_number,
                                        dam_number = found.dam_number,
                                        sire_number = found.sire_number,
                                        birth_date = found.birth_date,
                                        created_at = found.created_at
                                    )
                                }
                            } catch (_: Exception) {}
                        }
                    }
                    if (q.id != null) {
                        try {
                            val byId = cows.getCowById(q.id)
                            if (byId != null) {
                                return EntityResult.Cow(
                                    farmer_id = byId.farmer_id,
                                    tag_number = byId.tag_number,
                                    dam_number = byId.dam_number,
                                    sire_number = byId.sire_number,
                                    birth_date = byId.birth_date,
                                    created_at = byId.created_at
                                )
                            }
                        } catch (_: Exception) {}
                    }
                }

                Entity.Bull -> {
                    if (q.tag != null) {
                        val candidates = normalizeTagInput(q.tag)
                        for (cand in candidates) {
                            try {
                                val found = bulls.searchBullByTag(cand).firstOrNull()
                                if (found != null) {
                                    return EntityResult.Bull(
                                        farmer_id = found.farmer_id,
                                        tag_number = found.tag_number,
                                        bull_name = found.bull_name,
                                        date_in   = found.date_in,
                                        date_out  = found.date_out,
                                        created_at = found.created_at
                                    )
                                }
                            } catch (_: Exception) {}
                        }
                    }
                }

                Entity.Calf -> {
                    if (q.tag != null) {
                        val candidates = normalizeTagInput(q.tag)
                        for (cand in candidates) {
                            try {
                                val found = calves.searchCalfByTag(cand).firstOrNull()
                                if (found != null) {
                                    return EntityResult.Calf(
                                        farmer_id = found.farmer_id,
                                        tag_number = found.tag_number,
                                        dam_number = found.dam_number,
                                        sire_number = found.sire_number,
                                        birth_date = found.birth_date,
                                        sex        = found.sex,
                                        current_weight = found.current_weight,
                                        avg_gain       = found.avg_gain,
                                        created_at     = found.created_at
                                    )
                                }
                            } catch (_: Exception) {}
                        }
                    }
                    if (q.id != null) {
                        try {
                            val byId = calves.getCalfById(q.id)
                            if (byId != null) {
                                return EntityResult.Calf(
                                    farmer_id = byId.farmer_id,
                                    tag_number = byId.tag_number,
                                    dam_number = byId.dam_number,
                                    sire_number = byId.sire_number,
                                    birth_date = byId.birth_date,
                                    sex        = byId.sex,
                                    current_weight = byId.current_weight,
                                    avg_gain       = byId.avg_gain,
                                    created_at     = byId.created_at
                                )
                            }
                        } catch (_: Exception) {}
                    }
                }
            }
        }
        return null
    }

    /** Builds the text context the model will read. NOTE: farmer_id is intentionally omitted. */
    suspend fun fetchContextFor(userText: String): String {
        val entity = fetchEntityFor(userText) ?: return HELP
        return when (entity) {
            is EntityResult.Cow -> buildString {
                appendLine("Entity: Cow")
                // no farmer_id line
                appendLine("tag_number: ${entity.tag_number}")
                appendLine("dam_number: ${entity.dam_number ?: "(none)"}")
                appendLine("sire_number: ${entity.sire_number ?: "(none)"}")
                appendLine("birth_date: ${entity.birth_date}")
                appendLine("created_at: ${entity.created_at ?: "(none)"}")
            }.trimEnd()

            is EntityResult.Bull -> buildString {
                appendLine("Entity: Bull")
                // no farmer_id line
                appendLine("tag_number: ${entity.tag_number}")
                appendLine("bull_name: ${entity.bull_name ?: "(none)"}")
                appendLine("date_in: ${entity.date_in ?: "(none)"}")
                appendLine("date_out: ${entity.date_out ?: "(none)"}")
                appendLine("created_at: ${entity.created_at ?: "(none)"}")
            }.trimEnd()

            is EntityResult.Calf -> buildString {
                appendLine("Entity: Calf")
                // no farmer_id line
                appendLine("tag_number: ${entity.tag_number}")
                appendLine("dam_number: ${entity.dam_number}")
                appendLine("sire_number: ${entity.sire_number}")
                appendLine("birth_date: ${entity.birth_date}")
                appendLine("sex: ${entity.sex}")
                appendLine("current_weight: ${entity.current_weight ?: 0.0}")
                appendLine("avg_gain: ${entity.avg_gain ?: 0.0}")
                appendLine("created_at: ${entity.created_at ?: "(none)"}")
            }.trimEnd()
        }
    }

    // ---------- Parsing ----------

    private fun parse(text: String): ParsedQuery {
        val t = text.trim()

        // "tag 8", "tag#8", "ear tag 8", "tag:8", "tag8"
        Regex("""\b(?:ear\s+)?tag[#:=\s]*([A-Za-z0-9\-]+)\b""", RegexOption.IGNORE_CASE).find(t)?.let {
            val tag = it.groupValues[1]
            return ParsedQuery(tag = tag, entity = detectEntityHint(t))
        }

        // "cow 8", "bull 15", "calf 203"
        Regex("""\b(cow|bull|calf)s?\s*#?\s*([A-Za-z0-9\-]+)\b""", RegexOption.IGNORE_CASE).find(t)?.let {
            val e = when (it.groupValues[1].lowercase()) {
                "cow" -> Entity.Cow
                "bull" -> Entity.Bull
                "calf" -> Entity.Calf
                else -> null
            }
            val tag = it.groupValues[2]
            return ParsedQuery(tag = tag, entity = e ?: detectEntityHint(t))
        }

        // "#8", "#A12"
        Regex("""#([A-Za-z0-9\-]+)\b""", RegexOption.IGNORE_CASE).find(t)?.let {
            val tag = it.groupValues[1]
            return ParsedQuery(tag = tag, entity = detectEntityHint(t))
        }

        // Standalone short token: "8", "203" (1–6 chars)
        Regex("""\b([A-Za-z0-9\-]{1,6})\b""", RegexOption.IGNORE_CASE).find(t)?.let {
            val token = it.groupValues[1]
            return ParsedQuery(tag = token, entity = detectEntityHint(t))
        }

        // Long ID fallback
        Regex("""\b([a-f0-9]{8,}|[A-Z0-9]{12,})\b""", RegexOption.IGNORE_CASE).find(t)?.let {
            return ParsedQuery(id = it.groupValues[1], entity = detectEntityHint(t))
        }

        return ParsedQuery()
    }

    private fun detectEntityHint(t: String): Entity? {
        val s = t.lowercase()
        return when {
            s.contains("cow") || s.contains("heifer") -> Entity.Cow
            s.contains("bull") || s.contains("steer") -> Entity.Bull
            s.contains("calf") || s.contains("calves") -> Entity.Calf
            else -> null
        }
    }

    // ---------- Helpers ----------
    /** Try the raw tag, without '#', and without leading zeros. */
    private fun normalizeTagInput(raw: String): List<String> {
        val trimmed = raw.trim()
        val noHash = if (trimmed.startsWith("#")) trimmed.drop(1) else trimmed
        val noZeros = noHash.trimStart('0').ifEmpty { "0" }
        return listOf(trimmed, noHash, noZeros).distinct()
    }

    private companion object {
        const val HELP = "Tell me an animal and tag (e.g., “cow 8”, “calf 5”, “tag 8”, “#8”)."
    }
}

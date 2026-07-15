package com.example.myapplication.data

import org.json.JSONArray
import org.json.JSONObject

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: String,
    val phone: String,
    val villageId: Int?,
    val villageName: String?
) {
    val displayName: String
        get() = if (firstName.isNotEmpty() || lastName.isNotEmpty()) "$firstName $lastName".trim() else username

    companion object {
        fun fromJson(json: JSONObject): User {
            val villageObj = json.optJSONObject("village")
            val villageId = if (villageObj != null) {
                villageObj.optInt("id")
            } else {
                val vId = json.optInt("village", -1)
                if (vId != -1) vId else null
            }
            val villageName = villageObj?.optString("name")
            return User(
                id = json.getInt("id"),
                username = json.getString("username"),
                email = json.optString("email", ""),
                firstName = json.optString("first_name", ""),
                lastName = json.optString("last_name", ""),
                role = json.optString("role", "citizen"),
                phone = json.optString("phone", ""),
                villageId = villageId,
                villageName = villageName
            )
        }
    }
}

data class Village(
    val id: Int,
    val name: String,
    val district: String,
    val region: String,
    val population: Int,
    val latitude: Double?,
    val longitude: Double?
) {
    companion object {
        fun fromJson(json: JSONObject): Village {
            return Village(
                id = json.getInt("id"),
                name = json.getString("name"),
                district = json.getString("district"),
                region = json.getString("region"),
                population = json.optInt("population", 0),
                latitude = if (json.isNull("latitude")) null else json.optDouble("latitude"),
                longitude = if (json.isNull("longitude")) null else json.optDouble("longitude")
            )
        }
    }
}

data class WaterSource(
    val id: Int,
    val name: String,
    val sourceType: String,
    val villageId: Int,
    val villageName: String,
    val latitude: Double?,
    val longitude: Double?,
    val status: String,
    val phLevel: Double?,
    val bacteriaCount: Int?,
    val ironLevel: Double?,
    val turbidity: Double?,
    val lastTested: String?,
    val managedById: Int?,
    val managedByName: String?,
    val lastCleaned: String?,
    val nextCleaning: String?,
    val constructionYear: Int?,
    val imageUrl: String?
) {
    val sourceTypeDisplay: String
        get() = when (sourceType) {
            "shallow_well" -> "Kisima cha Juu"
            "deep_well" -> "Kisima cha Kina"
            "spring" -> "Chemchem"
            "river" -> "Mto"
            "dam" -> "Bwawa"
            "borehole" -> "Bomba la Kuchimba"
            "rainwater" -> "Maji ya Mvua"
            else -> sourceType
        }

    val statusDisplay: String
        get() = when (status) {
            "safe" -> "Salama"
            "caution" -> "Tahadhari"
            "unsafe" -> "Hatarini"
            "under_repair" -> "Inatengenezwa"
            "dry" -> "Kavu"
            else -> status
        }

    companion object {
        fun fromJson(json: JSONObject): WaterSource {
            val villageObj = json.optJSONObject("village_details") ?: json.optJSONObject("village")
            val villageId = villageObj?.optInt("id") ?: json.optInt("village", 0)
            val villageName = villageObj?.optString("name") ?: "Kijiji Kisichojulikana"

            val managedByObj = json.optJSONObject("managed_by_details") ?: json.optJSONObject("managed_by")
            val managedById = managedByObj?.optInt("id") ?: if (json.isNull("managed_by")) null else json.optInt("managed_by")
            val managedByName = managedByObj?.optString("username")

            return WaterSource(
                id = json.getInt("id"),
                name = json.getString("name"),
                sourceType = json.getString("source_type"),
                villageId = villageId,
                villageName = villageName,
                latitude = if (json.isNull("latitude")) null else json.optDouble("latitude"),
                longitude = if (json.isNull("longitude")) null else json.optDouble("longitude"),
                status = json.optString("status", "safe"),
                phLevel = if (json.isNull("ph_level")) null else json.optDouble("ph_level"),
                bacteriaCount = if (json.isNull("bacteria_count")) null else json.optInt("bacteria_count"),
                ironLevel = if (json.isNull("iron_level")) null else json.optDouble("iron_level"),
                turbidity = if (json.isNull("turbidity")) null else json.optDouble("turbidity"),
                lastTested = if (json.isNull("last_tested")) null else json.optString("last_tested"),
                managedById = managedById,
                managedByName = managedByName,
                lastCleaned = if (json.isNull("last_cleaned")) null else json.optString("last_cleaned"),
                nextCleaning = if (json.isNull("next_cleaning")) null else json.optString("next_cleaning"),
                constructionYear = if (json.isNull("construction_year")) null else json.optInt("construction_year"),
                imageUrl = if (json.isNull("image")) null else json.optString("image")
            )
        }
    }
}

data class DamageReport(
    val id: Int,
    val waterSourceId: Int,
    val waterSourceName: String,
    val reportedById: Int?,
    val reportedByName: String,
    val reportDate: String,
    val title: String,
    val description: String,
    val priority: String,
    val status: String,
    val latitude: Double?,
    val longitude: Double?,
    val images: List<String>,
    val assignedToId: Int?,
    val assignedToName: String?,
    val resolvedAt: String?,
    val resolutionNotes: String?,
    val villageApprovedById: Int?,
    val villageApprovedAt: String?,
    val forwardedById: Int?,
    val forwardedAt: String?,
    val rejectionReason: String?
) {
    val priorityDisplay: String
        get() = when (priority) {
            "low" -> "Ndogo"
            "medium" -> "Wastani"
            "high" -> "Kubwa"
            "critical" -> "Dharura"
            else -> priority
        }

    val statusDisplay: String
        get() = when (status) {
            "pending_village" -> "Inasubiri Idhini ya Mwenyekiti"
            "village_approved" -> "Imeidhinishwa na Mwenyekiti"
            "forwarded_to_district" -> "Imetumwa kwa Wilaya"
            "rejected" -> "Imekataliwa"
            "assigned" -> "Imepewa Wafanyakazi"
            "in_progress" -> "Inafanywa Kazi"
            "resolved" -> "Imetatuliwa"
            "closed" -> "Imefungwa"
            "pending" -> "Inasubiri (Zamani)"
            else -> status
        }

    companion object {
        fun fromJson(json: JSONObject): DamageReport {
            val sourceObj = json.optJSONObject("water_source_details") ?: json.optJSONObject("water_source")
            val sourceId = sourceObj?.optInt("id") ?: json.optInt("water_source", 0)
            val sourceName = sourceObj?.optString("name") ?: "Chanzo Kisichojulikana"

            val reportedByObj = json.optJSONObject("reported_by_details") ?: json.optJSONObject("reported_by")
            val reportedById = reportedByObj?.optInt("id") ?: if (json.isNull("reported_by")) null else json.optInt("reported_by")
            val reportedByName = reportedByObj?.optString("username") ?: "Mwananchi"

            val assignedToObj = json.optJSONObject("assigned_to_details") ?: json.optJSONObject("assigned_to")
            val assignedToId = assignedToObj?.optInt("id") ?: if (json.isNull("assigned_to")) null else json.optInt("assigned_to")
            val assignedToName = assignedToObj?.optString("username")

            val imgArray = json.optJSONArray("images")
            val imageList = mutableListOf<String>()
            if (imgArray != null) {
                for (i in 0 until imgArray.length()) {
                    imageList.add(imgArray.getString(i))
                }
            }

            return DamageReport(
                id = json.getInt("id"),
                waterSourceId = sourceId,
                waterSourceName = sourceName,
                reportedById = reportedById,
                reportedByName = reportedByName,
                reportDate = json.optString("report_date", ""),
                title = json.getString("title"),
                description = json.getString("description"),
                priority = json.optString("priority", "medium"),
                status = json.optString("status", "pending_village"),
                latitude = if (json.isNull("latitude")) null else json.optDouble("latitude"),
                longitude = if (json.isNull("longitude")) null else json.optDouble("longitude"),
                images = imageList,
                assignedToId = assignedToId,
                assignedToName = assignedToName,
                resolvedAt = if (json.isNull("resolved_at")) null else json.optString("resolved_at"),
                resolutionNotes = json.optString("resolution_notes", ""),
                villageApprovedById = if (json.isNull("village_approved_by")) null else json.optInt("village_approved_by"),
                villageApprovedAt = if (json.isNull("village_approved_at")) null else json.optString("village_approved_at"),
                forwardedById = if (json.isNull("forwarded_by")) null else json.optInt("forwarded_by"),
                forwardedAt = if (json.isNull("forwarded_at")) null else json.optString("forwarded_at"),
                rejectionReason = json.optString("rejection_reason", "")
            )
        }
    }
}

data class QualityReport(
    val id: Int,
    val waterSourceId: Int,
    val testedById: Int,
    val testedByName: String,
    val testDate: String,
    val phLevel: Double,
    val bacteriaCount: Int,
    val ironLevel: Double,
    val turbidity: Double,
    val chlorineLevel: Double?,
    val isSafe: Boolean,
    val notes: String
) {
    companion object {
        fun fromJson(json: JSONObject): QualityReport {
            val testedByObj = json.optJSONObject("tested_by_details") ?: json.optJSONObject("tested_by")
            val testedById = testedByObj?.optInt("id") ?: json.optInt("tested_by", 0)
            val testedByName = testedByObj?.optString("username") ?: "Mtaalamu"

            return QualityReport(
                id = json.getInt("id"),
                waterSourceId = json.optInt("water_source", 0),
                testedById = testedById,
                testedByName = testedByName,
                testDate = json.optString("test_date", ""),
                phLevel = json.getDouble("ph_level"),
                bacteriaCount = json.getInt("bacteria_count"),
                ironLevel = json.getDouble("iron_level"),
                turbidity = json.getDouble("turbidity"),
                chlorineLevel = if (json.isNull("chlorine_level")) null else json.optDouble("chlorine_level"),
                isSafe = json.getBoolean("is_safe"),
                notes = json.optString("notes", "")
            )
        }
    }
}

data class Alert(
    val id: Int,
    val waterSourceId: Int?,
    val alertType: String,
    val message: String,
    val isRead: Boolean,
    val createdAt: String
) {
    val alertTypeDisplay: String
        get() = when (alertType) {
            "quality_drop" -> "Ubora Umeshuka"
            "source_dry" -> "Chanzo Kimekauka"
            "damage" -> "Uharibifu"
            "maintenance_due" -> "Usafishaji Umekaribia"
            "general" -> "Ujumbe Mkuu"
            else -> alertType
        }

    companion object {
        fun fromJson(json: JSONObject): Alert {
            return Alert(
                id = json.getInt("id"),
                waterSourceId = if (json.isNull("water_source")) null else json.optInt("water_source"),
                alertType = json.getString("alert_type"),
                message = json.getString("message"),
                isRead = json.optBoolean("is_read", false),
                createdAt = json.optString("created_at", "")
            )
        }
    }
}

data class Message(
    val id: Int,
    val senderId: Int,
    val senderName: String,
    val recipientId: Int,
    val recipientName: String,
    val subject: String,
    val body: String,
    val relatedReportId: Int?,
    val isRead: Boolean,
    val createdAt: String
) {
    companion object {
        fun fromJson(json: JSONObject): Message {
            val senderObj = json.optJSONObject("sender_details") ?: json.optJSONObject("sender")
            val senderId = senderObj?.optInt("id") ?: json.optInt("sender", 0)
            val senderName = senderObj?.optString("username") ?: "Mtumaji"

            val recipientObj = json.optJSONObject("recipient_details") ?: json.optJSONObject("recipient")
            val recipientId = recipientObj?.optInt("id") ?: json.optInt("recipient", 0)
            val recipientName = recipientObj?.optString("username") ?: "Mpokeaji"

            return Message(
                id = json.getInt("id"),
                senderId = senderId,
                senderName = senderName,
                recipientId = recipientId,
                recipientName = recipientName,
                subject = json.optString("subject", ""),
                body = json.getString("body"),
                relatedReportId = if (json.isNull("related_report")) null else json.optInt("related_report"),
                isRead = json.optBoolean("is_read", false),
                createdAt = json.optString("created_at", "")
            )
        }
    }
}

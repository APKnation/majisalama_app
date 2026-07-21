package com.example.myapplication.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

object ApiClient {
    private const val TAG = "ApiClient"

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    var accessToken: String? = null
    var currentUser: User? = null

    private suspend fun fetchCurrentUser(uid: String): User? {
        return try {
            val doc = db.collection("users").document(uid).get().await()
            if (!doc.exists()) return null
            User(
                id = doc.getLong("id")?.toInt() ?: 0,
                username = doc.getString("username") ?: "",
                email = doc.getString("email") ?: "",
                firstName = doc.getString("firstName") ?: "",
                lastName = doc.getString("lastName") ?: "",
                role = doc.getString("role") ?: "citizen",
                phone = doc.getString("phone") ?: "",
                villageId = doc.getLong("villageId")?.toInt(),
                villageName = doc.getString("villageName")
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching user", e)
            null
        }
    }

    suspend fun login(username: String, securePassword: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            // Note: Firebase Auth uses email for sign-in by default
            // If the user inputs a non-email username, we could query Firestore to find the email,
            // but for this integration we assume 'username' field on the UI is actually the email.
            val email = if (username.contains("@")) username else "$username@example.com"
            val result = auth.signInWithEmailAndPassword(email, securePassword).await()
            val authUser = result.user ?: throw Exception("Login failed")

            val dbUser = fetchCurrentUser(authUser.uid) ?: throw Exception("User data missing from Firestore")
            accessToken = authUser.uid
            currentUser = dbUser
            Result.success(dbUser)
        } catch (e: Exception) {
            Log.e(TAG, "Login error", e)
            Result.failure(e)
        }
    }

    suspend fun register(
        username: String,
        email: String,
        securePassword: String,
        firstName: String,
        lastName: String,
        role: String,
        phone: String,
        villageId: Int?
    ): Result<User> = withContext(Dispatchers.IO) {
        try {
            val result = auth.createUserWithEmailAndPassword(email, securePassword).await()
            val authUser = result.user ?: throw Exception("Registration failed")

            val newUser = User(
                id = Math.abs(authUser.uid.hashCode()),
                username = username,
                email = email,
                firstName = firstName,
                lastName = lastName,
                role = role,
                phone = phone,
                villageId = villageId,
                villageName = "Assigned Village"
            )

            val userMap = hashMapOf(
                "id" to newUser.id,
                "username" to newUser.username,
                "email" to newUser.email,
                "firstName" to newUser.firstName,
                "lastName" to newUser.lastName,
                "role" to newUser.role,
                "phone" to newUser.phone,
                "villageId" to newUser.villageId,
                "villageName" to newUser.villageName
            )
            
            db.collection("users").document(authUser.uid).set(userMap).await()

            accessToken = authUser.uid
            currentUser = newUser

            Result.success(newUser)
        } catch (e: Exception) {
            Log.e(TAG, "Registration error", e)
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
        accessToken = null
        currentUser = null
    }

    suspend fun getVillages(): Result<List<Village>> = withContext(Dispatchers.IO) {
        try {
            val snapshot = db.collection("villages").get().await()
            val villages = snapshot.documents.map { doc ->
                Village(
                    id = doc.getLong("id")?.toInt() ?: 0,
                    name = doc.getString("name") ?: "",
                    district = doc.getString("district") ?: "",
                    region = doc.getString("region") ?: "",
                    population = doc.getLong("population")?.toInt() ?: 0,
                    latitude = doc.getDouble("latitude"),
                    longitude = doc.getDouble("longitude")
                )
            }
            Result.success(villages)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWaterSources(villageId: Int? = null): Result<List<WaterSource>> = withContext(Dispatchers.IO) {
        try {
            val query = if (villageId != null) {
                db.collection("waterSources").whereEqualTo("villageId", villageId)
            } else {
                db.collection("waterSources")
            }
            val snapshot = query.get().await()
            val sources = snapshot.documents.map { doc ->
                WaterSource(
                    id = doc.getLong("id")?.toInt() ?: 0,
                    name = doc.getString("name") ?: "",
                    sourceType = doc.getString("sourceType") ?: "",
                    villageId = doc.getLong("villageId")?.toInt() ?: 0,
                    villageName = doc.getString("villageName") ?: "",
                    latitude = doc.getDouble("latitude"),
                    longitude = doc.getDouble("longitude"),
                    status = doc.getString("status") ?: "safe",
                    phLevel = doc.getDouble("phLevel"),
                    bacteriaCount = doc.getLong("bacteriaCount")?.toInt(),
                    ironLevel = doc.getDouble("ironLevel"),
                    turbidity = doc.getDouble("turbidity"),
                    lastTested = doc.getString("lastTested"),
                    managedById = doc.getLong("managedById")?.toInt(),
                    managedByName = doc.getString("managedByName"),
                    lastCleaned = doc.getString("lastCleaned"),
                    nextCleaning = doc.getString("nextCleaning"),
                    constructionYear = doc.getLong("constructionYear")?.toInt(),
                    imageUrl = doc.getString("imageUrl")
                )
            }
            Result.success(sources)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNearbyWaterSources(lat: Double, lng: Double, radius: Double): Result<List<WaterSource>> = withContext(Dispatchers.IO) {
        // Simple mock distance filter based on fetched sources
        val sources = getWaterSources().getOrDefault(emptyList())
        val filtered = sources.filter {
            val srcLat = it.latitude ?: return@filter false
            val srcLng = it.longitude ?: return@filter false
            Math.abs(srcLat - lat) < 0.1 && Math.abs(srcLng - lng) < 0.1
        }
        Result.success(filtered)
    }

    suspend fun getDamageReports(): Result<List<DamageReport>> = withContext(Dispatchers.IO) {
        try {
            val snapshot = db.collection("damageReports").get().await()
            val reports = snapshot.documents.map { doc ->
                DamageReport(
                    id = doc.getLong("id")?.toInt() ?: 0,
                    waterSourceId = doc.getLong("waterSourceId")?.toInt() ?: 0,
                    waterSourceName = doc.getString("waterSourceName") ?: "",
                    reportedById = doc.getLong("reportedById")?.toInt(),
                    reportedByName = doc.getString("reportedByName") ?: "",
                    reportDate = doc.getString("reportDate") ?: "",
                    title = doc.getString("title") ?: "",
                    description = doc.getString("description") ?: "",
                    priority = doc.getString("priority") ?: "medium",
                    status = doc.getString("status") ?: "pending_village",
                    latitude = doc.getDouble("latitude"),
                    longitude = doc.getDouble("longitude"),
                    images = (doc.get("images") as? List<String>) ?: emptyList(),
                    assignedToId = doc.getLong("assignedToId")?.toInt(),
                    assignedToName = doc.getString("assignedToName"),
                    resolvedAt = doc.getString("resolvedAt"),
                    resolutionNotes = doc.getString("resolutionNotes"),
                    villageApprovedById = doc.getLong("villageApprovedById")?.toInt(),
                    villageApprovedAt = doc.getString("villageApprovedAt"),
                    forwardedById = doc.getLong("forwardedById")?.toInt(),
                    forwardedAt = doc.getString("forwardedAt"),
                    rejectionReason = doc.getString("rejectionReason")
                )
            }
            Result.success(reports)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun reportDamage(
        waterSourceId: Int,
        title: String,
        description: String,
        priority: String
    ): Result<DamageReport> = withContext(Dispatchers.IO) {
        try {
            val sources = getWaterSources().getOrDefault(emptyList())
            val source = sources.find { it.id == waterSourceId } ?: throw Exception("Source not found")
            val reporterId = currentUser?.id
            val reporterName = currentUser?.username ?: "Anonymous"
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            
            val docRef = db.collection("damageReports").document()
            val newId = Math.abs(docRef.id.hashCode())

            val reportMap = hashMapOf(
                "id" to newId,
                "waterSourceId" to source.id,
                "waterSourceName" to source.name,
                "reportedById" to reporterId,
                "reportedByName" to reporterName,
                "reportDate" to today,
                "title" to title,
                "description" to description,
                "priority" to priority,
                "status" to "pending_village",
                "latitude" to source.latitude,
                "longitude" to source.longitude,
                "images" to emptyList<String>(),
                "assignedToId" to null,
                "assignedToName" to null,
                "resolvedAt" to null,
                "resolutionNotes" to null,
                "villageApprovedById" to null,
                "villageApprovedAt" to null,
                "forwardedById" to null,
                "forwardedAt" to null,
                "rejectionReason" to null
            )
            
            docRef.set(reportMap).await()

            val report = DamageReport(
                id = newId, waterSourceId = source.id, waterSourceName = source.name,
                reportedById = reporterId, reportedByName = reporterName, reportDate = today,
                title = title, description = description, priority = priority, status = "pending_village",
                latitude = source.latitude, longitude = source.longitude, images = emptyList(),
                assignedToId = null, assignedToName = null, resolvedAt = null, resolutionNotes = null,
                villageApprovedById = null, villageApprovedAt = null, forwardedById = null,
                forwardedAt = null, rejectionReason = null
            )
            Result.success(report)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun approveDamageReport(id: Int): Result<Boolean> = withContext(Dispatchers.IO) {
        updateDamageReportField(id, mapOf(
            "status" to "village_approved",
            "villageApprovedById" to currentUser?.id,
            "villageApprovedAt" to SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        ))
    }

    suspend fun rejectDamageReport(id: Int, reason: String): Result<Boolean> = withContext(Dispatchers.IO) {
        updateDamageReportField(id, mapOf(
            "status" to "rejected",
            "rejectionReason" to reason
        ))
    }

    suspend fun forwardToDistrict(id: Int): Result<Boolean> = withContext(Dispatchers.IO) {
        updateDamageReportField(id, mapOf(
            "status" to "forwarded_to_district",
            "forwardedById" to currentUser?.id,
            "forwardedAt" to SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        ))
    }

    suspend fun assignDamageReport(id: Int, workerId: Int): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val userDoc = db.collection("users").whereEqualTo("id", workerId).get().await().documents.firstOrNull()
                ?: throw Exception("Worker not found")
            val workerName = userDoc.getString("username")
            updateDamageReportField(id, mapOf(
                "status" to "assigned",
                "assignedToId" to workerId,
                "assignedToName" to workerName
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun startWorkDamageReport(id: Int): Result<Boolean> = withContext(Dispatchers.IO) {
        updateDamageReportField(id, mapOf("status" to "in_progress"))
    }

    suspend fun resolveDamageReport(id: Int, notes: String): Result<Boolean> = withContext(Dispatchers.IO) {
        updateDamageReportField(id, mapOf(
            "status" to "resolved",
            "resolutionNotes" to notes,
            "resolvedAt" to SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        ))
    }

    private suspend fun updateDamageReportField(id: Int, updates: Map<String, Any?>): Result<Boolean> {
        return try {
            val snapshot = db.collection("damageReports").whereEqualTo("id", id).get().await()
            val doc = snapshot.documents.firstOrNull() ?: throw Exception("Report not found")
            doc.reference.update(updates).await()
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating report", e)
            Result.failure(e)
        }
    }

    suspend fun submitQualityReport(
        waterSourceId: Int, phLevel: Double, bacteriaCount: Int, ironLevel: Double,
        turbidity: Double, chlorineLevel: Double?, notes: String
    ): Result<QualityReport> = withContext(Dispatchers.IO) {
        try {
            val reporter = currentUser ?: throw Exception("Login required")
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val isSafe = phLevel in 6.5..8.5 && bacteriaCount <= 10
            
            val docRef = db.collection("qualityReports").document()
            val newId = Math.abs(docRef.id.hashCode())

            val reportMap = hashMapOf(
                "id" to newId,
                "waterSourceId" to waterSourceId,
                "testedById" to reporter.id,
                "testedByName" to reporter.username,
                "testDate" to today,
                "phLevel" to phLevel,
                "bacteriaCount" to bacteriaCount,
                "ironLevel" to ironLevel,
                "turbidity" to turbidity,
                "chlorineLevel" to chlorineLevel,
                "isSafe" to isSafe,
                "notes" to notes
            )
            docRef.set(reportMap).await()

            // Update water source status
            val srcSnapshot = db.collection("waterSources").whereEqualTo("id", waterSourceId).get().await()
            srcSnapshot.documents.firstOrNull()?.reference?.update(
                mapOf(
                    "phLevel" to phLevel,
                    "bacteriaCount" to bacteriaCount,
                    "ironLevel" to ironLevel,
                    "turbidity" to turbidity,
                    "lastTested" to today,
                    "status" to if (isSafe) "safe" else "unsafe"
                )
            )

            Result.success(QualityReport(
                newId, waterSourceId, reporter.id, reporter.username, today,
                phLevel, bacteriaCount, ironLevel, turbidity, chlorineLevel, isSafe, notes
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getQualityReports(waterSourceId: Int): Result<List<QualityReport>> = withContext(Dispatchers.IO) {
        try {
            val snapshot = db.collection("qualityReports").whereEqualTo("waterSourceId", waterSourceId).get().await()
            val reports = snapshot.documents.map { doc ->
                QualityReport(
                    id = doc.getLong("id")?.toInt() ?: 0,
                    waterSourceId = doc.getLong("waterSourceId")?.toInt() ?: 0,
                    testedById = doc.getLong("testedById")?.toInt() ?: 0,
                    testedByName = doc.getString("testedByName") ?: "",
                    testDate = doc.getString("testDate") ?: "",
                    phLevel = doc.getDouble("phLevel") ?: 0.0,
                    bacteriaCount = doc.getLong("bacteriaCount")?.toInt() ?: 0,
                    ironLevel = doc.getDouble("ironLevel") ?: 0.0,
                    turbidity = doc.getDouble("turbidity") ?: 0.0,
                    chlorineLevel = doc.getDouble("chlorineLevel"),
                    isSafe = doc.getBoolean("isSafe") ?: false,
                    notes = doc.getString("notes") ?: ""
                )
            }
            Result.success(reports)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAlerts(): Result<List<Alert>> = withContext(Dispatchers.IO) {
        try {
            val snapshot = db.collection("alerts").get().await()
            val alertsList = snapshot.documents.map { doc ->
                Alert(
                    id = doc.getLong("id")?.toInt() ?: 0,
                    waterSourceId = doc.getLong("waterSourceId")?.toInt(),
                    alertType = doc.getString("alertType") ?: "",
                    message = doc.getString("message") ?: "",
                    isRead = doc.getBoolean("isRead") ?: false,
                    createdAt = doc.getString("createdAt") ?: ""
                )
            }
            Result.success(alertsList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWaterOfficers(): Result<List<User>> = withContext(Dispatchers.IO) {
        try {
            val snapshot = db.collection("users").whereEqualTo("role", "water_officer").get().await()
            val officers = snapshot.documents.mapNotNull { doc ->
                User(
                    id = doc.getLong("id")?.toInt() ?: 0,
                    username = doc.getString("username") ?: "",
                    email = doc.getString("email") ?: "",
                    firstName = doc.getString("firstName") ?: "",
                    lastName = doc.getString("lastName") ?: "",
                    role = doc.getString("role") ?: "water_officer",
                    phone = doc.getString("phone") ?: "",
                    villageId = doc.getLong("villageId")?.toInt(),
                    villageName = doc.getString("villageName")
                )
            }
            Result.success(officers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun predictWaterDemand(
        district: String,
        temperature: Double,
        rainfall: Double,
        population: Double,
        month: String
    ): Result<Double> = withContext(Dispatchers.IO) {
        // Kept as mock for now unless there's a Firebase ML endpoint
        val base = population * 0.15
        val tempMultiplier = if (temperature > 30) 1.2 else 1.0
        val demand = base * tempMultiplier
        Result.success(demand)
    }
}

package com.example.myapplication.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

object ApiClient {
    private const val TAG = "ApiClient"

    var accessToken: String? = null
    var currentUser: User? = null

    // Mock Data Store
    private val users = mutableListOf(
        User(1, "admin", "", "System", "Admin", "admin", "", null, null).apply { password = "admin123" },
        User(2, "citizen1", "", "Mwananchi", "Mmoja", "citizen", "", 1, "Kijiji A").apply { password = "pass1234" },
        User(3, "leader1", "", "Kiongozi", "Mkuu", "village_leader", "", 1, "Kijiji A").apply { password = "pass1234" },
        User(4, "officer1", "", "Afisa", "Maji", "water_officer", "", null, null).apply { password = "pass1234" }
    )

    // For mock auth check (not part of original User model, but needed for local auth)
    private var User.password: String
        get() = this.username + "pwd" // dummy
        set(value) {}

    private val authMap = mutableMapOf(
        "admin" to "admin123",
        "citizen1" to "pass1234",
        "leader1" to "pass1234",
        "officer1" to "pass1234"
    )

    private val villages = mutableListOf(
        Village(1, "Kijiji A", "Ilala", "Dar es Salaam", 5000, -6.81, 39.28),
        Village(2, "Kijiji B", "Kinondoni", "Dar es Salaam", 8000, -6.78, 39.25)
    )

    private val waterSources = mutableListOf(
        WaterSource(1, "Kisima cha Kati", "deep_well", 1, "Kijiji A", -6.812, 39.282, "safe", 7.2, 5, 0.1, 1.2, "2026-07-01", null, null, "2026-06-15", "2026-12-15", 2015, null),
        WaterSource(2, "Mto Msimbazi", "river", 1, "Kijiji A", -6.815, 39.285, "unsafe", 6.5, 50, 0.5, 5.0, "2026-07-10", null, null, null, null, null, null),
        WaterSource(3, "Bomba la Shule", "borehole", 2, "Kijiji B", -6.782, 39.252, "caution", 7.0, 12, 0.2, 2.5, "2026-07-12", 4, "officer1", "2026-01-10", "2026-08-10", 2020, null)
    )

    private val damageReports = mutableListOf<DamageReport>(
        DamageReport(
            id = 1, waterSourceId = 2, waterSourceName = "Mto Msimbazi",
            reportedById = 2, reportedByName = "citizen1",
            reportDate = "2026-07-14", title = "Maji Yenye Rangi ya Njano",
            description = "Maji yamebadilika rangi na hayana harufu nzuri. Wananchi hawakunywa.",
            priority = "high", status = "pending_village",
            latitude = -6.815, longitude = 39.285, images = emptyList(),
            assignedToId = null, assignedToName = null, resolvedAt = null,
            resolutionNotes = null, villageApprovedById = null, villageApprovedAt = null,
            forwardedById = null, forwardedAt = null, rejectionReason = null
        ),
        DamageReport(
            id = 2, waterSourceId = 3, waterSourceName = "Bomba la Shule",
            reportedById = 2, reportedByName = "citizen1",
            reportDate = "2026-07-13", title = "Bomba Limevunjika Kasoro",
            description = "Bomba kuu limevunjika na maji yanapotea. Shule haina maji tangu jana.",
            priority = "critical", status = "village_approved",
            latitude = -6.782, longitude = 39.252, images = emptyList(),
            assignedToId = 4, assignedToName = "officer1", resolvedAt = null,
            resolutionNotes = null, villageApprovedById = 3, villageApprovedAt = "2026-07-13",
            forwardedById = null, forwardedAt = null, rejectionReason = null
        ),
        DamageReport(
            id = 3, waterSourceId = 1, waterSourceName = "Kisima cha Kati",
            reportedById = 2, reportedByName = "citizen1",
            reportDate = "2026-07-12", title = "Kisima Kimezama Ndani",
            description = "Kizingiti cha kisima kimevunjika. Hatari kwa watoto wanaozunguka eneo.",
            priority = "medium", status = "assigned",
            latitude = -6.812, longitude = 39.282, images = emptyList(),
            assignedToId = 4, assignedToName = "officer1", resolvedAt = null,
            resolutionNotes = null, villageApprovedById = 3, villageApprovedAt = "2026-07-12",
            forwardedById = null, forwardedAt = null, rejectionReason = null
        ),
        DamageReport(
            id = 4, waterSourceId = 2, waterSourceName = "Mto Msimbazi",
            reportedById = 2, reportedByName = "citizen1",
            reportDate = "2026-07-10", title = "Taka Karibu na Chanzo",
            description = "Kuna takataka nyingi zimezingira chanzo cha maji. Inachafua mazingira.",
            priority = "low", status = "resolved",
            latitude = -6.815, longitude = 39.285, images = emptyList(),
            assignedToId = 4, assignedToName = "officer1",
            resolvedAt = "2026-07-11", resolutionNotes = "Taka zimeondolewa na timu ya usafi.",
            villageApprovedById = 3, villageApprovedAt = "2026-07-10",
            forwardedById = null, forwardedAt = null, rejectionReason = null
        ),
        DamageReport(
            id = 5, waterSourceId = 3, waterSourceName = "Bomba la Shule",
            reportedById = 2, reportedByName = "citizen1",
            reportDate = "2026-07-09", title = "Msururu wa Maji Usiku",
            description = "Maji yanakimbia usiku kutoka kwa bomba. Hasara kubwa ya maji kwa kijiji.",
            priority = "high", status = "in_progress",
            latitude = -6.782, longitude = 39.252, images = emptyList(),
            assignedToId = 4, assignedToName = "officer1", resolvedAt = null,
            resolutionNotes = null, villageApprovedById = 3, villageApprovedAt = "2026-07-09",
            forwardedById = null, forwardedAt = null, rejectionReason = null
        )
    )
    private var nextReportId = 6

    private val qualityReports = mutableListOf<QualityReport>()
    private var nextQualityId = 1

    private val alerts = mutableListOf(
        Alert(1, 2, "quality_drop", "Ubora wa maji Mto Msimbazi umeshuka sana. Tafadhali chukua tahadhari.", false, "2026-07-10T10:00:00Z"),
        Alert(2, 3, "maintenance_due", "Bomba la Shule linahitaji usafishaji mwezi ujao.", false, "2026-07-12T08:00:00Z")
    )

    private suspend fun simulateNetwork() {
        delay(300) // Artificial delay for UX
    }

    suspend fun login(username: String, securePassword: String): Result<User> = withContext(Dispatchers.IO) {
        simulateNetwork()
        try {
            val validPassword = authMap[username]
            if (validPassword != null && validPassword == securePassword) {
                val user = users.find { it.username == username } ?: throw Exception("User data missing")
                accessToken = "mock_token_${user.id}"
                currentUser = user
                Result.success(user)
            } else {
                throw Exception("Taarifa si sahihi (Invalid credentials)")
            }
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
        simulateNetwork()
        try {
            if (users.any { it.username == username }) {
                throw Exception("Username tayari inatumika")
            }
            val villageName = villages.find { it.id == villageId }?.name
            val newUser = User(
                id = users.size + 1,
                username = username,
                email = email,
                firstName = firstName,
                lastName = lastName,
                role = role,
                phone = phone,
                villageId = villageId,
                villageName = villageName
            )
            users.add(newUser)
            authMap[username] = securePassword
            
            // Auto login
            login(username, securePassword)
        } catch (e: Exception) {
            Log.e(TAG, "Registration error", e)
            Result.failure(e)
        }
    }

    fun logout() {
        accessToken = null
        currentUser = null
    }

    suspend fun getVillages(): Result<List<Village>> = withContext(Dispatchers.IO) {
        simulateNetwork()
        Result.success(villages.toList())
    }

    suspend fun getWaterSources(villageId: Int? = null): Result<List<WaterSource>> = withContext(Dispatchers.IO) {
        simulateNetwork()
        val list = if (villageId != null) {
            waterSources.filter { it.villageId == villageId }
        } else {
            waterSources.toList()
        }
        Result.success(list)
    }

    suspend fun getNearbyWaterSources(lat: Double, lng: Double, radius: Double): Result<List<WaterSource>> = withContext(Dispatchers.IO) {
        simulateNetwork()
        // Mock distance filter (returns all for now to keep it simple, or rough bounding box)
        val list = waterSources.filter {
            val srcLat = it.latitude ?: return@filter false
            val srcLng = it.longitude ?: return@filter false
            Math.abs(srcLat - lat) < 0.1 && Math.abs(srcLng - lng) < 0.1
        }
        Result.success(list)
    }

    suspend fun getDamageReports(): Result<List<DamageReport>> = withContext(Dispatchers.IO) {
        simulateNetwork()
        Result.success(damageReports.toList())
    }

    suspend fun reportDamage(
        waterSourceId: Int,
        title: String,
        description: String,
        priority: String
    ): Result<DamageReport> = withContext(Dispatchers.IO) {
        simulateNetwork()
        try {
            val source = waterSources.find { it.id == waterSourceId } ?: throw Exception("Chanzo hakijapatikana")
            val reporterId = currentUser?.id
            val reporterName = currentUser?.username ?: "Mwananchi (Anonymous)"
            
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val today = dateFormat.format(Date())

            val report = DamageReport(
                id = nextReportId++,
                waterSourceId = source.id,
                waterSourceName = source.name,
                reportedById = reporterId,
                reportedByName = reporterName,
                reportDate = today,
                title = title,
                description = description,
                priority = priority,
                status = "pending_village",
                latitude = source.latitude,
                longitude = source.longitude,
                images = emptyList(),
                assignedToId = null,
                assignedToName = null,
                resolvedAt = null,
                resolutionNotes = null,
                villageApprovedById = null,
                villageApprovedAt = null,
                forwardedById = null,
                forwardedAt = null,
                rejectionReason = null
            )
            damageReports.add(report)
            Result.success(report)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun approveDamageReport(id: Int): Result<Boolean> = withContext(Dispatchers.IO) {
        simulateNetwork()
        try {
            val idx = damageReports.indexOfFirst { it.id == id }
            if (idx == -1) throw Exception("Ripoti haipatikani")
            val report = damageReports[idx]
            damageReports[idx] = report.copy(
                status = "village_approved",
                villageApprovedById = currentUser?.id,
                villageApprovedAt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            )
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun rejectDamageReport(id: Int, reason: String): Result<Boolean> = withContext(Dispatchers.IO) {
        simulateNetwork()
        try {
            val idx = damageReports.indexOfFirst { it.id == id }
            if (idx == -1) throw Exception("Ripoti haipatikani")
            val report = damageReports[idx]
            damageReports[idx] = report.copy(
                status = "rejected",
                rejectionReason = reason
            )
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun forwardToDistrict(id: Int): Result<Boolean> = withContext(Dispatchers.IO) {
        simulateNetwork()
        try {
            val idx = damageReports.indexOfFirst { it.id == id }
            if (idx == -1) throw Exception("Ripoti haipatikani")
            val report = damageReports[idx]
            damageReports[idx] = report.copy(
                status = "forwarded_to_district",
                forwardedById = currentUser?.id,
                forwardedAt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            )
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun assignDamageReport(id: Int, workerId: Int): Result<Boolean> = withContext(Dispatchers.IO) {
        simulateNetwork()
        try {
            val idx = damageReports.indexOfFirst { it.id == id }
            if (idx == -1) throw Exception("Ripoti haipatikani")
            val worker = users.find { it.id == workerId } ?: throw Exception("Mfanyakazi hajapatikana")
            val report = damageReports[idx]
            damageReports[idx] = report.copy(
                status = "assigned",
                assignedToId = worker.id,
                assignedToName = worker.username
            )
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun startWorkDamageReport(id: Int): Result<Boolean> = withContext(Dispatchers.IO) {
        simulateNetwork()
        try {
            val idx = damageReports.indexOfFirst { it.id == id }
            if (idx == -1) throw Exception("Ripoti haipatikani")
            val report = damageReports[idx]
            damageReports[idx] = report.copy(status = "in_progress")
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resolveDamageReport(id: Int, notes: String): Result<Boolean> = withContext(Dispatchers.IO) {
        simulateNetwork()
        try {
            val idx = damageReports.indexOfFirst { it.id == id }
            if (idx == -1) throw Exception("Ripoti haipatikani")
            val report = damageReports[idx]
            damageReports[idx] = report.copy(
                status = "resolved",
                resolutionNotes = notes,
                resolvedAt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            )
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitQualityReport(
        waterSourceId: Int,
        phLevel: Double,
        bacteriaCount: Int,
        ironLevel: Double,
        turbidity: Double,
        chlorineLevel: Double?,
        notes: String
    ): Result<QualityReport> = withContext(Dispatchers.IO) {
        simulateNetwork()
        try {
            val reporter = currentUser ?: throw Exception("Unahitaji kuingia")
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val isSafe = phLevel in 6.5..8.5 && bacteriaCount <= 10

            val report = QualityReport(
                id = nextQualityId++,
                waterSourceId = waterSourceId,
                testedById = reporter.id,
                testedByName = reporter.username,
                testDate = dateFormat.format(Date()),
                phLevel = phLevel,
                bacteriaCount = bacteriaCount,
                ironLevel = ironLevel,
                turbidity = turbidity,
                chlorineLevel = chlorineLevel,
                isSafe = isSafe,
                notes = notes
            )
            qualityReports.add(report)

            // Update water source
            val idx = waterSources.indexOfFirst { it.id == waterSourceId }
            if (idx != -1) {
                val source = waterSources[idx]
                waterSources[idx] = source.copy(
                    phLevel = phLevel,
                    bacteriaCount = bacteriaCount,
                    ironLevel = ironLevel,
                    turbidity = turbidity,
                    lastTested = report.testDate,
                    status = if (isSafe) "safe" else "unsafe"
                )
            }
            Result.success(report)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getQualityReports(waterSourceId: Int): Result<List<QualityReport>> = withContext(Dispatchers.IO) {
        simulateNetwork()
        Result.success(qualityReports.filter { it.waterSourceId == waterSourceId })
    }

    suspend fun getAlerts(): Result<List<Alert>> = withContext(Dispatchers.IO) {
        simulateNetwork()
        Result.success(alerts.toList())
    }

    suspend fun getWaterOfficers(): Result<List<User>> = withContext(Dispatchers.IO) {
        simulateNetwork()
        Result.success(users.filter { it.role == "water_officer" })
    }

    suspend fun predictWaterDemand(
        district: String,
        temperature: Double,
        rainfall: Double,
        population: Double,
        month: String
    ): Result<Double> = withContext(Dispatchers.IO) {
        simulateNetwork()
        // Mock ML prediction logic based on population and temp
        val base = population * 0.15
        val tempMultiplier = if (temperature > 30) 1.2 else 1.0
        val demand = base * tempMultiplier
        Result.success(demand)
    }
}

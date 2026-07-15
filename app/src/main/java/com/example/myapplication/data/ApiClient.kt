package com.example.myapplication.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

object ApiClient {
    private const val TAG = "ApiClient"

    // Default URL to connect from emulator to local Django host
    var baseUrl = "http://10.0.2.2:8000"
        private set

    var accessToken: String? = null
    var currentUser: User? = null

    fun setBaseUrl(url: String) {
        baseUrl = url.trim().removeSuffix("/")
    }

    private suspend fun makeRequest(
        method: String,
        endpoint: String,
        body: String? = null
    ): String = withContext(Dispatchers.IO) {
        val url = URL("$baseUrl$endpoint")
        Log.d(TAG, "Request: $method $url")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = method
        conn.connectTimeout = 8000
        conn.readTimeout = 8000
        conn.setRequestProperty("Content-Type", "application/json")
        conn.setRequestProperty("Accept", "application/json")

        accessToken?.let {
            conn.setRequestProperty("Authorization", "Bearer $it")
        }

        if (body != null && (method == "POST" || method == "PUT" || method == "PATCH")) {
            conn.doOutput = true
            val os: OutputStream = conn.outputStream
            os.write(body.toByteArray(Charsets.UTF_8))
            os.flush()
            os.close()
        }

        val responseCode = conn.responseCode
        Log.d(TAG, "Response Code: $responseCode")

        val stream = if (responseCode in 200..299) {
            conn.inputStream
        } else {
            conn.errorStream
        }

        val responseText = stream?.bufferedReader()?.use { it.readText() } ?: ""
        if (responseCode !in 200..299) {
            val errorMsg = try {
                JSONObject(responseText).optString("error", responseText)
            } catch (e: Exception) {
                responseText
            }
            throw Exception(errorMsg.ifEmpty { "HTTP Error $responseCode" })
        }
        responseText
    }

    suspend fun login(username: String, securePassword: String): Result<User> {
        return try {
            val payload = JSONObject().apply {
                put("username", username)
                put("password", securePassword)
            }.toString()

            val response = makeRequest("POST", "/api/auth/login/", payload)
            val json = JSONObject(response)
            
            accessToken = json.getString("access")
            val userJson = json.getJSONObject("user")
            val user = User.fromJson(userJson)
            currentUser = user
            
            Result.success(user)
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
    ): Result<User> {
        return try {
            val payload = JSONObject().apply {
                put("username", username)
                put("email", email)
                put("password", securePassword)
                put("first_name", firstName)
                put("last_name", lastName)
                put("role", role)
                put("phone", phone)
                if (villageId != null) {
                    put("village", villageId)
                }
            }.toString()

            val response = makeRequest("POST", "/api/auth/registration/", payload)
            // Django registration typically returns token info or user info.
            // If the register response format varies, we handle it. Let's do a fallback:
            val json = JSONObject(response)
            val user = if (json.has("user")) {
                User.fromJson(json.getJSONObject("user"))
            } else {
                // If it just logins or returns token directly, parse or perform auto-login
                // For safety: if registration is successful, let's login
                val loginResult = login(username, securePassword)
                if (loginResult.isSuccess) {
                    loginResult.getOrThrow()
                } else {
                    throw Exception("Registration succeeded, but auto-login failed.")
                }
            }
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Registration error", e)
            Result.failure(e)
        }
    }

    fun logout() {
        accessToken = null
        currentUser = null
    }

    suspend fun getVillages(): Result<List<Village>> {
        return try {
            val response = makeRequest("GET", "/api/villages/")
            val list = mutableListOf<Village>()
            // Check if it returns a paginated list or plain array
            val results = try {
                JSONObject(response).getJSONArray("results")
            } catch (e: Exception) {
                JSONArray(response)
            }
            for (i in 0 until results.length()) {
                list.add(Village.fromJson(results.getJSONObject(i)))
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWaterSources(villageId: Int? = null): Result<List<WaterSource>> {
        return try {
            val endpoint = if (villageId != null) {
                "/api/water-sources/?village=$villageId"
            } else {
                "/api/water-sources/"
            }
            val response = makeRequest("GET", endpoint)
            val list = mutableListOf<WaterSource>()
            val results = try {
                JSONObject(response).getJSONArray("results")
            } catch (e: Exception) {
                JSONArray(response)
            }
            for (i in 0 until results.length()) {
                list.add(WaterSource.fromJson(results.getJSONObject(i)))
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNearbyWaterSources(lat: Double, lng: Double, radius: Double): Result<List<WaterSource>> {
        return try {
            val response = makeRequest("GET", "/api/water-sources/nearby/?lat=$lat&lng=$lng&radius=$radius")
            val list = mutableListOf<WaterSource>()
            val results = try {
                JSONObject(response).getJSONArray("results")
            } catch (e: Exception) {
                JSONArray(response)
            }
            for (i in 0 until results.length()) {
                list.add(WaterSource.fromJson(results.getJSONObject(i)))
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDamageReports(): Result<List<DamageReport>> {
        return try {
            val response = makeRequest("GET", "/api/damage-reports/")
            val list = mutableListOf<DamageReport>()
            val results = try {
                JSONObject(response).getJSONArray("results")
            } catch (e: Exception) {
                JSONArray(response)
            }
            for (i in 0 until results.length()) {
                list.add(DamageReport.fromJson(results.getJSONObject(i)))
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun reportDamage(
        waterSourceId: Int,
        title: String,
        description: String,
        priority: String
    ): Result<DamageReport> {
        return try {
            val payload = JSONObject().apply {
                put("water_source", waterSourceId)
                put("title", title)
                put("description", description)
                put("priority", priority)
            }.toString()

            val response = makeRequest("POST", "/api/damage-reports/", payload)
            val report = DamageReport.fromJson(JSONObject(response))
            Result.success(report)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun approveDamageReport(id: Int): Result<Boolean> {
        return try {
            makeRequest("POST", "/api/damage-reports/$id/village_approve/")
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun rejectDamageReport(id: Int, reason: String): Result<Boolean> {
        return try {
            val payload = JSONObject().apply {
                put("reason", reason)
            }.toString()
            makeRequest("POST", "/api/damage-reports/$id/village_reject/", payload)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun forwardToDistrict(id: Int): Result<Boolean> {
        return try {
            makeRequest("POST", "/api/damage-reports/$id/forward_to_district/")
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun assignDamageReport(id: Int, workerId: Int): Result<Boolean> {
        return try {
            val payload = JSONObject().apply {
                put("worker_id", workerId)
            }.toString()
            makeRequest("POST", "/api/damage-reports/$id/assign/", payload)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun startWorkDamageReport(id: Int): Result<Boolean> {
        return try {
            makeRequest("POST", "/api/damage-reports/$id/in_progress/")
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resolveDamageReport(id: Int, notes: String): Result<Boolean> {
        return try {
            val payload = JSONObject().apply {
                put("notes", notes)
            }.toString()
            makeRequest("POST", "/api/damage-reports/$id/resolve/", payload)
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
    ): Result<QualityReport> {
        return try {
            val payload = JSONObject().apply {
                put("water_source", waterSourceId)
                put("ph_level", phLevel)
                put("bacteria_count", bacteriaCount)
                put("iron_level", ironLevel)
                put("turbidity", turbidity)
                if (chlorineLevel != null) {
                    put("chlorine_level", chlorineLevel)
                }
                put("notes", notes)
                put("is_safe", phLevel in 6.5..8.5 && bacteriaCount <= 10)
            }.toString()

            val response = makeRequest("POST", "/api/quality-reports/", payload)
            val report = QualityReport.fromJson(JSONObject(response))
            Result.success(report)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getQualityReports(waterSourceId: Int): Result<List<QualityReport>> {
        return try {
            val response = makeRequest("GET", "/api/quality-reports/?water_source=$waterSourceId")
            val list = mutableListOf<QualityReport>()
            val results = try {
                JSONObject(response).getJSONArray("results")
            } catch (e: Exception) {
                JSONArray(response)
            }
            for (i in 0 until results.length()) {
                list.add(QualityReport.fromJson(results.getJSONObject(i)))
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAlerts(): Result<List<Alert>> {
        return try {
            val response = makeRequest("GET", "/api/alerts/")
            val list = mutableListOf<Alert>()
            val results = try {
                JSONObject(response).getJSONArray("results")
            } catch (e: Exception) {
                JSONArray(response)
            }
            for (i in 0 until results.length()) {
                list.add(Alert.fromJson(results.getJSONObject(i)))
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWaterOfficers(): Result<List<User>> {
        return try {
            val response = makeRequest("GET", "/api/users/?role=water_officer")
            val list = mutableListOf<User>()
            val results = try {
                JSONObject(response).getJSONArray("results")
            } catch (e: Exception) {
                JSONArray(response)
            }
            for (i in 0 until results.length()) {
                list.add(User.fromJson(results.getJSONObject(i)))
            }
            Result.success(list)
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
    ): Result<Double> {
        return try {
            val payload = JSONObject().apply {
                put("district", district)
                put("temperature", temperature)
                put("rainfall", rainfall)
                put("population", population)
                put("month", month)
            }.toString()

            val response = makeRequest("POST", "/api/predict-demand/", payload)
            val json = JSONObject(response)
            val demand = json.getDouble("predicted_demand")
            Result.success(demand)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

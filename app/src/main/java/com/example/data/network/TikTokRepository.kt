package com.example.data.network

import android.util.Log
import com.example.data.model.TikTokAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class TikTokRepository {

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    companion object {
        private const val TAG = "TikTokRepository"
        private const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
    }

    /**
     * Cleans and sanitizes username or URL into a clean TikTok handle
     */
    fun extractUsername(input: String): String {
        var trimmed = input.trim()
        if (trimmed.isEmpty()) return ""

        // Handle URL formats like https://www.tiktok.com/@username?is_from_webapp=1
        if (trimmed.contains("tiktok.com/")) {
            val atIndex = trimmed.indexOf("@")
            if (atIndex != -1) {
                var extracted = trimmed.substring(atIndex + 1)
                val stopChars = charArrayOf('?', '/', '&', '#')
                for (ch in stopChars) {
                    val stopIndex = extracted.indexOf(ch)
                    if (stopIndex != -1) {
                        extracted = extracted.substring(0, stopIndex)
                    }
                }
                trimmed = extracted
            }
        }

        // Remove starting @
        if (trimmed.startsWith("@")) {
            trimmed = trimmed.substring(1)
        }

        return trimmed.trim()
    }

    suspend fun fetchAccount(rawInput: String): Result<TikTokAccount> = withContext(Dispatchers.IO) {
        val username = extractUsername(rawInput)
        if (username.isEmpty()) {
            return@withContext Result.failure(IllegalArgumentException("يرجى إدخال اسم مستخدم أو رابط صالح"))
        }

        val profileUrl = "https://www.tiktok.com/@$username"

        try {
            // First attempt: Fetch directly from TikTok web profile
            val request = Request.Builder()
                .url(profileUrl)
                .header("User-Agent", USER_AGENT)
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
                .header("Accept-Language", "en-US,en;q=0.9,ar;q=0.8")
                .header("Cache-Control", "no-cache")
                .header("Pragma", "no-cache")
                .build()

            client.newCall(request).execute().use { response ->
                val code = response.code
                if (code == 404) {
                    return@withContext Result.failure(NoSuchElementException("لم يتم العثور على الحساب (@$username). يرجى التحقق من صحة الاسم."))
                }

                val html = response.body?.string() ?: ""

                // 1. Try parsing JSON from __UNIVERSAL_DATA_FOR_REHYDRATION__
                val accountFromJson = parseUniversalData(html, username, profileUrl)
                if (accountFromJson != null) {
                    return@withContext Result.success(accountFromJson)
                }

                // 2. Try parsing SIGI_STATE
                val accountFromSigi = parseSigiState(html, username, profileUrl)
                if (accountFromSigi != null) {
                    return@withContext Result.success(accountFromSigi)
                }

                // 3. Try parsing Meta Tags and OpenGraph
                val accountFromMeta = parseMetaTags(html, username, profileUrl)
                if (accountFromMeta != null) {
                    return@withContext Result.success(accountFromMeta)
                }
            }

            // Fallback: Query official TikTok oEmbed API for profile verification and thumbnail
            val oembedAccount = fetchFromOEmbed(username, profileUrl)
            if (oembedAccount != null) {
                return@withContext Result.success(oembedAccount)
            }

            // If completely blocked or unavailable without fabricating data:
            Result.failure(IOException("تعذر جلب معلومات الحساب الكاملة حالياً. قد يكون الحساب خاصاً أو واجهت المنصة حماية ضد الفحص المؤقت."))
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching TikTok profile for $username", e)
            Result.failure(e)
        }
    }

    private fun parseUniversalData(html: String, username: String, profileUrl: String): TikTokAccount? {
        try {
            val scriptTag = "<script id=\"__UNIVERSAL_DATA_FOR_REHYDRATION__\" type=\"application/json\">"
            val startIndex = html.indexOf(scriptTag)
            if (startIndex == -1) return null

            val jsonStart = startIndex + scriptTag.length
            val endIndex = html.indexOf("</script>", jsonStart)
            if (endIndex == -1) return null

            val jsonString = html.substring(jsonStart, endIndex)
            val root = JSONObject(jsonString)
            val defaultScope = root.optJSONObject("__DEFAULT_SCOPE__") ?: return null
            val userDetail = defaultScope.optJSONObject("webapp.user-detail") ?: return null
            val userInfo = userDetail.optJSONObject("userInfo") ?: return null

            val userObj = userInfo.optJSONObject("user") ?: return null
            val statsObj = userInfo.optJSONObject("stats") ?: JSONObject()

            val uniqueId = userObj.optString("uniqueId", username)
            val nickname = userObj.optString("nickname", uniqueId)
            val userId = userObj.optString("id", "")
            val secUid = userObj.optString("secUid", "")
            val avatarUrl = userObj.optString("avatarLarger")
                .ifEmpty { userObj.optString("avatarMedium") }
                .ifEmpty { userObj.optString("avatarThumb") }
            val bio = userObj.optString("signature", "")
            val isVerified = userObj.optBoolean("verified", false)
            val isPrivate = userObj.optBoolean("privateAccount", false) || userObj.optBoolean("secret", false)

            val followerCount = statsObj.optLong("followerCount", 0L)
            val followingCount = statsObj.optLong("followingCount", 0L)
            val heartCount = statsObj.optLong("heartCount", 0L).let {
                if (it == 0L) statsObj.optLong("heart", 0L) else it
            }
            val videoCount = statsObj.optLong("videoCount", 0L)
            val diggCount = statsObj.optLong("diggCount", 0L)

            return TikTokAccount(
                username = uniqueId,
                nickname = nickname,
                userId = userId.ifBlank { null },
                secUid = secUid.ifBlank { null },
                avatarUrl = avatarUrl,
                bio = bio,
                followerCount = followerCount,
                followingCount = followingCount,
                likesCount = heartCount,
                videoCount = videoCount,
                diggCount = diggCount,
                isVerified = isVerified,
                isPrivate = isPrivate,
                profileUrl = profileUrl
            )
        } catch (e: Exception) {
            Log.w(TAG, "Error parsing __UNIVERSAL_DATA_FOR_REHYDRATION__", e)
            return null
        }
    }

    private fun parseSigiState(html: String, username: String, profileUrl: String): TikTokAccount? {
        try {
            val sigiMarker = "<script id=\"SIGI_STATE\" type=\"application/json\">"
            val startIndex = html.indexOf(sigiMarker)
            if (startIndex == -1) return null

            val jsonStart = startIndex + sigiMarker.length
            val endIndex = html.indexOf("</script>", jsonStart)
            if (endIndex == -1) return null

            val jsonString = html.substring(jsonStart, endIndex)
            val root = JSONObject(jsonString)
            val userModule = root.optJSONObject("UserModule") ?: return null
            val users = userModule.optJSONObject("users") ?: return null
            val stats = userModule.optJSONObject("stats") ?: return null

            val userObj = users.optJSONObject(username) ?: users.optJSONObject(users.keys().asSequence().firstOrNull() ?: "")
            if (userObj == null) return null

            val userKey = userObj.optString("uniqueId", username)
            val statsObj = stats.optJSONObject(userKey) ?: JSONObject()

            val nickname = userObj.optString("nickname", userKey)
            val userId = userObj.optString("id", "")
            val secUid = userObj.optString("secUid", "")
            val avatarUrl = userObj.optString("avatarLarger")
                .ifEmpty { userObj.optString("avatarMedium") }
            val bio = userObj.optString("signature", "")
            val isVerified = userObj.optBoolean("verified", false)
            val isPrivate = userObj.optBoolean("privateAccount", false)

            val followerCount = statsObj.optLong("followerCount", 0L)
            val followingCount = statsObj.optLong("followingCount", 0L)
            val heartCount = statsObj.optLong("heartCount", 0L)
            val videoCount = statsObj.optLong("videoCount", 0L)

            return TikTokAccount(
                username = userKey,
                nickname = nickname,
                userId = userId.ifBlank { null },
                secUid = secUid.ifBlank { null },
                avatarUrl = avatarUrl,
                bio = bio,
                followerCount = followerCount,
                followingCount = followingCount,
                likesCount = heartCount,
                videoCount = videoCount,
                isVerified = isVerified,
                isPrivate = isPrivate,
                profileUrl = profileUrl
            )
        } catch (e: Exception) {
            Log.w(TAG, "Error parsing SIGI_STATE", e)
            return null
        }
    }

    private fun parseMetaTags(html: String, username: String, profileUrl: String): TikTokAccount? {
        try {
            // Check if page indicates account does not exist
            if (html.contains("Couldn't find this account") || html.contains("Account not found")) {
                return null
            }

            // Extract og:title (e.g. "Khaby Lame (@khaby.lame) | TikTok")
            val titlePattern = Pattern.compile("<meta\\s+property=[\"']og:title[\"']\\s+content=[\"'](.*?)[\"']", Pattern.CASE_INSENSITIVE)
            val titleMatcher = titlePattern.matcher(html)
            var nickname = username
            if (titleMatcher.find()) {
                val fullTitle = titleMatcher.group(1) ?: ""
                val atPos = fullTitle.indexOf("(@")
                if (atPos != -1) {
                    nickname = fullTitle.substring(0, atPos).trim()
                }
            }

            // Extract og:image
            val imgPattern = Pattern.compile("<meta\\s+property=[\"']og:image[\"']\\s+content=[\"'](.*?)[\"']", Pattern.CASE_INSENSITIVE)
            val imgMatcher = imgPattern.matcher(html)
            val avatarUrl = if (imgMatcher.find()) imgMatcher.group(1) ?: "" else ""

            // Extract og:description (e.g. "161.4M Followers, 78 Following, 2.5B Likes...")
            val descPattern = Pattern.compile("<meta\\s+property=[\"']og:description[\"']\\s+content=[\"'](.*?)[\"']", Pattern.CASE_INSENSITIVE)
            val descMatcher = descPattern.matcher(html)
            var followerCount = 0L
            var followingCount = 0L
            var likesCount = 0L

            if (descMatcher.find()) {
                val desc = descMatcher.group(1) ?: ""
                val statsPattern = Pattern.compile("([0-9.,]+[KMBkmb]?)\\s*Followers,\\s*([0-9.,]+[KMBkmb]?)\\s*Following,\\s*([0-9.,]+[KMBkmb]?)\\s*Likes", Pattern.CASE_INSENSITIVE)
                val sm = statsPattern.matcher(desc)
                if (sm.find()) {
                    followerCount = parseCompactNumber(sm.group(1) ?: "0")
                    followingCount = parseCompactNumber(sm.group(2) ?: "0")
                    likesCount = parseCompactNumber(sm.group(3) ?: "0")
                }
            }

            if (avatarUrl.isNotEmpty() || followerCount > 0L) {
                return TikTokAccount(
                    username = username,
                    nickname = nickname.ifEmpty { username },
                    userId = null,
                    secUid = null,
                    avatarUrl = avatarUrl,
                    bio = "",
                    followerCount = followerCount,
                    followingCount = followingCount,
                    likesCount = likesCount,
                    videoCount = 0L,
                    isVerified = false,
                    isPrivate = false,
                    profileUrl = profileUrl
                )
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error in parseMetaTags", e)
        }
        return null
    }

    private fun fetchFromOEmbed(username: String, profileUrl: String): TikTokAccount? {
        try {
            val oembedUrl = "https://www.tiktok.com/oembed?url=https://www.tiktok.com/@$username"
            val request = Request.Builder()
                .url(oembedUrl)
                .header("User-Agent", USER_AGENT)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return null
                val body = response.body?.string() ?: return null
                val json = JSONObject(body)
                val authorName = json.optString("author_name", username)
                val authorUniqueId = json.optString("author_unique_id", username)
                val thumbnailUrl = json.optString("thumbnail_url", "")
                val title = json.optString("title", "")

                return TikTokAccount(
                    username = authorUniqueId,
                    nickname = authorName,
                    userId = null,
                    secUid = null,
                    avatarUrl = thumbnailUrl,
                    bio = title,
                    followerCount = 0L,
                    followingCount = 0L,
                    likesCount = 0L,
                    videoCount = 0L,
                    isVerified = false,
                    isPrivate = false,
                    profileUrl = profileUrl
                )
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error fetching from oEmbed", e)
            return null
        }
    }

    private fun parseCompactNumber(str: String): Long {
        try {
            val clean = str.replace(",", "").trim()
            val lastChar = clean.lastOrNull()?.uppercaseChar()
            if (lastChar == 'K') {
                val num = clean.dropLast(1).toDouble()
                return (num * 1_000).toLong()
            } else if (lastChar == 'M') {
                val num = clean.dropLast(1).toDouble()
                return (num * 1_000_000).toLong()
            } else if (lastChar == 'B') {
                val num = clean.dropLast(1).toDouble()
                return (num * 1_000_000_000).toLong()
            }
            return clean.toDoubleOrNull()?.toLong() ?: 0L
        } catch (e: Exception) {
            return 0L
        }
    }
}

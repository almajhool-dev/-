package com.example

import com.example.data.model.IssueSeverity
import com.example.data.model.TikTokAccount
import com.example.data.network.TikTokRepository
import com.example.domain.analyzer.TikTokSecurityAnalyzer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    private val repository = TikTokRepository()

    @Test
    fun testUsernameExtraction() {
        assertEquals("khaby.lame", repository.extractUsername("@khaby.lame"))
        assertEquals("khaby.lame", repository.extractUsername("khaby.lame"))
        assertEquals("khaby.lame", repository.extractUsername("https://www.tiktok.com/@khaby.lame"))
        assertEquals("khaby.lame", repository.extractUsername("https://www.tiktok.com/@khaby.lame?is_from_webapp=1&sender_device=pc"))
        assertEquals("tiktok", repository.extractUsername("   @tiktok   "))
        assertEquals("", repository.extractUsername(""))
    }

    @Test
    fun testAnalyzerAlgorithmForHealthyAccount() {
        val account = TikTokAccount(
            username = "cyber_expert",
            nickname = "خبير الأمن السيبراني",
            userId = "98234123",
            secUid = "sec_test_123",
            avatarUrl = "https://example.com/avatar.jpg",
            bio = "حساب متخصص في التوعية الأمنية الرقمية ومراجعة الثغرات وتأمين الحسابات الشخصية",
            followerCount = 15000L,
            followingCount = 120L,
            likesCount = 85000L,
            videoCount = 45L,
            isVerified = true,
            isPrivate = false,
            profileUrl = "https://www.tiktok.com/@cyber_expert"
        )

        val audit = TikTokSecurityAnalyzer.analyze(account)

        // Account is verified, has complete bio, strong engagement and video count
        assertTrue("Security score should be >= 90 for healthy verified account", audit.safetyScore >= 90)
        assertEquals("ممتاز (صحة وموثوقية عالية)", audit.ratingLevel)
        assertTrue("Should have strengths detected", audit.strengths.isNotEmpty())
        assertTrue("Should have verified strength", audit.strengths.any { it.id == "verified_badge" })
    }

    @Test
    fun testAnalyzerAlgorithmDetectsIssues() {
        val suspiciousAccount = TikTokAccount(
            username = "user88392104921",
            nickname = "",
            userId = null,
            secUid = null,
            avatarUrl = "", // Empty avatar
            bio = "",       // Empty bio
            followerCount = 8000L,
            followingCount = 9500L, // Aggressive follow-for-follow
            likesCount = 12L,       // Almost zero likes despite 8000 followers
            videoCount = 0L,        // Zero videos
            isVerified = false,
            isPrivate = true,
            profileUrl = "https://www.tiktok.com/@user88392104921"
        )

        val audit = TikTokSecurityAnalyzer.analyze(suspiciousAccount)

        assertTrue("Security score should be low for suspicious/ghost account", audit.safetyScore <= 45)
        assertTrue("Issues list must not be empty", audit.issues.isNotEmpty())

        // Verify that critical/warning issues exist
        val issueIds = audit.issues.map { it.id }
        assertTrue(issueIds.contains("missing_avatar"))
        assertTrue(issueIds.contains("empty_bio"))
        assertTrue(issueIds.contains("zero_videos"))
        assertTrue(issueIds.contains("ghost_followers"))

        // Verify that each issue has Problem, Cause, and Solution
        audit.issues.forEach { issue ->
            assertTrue("Issue title must not be empty", issue.title.isNotBlank())
            assertTrue("Issue reason must not be empty", issue.reason.isNotBlank())
            assertTrue("Issue solution must not be empty", issue.solution.isNotBlank())
        }
    }
}

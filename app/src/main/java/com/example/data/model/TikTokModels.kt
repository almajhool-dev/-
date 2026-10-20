package com.example.data.model

data class TikTokAccount(
    val username: String,
    val nickname: String,
    val userId: String?,
    val secUid: String?,
    val avatarUrl: String,
    val bio: String,
    val followerCount: Long,
    val followingCount: Long,
    val likesCount: Long,
    val videoCount: Long,
    val diggCount: Long = 0L,
    val isVerified: Boolean,
    val isPrivate: Boolean,
    val profileUrl: String,
    val isFound: Boolean = true
)

enum class IssueSeverity {
    CRITICAL,
    WARNING,
    INFO
}

data class AuditIssue(
    val id: String,
    val severity: IssueSeverity,
    val title: String,
    val reason: String,
    val solution: String
)

data class AuditStrength(
    val id: String,
    val title: String,
    val description: String
)

data class ScoreFactor(
    val title: String,
    val earnedPoints: Int,
    val maxPoints: Int,
    val description: String
)

data class AccountAuditResult(
    val account: TikTokAccount,
    val safetyScore: Int,
    val ratingLevel: String,
    val issues: List<AuditIssue>,
    val strengths: List<AuditStrength>,
    val factors: List<ScoreFactor>,
    val engagementRatePercent: Double,
    val avgLikesPerVideo: Long,
    val profileCompletenessPercent: Int,
    val auditedAtTimestamp: Long = System.currentTimeMillis()
)

package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audit_history")
data class AuditHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val nickname: String,
    val avatarUrl: String,
    val followerCount: Long,
    val followingCount: Long,
    val likesCount: Long,
    val videoCount: Long,
    val isVerified: Boolean,
    val isPrivate: Boolean,
    val safetyScore: Int,
    val ratingLevel: String,
    val timestamp: Long = System.currentTimeMillis()
)

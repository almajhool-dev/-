package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AuditHistoryDao {
    @Query("SELECT * FROM audit_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<AuditHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudit(entity: AuditHistoryEntity): Long

    @Query("DELETE FROM audit_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM audit_history")
    suspend fun clearAll()
}

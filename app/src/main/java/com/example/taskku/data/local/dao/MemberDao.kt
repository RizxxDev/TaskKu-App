package com.example.taskku.data.local.dao

import androidx.room.*
import com.example.taskku.data.local.entity.MemberEntity

@Dao
interface MemberDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: MemberEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembers(members: List<MemberEntity>): List<Long>

    @Update
    suspend fun updateMember(member: MemberEntity)

    @Delete
    suspend fun deleteMember(member: MemberEntity)

    @Query("DELETE FROM members WHERE taskId = :taskId")
    suspend fun deleteMembersByTaskId(taskId: Long)

    @Query("SELECT * FROM members WHERE taskId = :taskId")
    suspend fun getMembersByTaskId(taskId: Long): List<MemberEntity>
}

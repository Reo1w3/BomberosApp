package com.example.bomberosapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bomberosapp.data.model.Personal

@Dao
interface PersonalDao {
    @Query("SELECT * FROM personal WHERE codigo_personal = :codigo AND numero_identificacion = :pin LIMIT 1")
    suspend fun login(codigo: Int, pin: String): Personal?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersonal(personal: Personal)

    @Query("SELECT COUNT(*) FROM personal")
    suspend fun getCount(): Int
}

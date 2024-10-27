package com.example.myapplication

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UsuarioDAO {
    @Insert
    suspend fun insertUsuario(usuario: Usuario)

    @Query("SELECT * FROM usuarios")
    suspend fun getAllUsuarios(): List<Usuario>

    @Query("DELETE FROM usuarios WHERE id = :usuarioId")
    suspend fun deleteUsuario(usuarioId: Int)
}

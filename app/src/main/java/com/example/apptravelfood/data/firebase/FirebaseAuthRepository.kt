package com.example.apptravelfood.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository {

    private val auth = FirebaseAuth.getInstance()

    suspend fun loginWithGoogle(
        idToken: String
    ): String {
        val credential = GoogleAuthProvider.getCredential(
            idToken,
            null
        )

        val result = auth.signInWithCredential(
            credential
        ).await()

        return result.user?.uid
            ?: throw Exception("Đăng nhập Google thất bại")
    }

    suspend fun sendResetPassword(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    fun logout() {
        auth.signOut()
    }

    fun currentUid(): String? {
        return auth.currentUser?.uid
    }
}
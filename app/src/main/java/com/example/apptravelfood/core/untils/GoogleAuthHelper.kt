package com.example.apptravelfood.core.untils

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.example.apptravelfood.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

data class GoogleUserInfo(
    val idToken: String,
    val email: String,
    val fullName: String?,
    val avatarUrl: String?
)

object GoogleAuthHelper {

    suspend fun signIn(
        context: Context
    ): GoogleUserInfo {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(
                context.getString(R.string.google_web_client_id)
            )
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val credentialManager = CredentialManager.create(context)

        val result = credentialManager.getCredential(
            context = context,
            request = request
        )

        val googleCredential =
            GoogleIdTokenCredential.createFrom(
                result.credential.data
            )

        return GoogleUserInfo(
            idToken = googleCredential.idToken,
            email = googleCredential.id,
            fullName = googleCredential.displayName,
            avatarUrl = googleCredential.profilePictureUri?.toString()
        )
    }
}
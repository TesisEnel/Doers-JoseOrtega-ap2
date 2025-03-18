package edu.ucne.doers.presentation.sign_in

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import android.content.SharedPreferences
import android.util.Log
import edu.ucne.doers.R
import javax.inject.Inject

class GoogleAuthUiClient @Inject constructor(
    private val context: Context,
    private val oneTapClient: SignInClient,
    private val sharedPreferences: SharedPreferences
) {
    private val auth = FirebaseAuth.getInstance()

    suspend fun signIn(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        return result?.pendingIntent?.intentSender
    }

    suspend fun signInWithIntent(intent: Intent): SignInResult {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
        return try {
            val user = auth.signInWithCredential(googleCredential).await().user
            val userData = user?.let {
                UserData(
                    userId = it.uid,
                    userName = it.displayName,
                    email = it.email,
                    profilePictureUrl = it.photoUrl?.toString()
                )
            }
            userData?.let {
                sharedPreferences.edit()
                    .putString("userId", it.userId)
                    .putString("userName", it.userName)
                    .putString("email", it.email)
                    .putString("profilePictureUrl", it.profilePictureUrl)
                    .apply()
            }
            SignInResult(
                data = userData,
                errorMessage = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            SignInResult(
                data = null,
                errorMessage = e.message
            )
        }
    }

    suspend fun signOut() {
        try {
            oneTapClient.signOut().await()
            auth.signOut()
            sharedPreferences.edit()
                .remove("userId")
                .remove("userName")
                .remove("email")
                .remove("profilePictureUrl")
                .apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getSignedInUser(): UserData? {
        val userId = sharedPreferences.getString("userId", null)
        val userName = sharedPreferences.getString("userName", null)
        val email = sharedPreferences.getString("email", null)
        val profilePictureUrl = sharedPreferences.getString("profilePictureUrl", null)

        Log.d("GoogleAuthUiClient", "getSignedInUser - userId: $userId, userName: $userName, email: $email, profilePictureUrl: $profilePictureUrl")

        return if (userId != null) {
            UserData(
                userId = userId,
                userName = userName,
                email = email,
                profilePictureUrl = profilePictureUrl
            )
        } else {
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                val userData = UserData(
                    userId = firebaseUser.uid,
                    userName = firebaseUser.displayName,
                    email = firebaseUser.email,
                    profilePictureUrl = firebaseUser.photoUrl?.toString()
                )
                sharedPreferences.edit()
                    .putString("userId", userData.userId)
                    .putString("userName", userData.userName)
                    .putString("email", userData.email)
                    .putString("profilePictureUrl", userData.profilePictureUrl)
                    .apply()
                Log.d("GoogleAuthUiClient", "Usuario recuperado de Firebase: $userData")
                userData
            } else {
                Log.w("GoogleAuthUiClient", "No hay usuario en SharedPreferences ni en Firebase")
                null
            }
        }
    }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.default_web_client_id))
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}
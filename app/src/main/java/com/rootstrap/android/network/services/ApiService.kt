package com.rootstrap.android.network.services

import com.rootstrap.android.network.models.FacebookSignIn
import com.rootstrap.android.network.models.TargetPointSerializer
import com.rootstrap.android.network.models.TopicsSerializer
import com.rootstrap.android.network.models.UserSerializer
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("users")
    fun signUp(@Body user: UserSerializer): Call<UserSerializer>

    @POST("users/sign_in")
    fun signIn(@Body user: UserSerializer): Call<UserSerializer>

    @POST("users/facebook")
    fun signInWithFacebook(@Body facebookSignIn: FacebookSignIn): Call<UserSerializer>

    @DELETE("users/sign_out")
    fun signOut(): Call<Void>

    @POST("targets")
    fun createTarget(@Body target: TargetPointSerializer): Call<TargetPointSerializer>

    @GET("topics")
    fun getTopics(): Call<TopicsSerializer>
}

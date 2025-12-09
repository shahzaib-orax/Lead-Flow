package com.example.leadflow.network

import android.util.Base64

object TwilioConfig {

    const val ACCOUNT_SID = ""
    const val AUTH_TOKEN = ""
    const val SENDER_NUMBER = ""

    const val BASE_URL = "https://api.twilio.com/2010-04-01/Accounts/$ACCOUNT_SID/Messages.json"


    fun getAuthHelper(): String{
        val credentials = "$ACCOUNT_SID:$AUTH_TOKEN"
        val encodedCredentials = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
        return "Basic $encodedCredentials"
    }
}
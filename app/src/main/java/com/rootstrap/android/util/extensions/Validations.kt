package com.rootstrap.android.util.extensions

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

fun String.validate(pattern: String): Boolean {
    return pattern.toRegex().matches(this)
}

fun String.isEmail(): Boolean {
    return validate(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )
}

fun Boolean?.isTrue(): Boolean {
    return this == true
}

fun Context.isPermissionGranted(permission: String): Boolean {
    return ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
}

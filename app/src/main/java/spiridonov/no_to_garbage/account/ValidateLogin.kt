package spiridonov.no_to_garbage.account

import android.util.Patterns

fun checkEmailIsValid(email: String): Boolean {
    if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) return true
    return false
}

fun checkPasswordIsValid(password: String): Boolean {
    if (password.length > 5) return true
    return false
}
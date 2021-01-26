package com.rootstrap.android.util

import com.google.android.material.textfield.TextInputLayout
import com.rootstrap.android.network.models.ErrorModel

class ErrorUtil {

    companion object {

        private const val FULL_MESSAGES = "full_messages"
        private const val TOPIC = "topic"

        fun handleCustomError(error: ErrorModel): String {
            var message = ""
            if (error.errors != null) {
                if (error.errors is List<*> && !error.errors.isEmpty()) {
                    if (error.errors.first() is String) {
                        message = error.errors.first() as String
                    }
                } else if (error.errors is Map<*, *> && error.errors.keys.first() is String &&
                    error.errors.values.first() is List<*>
                ) {
                    val errors = error.errors as Map<String, List<String>>
                    message = when {
                        errors.containsKey(FULL_MESSAGES) -> errors.getValue(FULL_MESSAGES).first()
                        errors.containsKey(TOPIC) -> TOPIC + ": " + errors.getValue(TOPIC).first()
                        else -> if (errors.values.isNotEmpty() && errors.values.first().isNotEmpty())
                            errors.values.first().first()
                        else ""
                    }
                }
            } else if (error.error != null && !error.error.isEmpty()) {
                message = error.error
            }

            return message
        }

        fun displayError(inputLayout: TextInputLayout, message: String) {
            inputLayout.isErrorEnabled = true
            inputLayout.error = message
        }
    }

    class ErrorsEvent(val error: String)
}

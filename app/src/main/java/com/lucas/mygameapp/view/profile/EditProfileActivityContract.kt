package com.lucas.mygameapp.view.profile

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class EditProfileActivityContract : ActivityResultContract<Int, Boolean?>() {
    override fun createIntent(context: Context, input: Int): Intent {
        return Intent(context, EditProfileActivity::class.java).putExtra("userId", input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean? {
        return intent?.getBooleanExtra("edited", false)
    }
}
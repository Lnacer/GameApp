package com.lucas.mygameapp.view.gamedetail

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.lucas.mygameapp.model.UserGame
import java.security.KeyPair

class GameDetailActivityContract : ActivityResultContract<Array<Pair<String, Int>>, UserGame?>() {
    override fun createIntent(context: Context, inputs: Array<Pair<String, Int>>): Intent {

        val intent = Intent(context, GameDetailActivity::class.java)

        for (input in inputs) {
            intent.putExtra(input.first, input.second)
        }

        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): UserGame? {
        return intent?.getParcelableExtra("userGame", UserGame::class.java)
    }
}
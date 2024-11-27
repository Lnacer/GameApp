package com.lucas.mygameapp.utils

import android.text.Editable
import android.text.TextWatcher

class MaskWatcher(private val mask : String) : TextWatcher {
    private var isRunning = false;
    private var isDeleting = false;

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        isDeleting = count > after
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(editable: Editable?) {
        if (isRunning || isDeleting || editable == null) {
            return
        }
        isRunning = true

        var editableLength : Int = editable.length

        if (editableLength < mask.length) {
            if (mask[editableLength] != '#') {
                editable.append(mask[editableLength])
            } else if (mask[editableLength - 1] != '#') {
                editable.insert(editableLength - 1, mask, editableLength-1, editableLength)
            }
        }
        else if (editableLength > mask.length) {
            editable.delete(mask.length, editableLength)
        }

        isRunning = false
    }
}
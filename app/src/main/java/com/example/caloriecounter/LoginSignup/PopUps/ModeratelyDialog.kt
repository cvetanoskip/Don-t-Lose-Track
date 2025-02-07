package com.example.caloriecounter.LoginSignup.PopUps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.caloriecounter.R

class ModeratelyDialog : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_activity3,container,false)
        rootView.findViewById<Button>(R.id.close_moderately).setOnClickListener{
            dismiss()
        }


        return rootView
    }
}
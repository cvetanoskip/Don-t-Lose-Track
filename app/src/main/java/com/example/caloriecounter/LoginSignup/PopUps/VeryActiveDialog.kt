package com.example.caloriecounter.LoginSignup.PopUps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.caloriecounter.R

class VeryActiveDialog : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_activity4,container,false)
        rootView.findViewById<Button>(R.id.close_very_active).setOnClickListener{
            dismiss()
        }


        return rootView
    }
}
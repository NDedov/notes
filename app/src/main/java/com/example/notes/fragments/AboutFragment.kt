package com.example.notes.fragments

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.notes.activity.OnBackPressedListener
import com.example.notes.R

class AboutFragment : Fragment(), OnBackPressedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) requireActivity().supportFragmentManager.popBackStack()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layout = view.findViewById<LinearLayout>(R.id.aboutLayout)
        layout.setOnClickListener {    //закрываем на нажатие
            if (resources.configuration.orientation
                    == Configuration.ORIENTATION_LANDSCAPE) //восстанавливаем фрагмент для заметок
            // для ландшафтной ориентации
                requireActivity().findViewById<View>(R.id.fragmentNoteContainer).visibility = View.VISIBLE
            requireActivity().supportFragmentManager.popBackStack() //закрываем
        }
        hideKeyBoard()
    }

    override fun onBackPressed() {
        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun hideKeyBoard() {
        val view1 = requireActivity().currentFocus
        if (view1 != null) { //скрытие клавиатуры при выходе
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view1.windowToken, 0)
        }
    }
}
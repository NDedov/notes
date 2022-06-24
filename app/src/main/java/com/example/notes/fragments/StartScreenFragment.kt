package com.example.notes.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import android.widget.TextView
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.fragment.app.Fragment
import com.example.notes.add.Constants
import com.example.notes.activity.OnBackPressedListener
import com.example.notes.R

class StartScreenFragment : Fragment(), OnBackPressedListener, Constants {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_start_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layout = view.findViewById<ConstraintLayout>(R.id.startScreenLayout)
        layout.setOnClickListener {    //по клику закрываем текущий фрагмент
            if (resources.configuration.orientation
                    == Configuration.ORIENTATION_LANDSCAPE) //восстанавливаем фрагмент для заметок
            // для ландшафтной ориентации
                requireActivity().findViewById<View>(R.id.fragmentNoteContainer).visibility = View.VISIBLE
            requireActivity().supportFragmentManager.popBackStack() //закрываем
        }
        showAnimatedText(view)
    }

    private fun showAnimatedText(view: View) {
        val textView = view.findViewById<TextView>(R.id.textContinueScreenView)
        val anim: Animation = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 700 //You can manage the blinking time with this parameter
        anim.startOffset = 20
        anim.repeatMode = Animation.REVERSE
        anim.repeatCount = Animation.INFINITE
        textView.startAnimation(anim)
    }

    override fun onBackPressed() {
        requireActivity().supportFragmentManager.popBackStack() //закрываем
    }
}
package com.example.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.fragment.app.Fragment

class FilterFragment : Fragment(), Constants {
    private var currentFilterPosition = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) requireActivity().supportFragmentManager.popBackStack() //удаляем лишние
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val arguments = arguments
        if (arguments != null) {
            currentFilterPosition = arguments.getInt(Constants.FILTER_INDEX)
            initViews(view)
        }
    }

    private fun initViews(view: View) {
        val layout = view.findViewById<LinearLayout>(R.id.categoryLayout)
        for (i in 0 until Note.categories.size + 1) { //заполняем радиобаттонами категорий
            val rb = RadioButton(context)
            if (i == Note.categories.size) rb.text = "Показать все" //для последнего радиобаттона
            else rb.text = Note.categories[i]
            if (i == currentFilterPosition) rb.isChecked = true
            layout.addView(rb) //добавляем радиобаттоны

            //прописываем Листенеры для вью
            rb.setOnClickListener {
                val result = Bundle()
                result.putInt(Constants.FILTER_INDEX, i)
                parentFragmentManager.setFragmentResult(Constants.RESULT_OK_FILTER_EXIT_INDEX, result)
                parentFragmentManager.popBackStack()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(currentFilterPosition: Int): FilterFragment {
            val filterFragment = FilterFragment()
            val args = Bundle()
            args.putInt(Constants.FILTER_INDEX, currentFilterPosition)
            filterFragment.arguments = args
            return filterFragment
        }
    }
}
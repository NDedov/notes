package com.example.notes

import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import android.widget.Spinner
import android.os.Bundle
import android.text.TextWatcher
import android.text.Editable
import android.widget.AdapterView
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import android.content.Intent
import android.app.DatePickerDialog
import android.content.Context
import android.content.res.Configuration
import android.view.*
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

class NoteTextFragment : Fragment(), Constants, DeleteDialogListener, OnBackPressedListener {
    private var note //заметка
            : Note? = null
    private lateinit var dateTimeView: TextView // поле для даты/времени
    private lateinit var favoriteButton: AppCompatButton // кнопка Избранное
    private lateinit var deleteButton: AppCompatButton // кнопка Удалить
    private lateinit var saveButton: AppCompatButton // кнопка сохранить
    private lateinit var shareButton: AppCompatButton // кнопка сохранить
    private lateinit var titleView: TextView // заголовок
    private lateinit var categorySpinner: Spinner // список категорий
    private lateinit var textView: TextView // текст заметки
    private var helperTextView //вспомогательный объект для обработки undo/redo
            : TextViewUndoRedo? = null
    private var flagForSpinner = false // флаг для вызова обработчика только по нажатию,
    // что бы не срабатывал при инициализации
    private var noteIsChanged = false //флаг фиксирующий, что заметка изменилась
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) requireActivity().supportFragmentManager.popBackStack()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note_text, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fList = requireActivity().supportFragmentManager.fragments
        for (item in fList) { //находим ненужные фрагменты и удаляем, возникают при повороте
            if (item != this && item is NoteTextFragment) {
                requireActivity().supportFragmentManager.beginTransaction().remove(item).commit()
            }
        }
        val arguments = arguments
        if (arguments != null) { // получаем из бандл текущую заметку
            note = arguments.getParcelable(Constants.LIST_TO_NOTE_INDEX)
            noteIsChanged = arguments.getBoolean(Constants.NOTE_IS_CHANGED_TAG)
            if (note != null) initViews(view)
        }
        if (!isLandscape) setActionBar(view)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (noteIsChanged) updateNoteList()
        super.onSaveInstanceState(outState)
    }

    private fun updateNoteList() { //метод для обновления основного списка заметок (NoteListFragment)
        val result = Bundle()
        if (note != null) {
            note!!.setTitle(titleView.text.toString())
            note!!.setText(textView.text.toString())
            note!!.setCategoryID(categorySpinner.selectedItemPosition)
            result.putParcelable(Constants.NOTE_CHANGE_INDEX, note)
            result.putBoolean(Constants.NOTE_IS_CHANGED_TAG, noteIsChanged)
            parentFragmentManager.setFragmentResult(Constants.NOTE_CHANGED, result)
        }
    }

    private fun initViews(view: View) {
        titleView = view.findViewById(R.id.titleTextView)
        dateTimeView = view.findViewById(R.id.dateTimeView)
        categorySpinner = view.findViewById(R.id.categorySpinner)
        textView = view.findViewById(R.id.textView)
        favoriteButton = view.findViewById(R.id.favoriteButton)
        deleteButton = view.findViewById(R.id.deleteButton)
        saveButton = view.findViewById(R.id.saveButton)
        shareButton = view.findViewById(R.id.shareButton)
        printValues()
        initButtons()
        initListeners()
        initEditListeners()
    }

    private fun initEditListeners() { // обработчик изменений едитов и спиннера
        textView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                noteIsChangedActions()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        titleView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                noteIsChangedActions()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                if (flagForSpinner) noteIsChangedActions()
                flagForSpinner = true
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    fun noteIsChangedActions() {
        noteIsChanged = true
        setIconMenu()
        setSaveButton()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun displayToast(text: String?) { //кастомизированный тоаст
        val toast = Toast.makeText(context,
                text,
                Toast.LENGTH_SHORT)
        val toastView = toast.view
        toastView!!.background = ResourcesCompat.getDrawable(resources,
                R.drawable.rounded_corner_toast, null)
        toast.show()
    }

    private fun initListeners() { //обработчики кнопок
        initDateTimeViewListener()
        initDeleteButtonListener()
        initShareButtonListener()
        initSaveButtonListener()
        initFavoriteButtonListener()
    }

    private fun initDeleteButtonListener() {
        deleteButton.setOnClickListener {    //обработка кнопки удалить
            hideKeyBoard()
            val deleteNoteDialogFragment = DeleteNoteDialogFragment()
            deleteNoteDialogFragment.setListener(this@NoteTextFragment)
            deleteNoteDialogFragment.show(requireActivity().supportFragmentManager,
                    Constants.DELETE_NOTE_DIALOG_TAG)
        }
    }

    private fun initShareButtonListener() {
        shareButton.setOnClickListener {    //обработка кнопки поделиться
            val shareBody = textView.text.toString()
            val shareSub = titleView.text.toString()
            if (shareBody == "" && shareSub == "") displayToast(getString(R.string.message_nothing_to_share)) else {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, shareSub)
                intent.putExtra(Intent.EXTRA_TEXT, """
     $shareSub
     $shareBody
     """.trimIndent())
                startActivity(Intent.createChooser(intent, getString(R.string.share_message)))
            }
        }
    }

    private fun initSaveButtonListener() {
        saveButton.setOnClickListener {    //обработка кнопки сохранить
            hideKeyBoard()
            if (noteIsChanged) {
                updateNoteList()
                if (isLandscape) {
                    displayToast(getString(R.string.save_ok))
                    helperTextView = TextViewUndoRedo(textView)
                    noteIsChanged = false
                    setSaveButton()
                } else {
                    displayToast(getString(R.string.save_ok))
                    noteIsChanged = false
                    helperTextView = TextViewUndoRedo(textView)
                    setIconMenu()
                    setSaveButton()
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }
    }

    private fun initFavoriteButtonListener() {
        favoriteButton.setOnClickListener {    //обработка кнопки Избранное
            note!!.setFavourite(!note!!.isFavourite())
            showFavoriteButton()
            val result = Bundle()
            result.putParcelable(Constants.NOTE_CHANGE_INDEX, note)
            parentFragmentManager.setFragmentResult(Constants.NOTE_CHANGED, result)
        }
    }

    private fun initDateTimeViewListener() { //обработка едита с датой временем
        dateTimeView.setOnClickListener {
            hideKeyBoard()
            showDateTimeDialog(note)
        }
    }

    private fun showDateTimeDialog(note: Note?) { //диалог по смене "даты". Меняет только визуально
        DatePickerDialog(requireContext(), 0, { datePicker: DatePicker, _: Int, _: Int, _: Int ->
            val tmpCalendar: Calendar = GregorianCalendar()
            tmpCalendar[Calendar.YEAR] = datePicker.year
            tmpCalendar[Calendar.MONTH] = datePicker.month
            tmpCalendar[Calendar.DAY_OF_MONTH] = datePicker.dayOfMonth
            dateTimeView.text = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                    .format(tmpCalendar.time)
        }, note!!.dateTimeModify!![Calendar.YEAR],
                note.dateTimeModify!![Calendar.MONTH],
                note.dateTimeModify!![Calendar.DAY_OF_MONTH]).show()
    }

    private fun hideKeyBoard() {
        val view1 = requireActivity().currentFocus
        if (view1 != null) { //скрытие клавиатуры при выходе
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view1.windowToken, 0)
        }
    }

    private fun setActionBar(view: View) { //обработчик кнопки выхода в toolBar
        val toolbar = view.findViewById<Toolbar>(R.id.toolbarNoteText)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener {    //при нажатии на выход в тулбаре
            hideKeyBoard()
            if (noteIsChanged) updateNoteList()
            requireActivity().supportFragmentManager.popBackStack()
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.text_menu, menu)
        setIconMenu()
    }

    private fun setIconMenu() { //метод раскрашивания иконок меню
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbarNoteText)
        if (toolbar != null) {
            val menu = toolbar.menu
            val itemSave = menu.findItem(R.id.action_text_save)
            val itemUndo = menu.findItem(R.id.action_text_undo)
            val itemRedo = menu.findItem(R.id.action_text_redo)
            if (itemSave != null) {
                if (noteIsChanged) {
                    menu.findItem(R.id.action_text_save).setIcon(R.drawable.ic_save)
                    menu.findItem(R.id.action_text_save).isEnabled = true
                } else {
                    menu.findItem(R.id.action_text_save).setIcon(R.drawable.ic_save_grey)
                    menu.findItem(R.id.action_text_save).isEnabled = false
                }
            }
            if (itemRedo != null) {
                if (helperTextView!!.canRedo) {
                    menu.findItem(R.id.action_text_redo).setIcon(R.drawable.ic_redo_new)
                    menu.findItem(R.id.action_text_redo).isEnabled = true
                } else {
                    menu.findItem(R.id.action_text_redo).setIcon(R.drawable.ic_redo_new_grey)
                    menu.findItem(R.id.action_text_redo).isEnabled = false
                }
            }
            if (itemUndo != null) {
                if (helperTextView!!.canUndo) {
                    menu.findItem(R.id.action_text_undo).setIcon(R.drawable.ic_undo_new)
                    menu.findItem(R.id.action_text_undo).isEnabled = true
                } else {
                    menu.findItem(R.id.action_text_undo).setIcon(R.drawable.ic_undo_new_grey)
                    menu.findItem(R.id.action_text_undo).isEnabled = false
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { //обработчик меню
        when (item.itemId) {
            R.id.action_text_save -> {
                if (noteIsChanged) { //отрабатываем сохранение если заметка изменилась
                    helperTextView = TextViewUndoRedo(textView)
                    updateNoteList()
                    displayToast(getString(R.string.save_ok))
                    noteIsChanged = false
                    setIconMenu()
                    setSaveButton()
                }
                return true
            }
            R.id.action_text_redo -> {
                helperTextView!!.redo()
                setIconMenu()
                return true
            }
            R.id.action_text_undo -> {
                helperTextView!!.undo()
                setIconMenu()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun printValues() {
        titleView.text = note!!.getTitle()
        textView.text = note!!.getText()
        dateTimeView.text = SimpleDateFormat("dd MMMM yyyy  HH:mm", Locale.getDefault())
                .format(note!!.dateTimeModify!!.time)
        val categoryAdapter = ArrayAdapter(requireContext(),
                android.R.layout.simple_spinner_item, Note.categories)
        categorySpinner.adapter = categoryAdapter
        categorySpinner.setSelection(note!!.getCategoryID())
        helperTextView = TextViewUndoRedo(textView)
    }

    private fun initButtons() {
        showFavoriteButton()
        deleteButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_trash, 0, 0)
        shareButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_share, 0, 0)
        setSaveButton()
    }

    private fun setSaveButton() {
        if (noteIsChanged) {
            saveButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_save3, 0, 0)
            saveButton.isEnabled = true
        } else {
            saveButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_save3_grey, 0, 0)
            saveButton.isEnabled = false
        }
    }

    private fun showFavoriteButton() {
        if (note!!.isFavourite()) favoriteButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_favorite_yes, 0, 0) else favoriteButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_favorite_no, 0, 0)
    }

    private val isLandscape: Boolean
        get() = (resources.configuration.orientation
                == Configuration.ORIENTATION_LANDSCAPE)

    override fun onDelete() { //обработчик кнопки удалить
        val result = Bundle()
        result.putParcelable(Constants.NOTE_CHANGE_INDEX, note)
        parentFragmentManager.setFragmentResult(Constants.NOTE_DELETE, result)
        if (isLandscape) requireActivity().supportFragmentManager.beginTransaction().remove(this).commit() else requireActivity().supportFragmentManager.popBackStack()
        displayToast(getString(R.string.toast_note_delete))
    }

    override fun onNo() {}
    override fun onBackPressed() {
        if (noteIsChanged) updateNoteList()
        requireActivity().supportFragmentManager.popBackStack()
    }

    companion object {
        @JvmStatic
        fun newInstance(note: Note?): NoteTextFragment {
            val noteTextFragment = NoteTextFragment()
            val args = Bundle()
            args.putParcelable(Constants.LIST_TO_NOTE_INDEX, note)
            noteTextFragment.arguments = args
            return noteTextFragment
        }
    }
}
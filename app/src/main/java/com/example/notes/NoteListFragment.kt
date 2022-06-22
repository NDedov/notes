package com.example.notes


import android.annotation.SuppressLint
import com.example.notes.FilterFragment.Companion.newInstance
import com.example.notes.NoteTextFragment.Companion.newInstance
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import android.os.Bundle
import android.text.TextWatcher
import android.text.Editable
import androidx.recyclerview.widget.LinearLayoutManager
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

import java.util.*

class NoteListFragment : Fragment(), Constants {
    private var notes: Notes? = null
    private var settings: Settings? = null
    private var filter: Filter? = null
    private lateinit var filterButton: Button
    private lateinit var filterFavoriteButton: Button
    private lateinit var filterClearButton: Button
    private lateinit var addButton: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageLayout: FrameLayout
    private lateinit var textSearchView: EditText

    /**
     * Реализация интерфейса сохранения и восстановления списка заметок в SharedPreferences
     */
    private val workSharedPreferences: IWorkSharedPreferences = object : IWorkSharedPreferences {
       // private var sharedPreferences: SharedPreferences? = null
        override fun saveNotes(notes: Notes?) {
            val sharedPreferences = requireActivity().getSharedPreferences(Constants.NOTES_SHARED_P,
                    Context.MODE_PRIVATE)
            val jsonNotes = GsonBuilder().create().toJson(notes)
            sharedPreferences.edit().putString(Constants.NOTES_SHARED_P_KEY_NOTES, jsonNotes).apply()
        }

        override fun restoreNotes(): Notes? {
            val sharedPreferences = requireActivity().getSharedPreferences(Constants.NOTES_SHARED_P,
                    Context.MODE_PRIVATE)
            val savedNotes = sharedPreferences.getString(Constants.NOTES_SHARED_P_KEY_NOTES, null)
            return if (savedNotes != null) GsonBuilder().create().fromJson(savedNotes, Notes::class.java) else null
        }

        override fun saveSettings(settings: Settings?) {}
        override fun restoreSettings(): Settings? {
            return null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //В момент создания нового фрагмента мы проверяем, создается ли этот фрагмент впервые, и
        //если да, то просто удаляем его из бэкстека.
        val fragmentManager = requireActivity().supportFragmentManager
        val fragments = fragmentManager.fragments
        for (fragment in fragments) {
            if (fragment !is NoteListFragment) fragmentManager.beginTransaction().remove(fragment!!).commit()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_note_list, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_list_settings) showSettingsFragment()
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.let { getParcelables(it) }
        val arguments = arguments //получаем настройки при открытии фрагмента
        if (arguments != null) settings = arguments.getParcelable(Constants.SETTINGS_TAG)
        if (notes == null) { //заполнение тестовыми заметками при первом запуске
            notes = workSharedPreferences.restoreNotes()
            if (notes == null) {
                notes = Notes()
                notes!!.testFillNotes()
            }
            filter = Filter(Note.categories.size, false, null)
            if (notes!!.size > 0) {
                notes!!.setCurrentNote(notes!![0])
            }
        }
        initViews(view)
        initButtons(view)
        initListNotes(view)
        initSearch(view)
        initFragmentResultListeners(view)
        setHasOptionsMenu(true)
        if (isLandscape) showLandNotes(notes!!.getCurrentNote(), false)
        (requireActivity() as IDrawerFromFragment).initDrawer()
    }

    private fun showSettingsFragment() {
        parentFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, SettingsFragment.newInstance(settings))
                .addToBackStack("")
                .commit()
    }

    private fun initViews(view: View) {
        textSearchView = view.findViewById(R.id.textSearchView)
        filterClearButton = view.findViewById(R.id.filterClearButton)
        filterFavoriteButton = view.findViewById(R.id.filterFavoriteButton)
        addButton = view.findViewById(R.id.addFab)
        filterButton = view.findViewById(R.id.filterButton)
        recyclerView = view.findViewById(R.id.recyclerViewNoteList)
        messageLayout = view.findViewById(R.id.messageLayout)
    }

    private fun getParcelables(savedInstanceState: Bundle) {
        notes = savedInstanceState.getParcelable(Constants.NOTES_LIST)
        filter = savedInstanceState.getParcelable(Constants.FILTER_INDEX)
        settings = savedInstanceState.getParcelable(Constants.SETTINGS_TAG)
    }

    private fun initSearch(view: View) {
        textSearchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                val text = textSearchView.text.toString()
                filter!!.setSearchString(text)
                initListNotes(view)
                showClearFilterIcon()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    private fun initFragmentResultListeners(view: View) {
        //прописываем Листенер, отлавливаем изменения в заметке из NoteTextFragment, обновляем список (превью)
        parentFragmentManager.setFragmentResultListener(Constants.NOTE_CHANGED, this
        ) { key: String?, bundle: Bundle ->
            notes!!.setCurrentNote(bundle.getParcelable(Constants.NOTE_CHANGE_INDEX))
            initListNotes(view)
        }

        //листенер на флаг удаления заметки
        parentFragmentManager.setFragmentResultListener(Constants.NOTE_DELETE, this
        ) { key: String?, bundle: Bundle ->
            notes!!.delete(bundle.getParcelable(Constants.NOTE_CHANGE_INDEX)!!)
            initListNotes(view)
            if (isLandscape) showNotes(notes!!.getCurrentNote(), true)
        }
        //листенер, обрабатывающий изменение в фильтре категорий
        childFragmentManager.setFragmentResultListener(Constants.RESULT_OK_FILTER_EXIT_INDEX, this
        ) { requestKey: String?, result: Bundle ->
            filter!!.currentFilterCategory = result.getInt(Constants.FILTER_INDEX, Note.categories.size)
            initListNotes(view)
            showClearFilterIcon()
        }
    }

    private fun hideKeyBoard() { //метод скрытия клавиатуры
        val view1 = requireActivity().currentFocus
        if (view1 != null) { //скрытие клавиатуры при выходе
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view1.windowToken, 0)
        }
    }

    private fun initButtons(view: View) {
        initFilterButton()
        initAddButton()
        initFavoriteButton(view)
        initFilterClearButton()
    }

    private fun initFilterClearButton() {
        showClearFilterIcon()
        filterClearButton.setOnClickListener {
            filter = Filter(Note.categories.size, false, null)
            textSearchView.setText("")
            showFavoriteIcon()
            showClearFilterIcon()
            hideKeyBoard()
        }
    }

    private fun initFavoriteButton(view: View) {
        filterFavoriteButton.setOnClickListener {
            filter!!.isFavoriteShow = !filter!!.isFavoriteShow
            showFavoriteIcon()
            initListNotes(view)
            showClearFilterIcon()
        }
        showFavoriteIcon()
    }

    private fun initAddButton() {
        addButton.setOnClickListener {
            notes!!.add(Note("", "", GregorianCalendar(), 0, false))
            showNotes(notes!!.getCurrentNote(), true)
        }
    }

    private fun initFilterButton() {
        filterButton.setOnClickListener {
            val fragmentList = childFragmentManager.fragments
            var isFilterShow = false
            for (fragment in fragmentList)  //проверяем есть ли уже открытый фрагмент с фильтром
                if (fragment is FilterFragment) {
                    isFilterShow = true
                    break
                }
            if (isFilterShow)
                childFragmentManager.popBackStack()
            else
                showFilterChildFragment()
        }
    }

    private fun showFilterChildFragment() {
        childFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_in_right)
                .replace(R.id.filterChildLayout, newInstance(filter!!.currentFilterCategory))
                .addToBackStack("")
                .commit()
    }

    private fun showClearFilterIcon() {
        if (filter!!.isFilterActive) {
            filterClearButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_cancel, 0, 0)
            filterClearButton.isEnabled = true
        } else {
            filterClearButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_cancel_grey, 0, 0)
            filterClearButton.isEnabled = false
        }
    }

    private fun showFavoriteIcon() {
        if (filter!!.isFavoriteShow) filterFavoriteButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_favorite_yes, 0, 0) else filterFavoriteButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_favorite_no, 0, 0)
    }

    private val isLandscape: Boolean
        get() = (resources.configuration.orientation
                == Configuration.ORIENTATION_LANDSCAPE)//заполняем список по фильтру

    /**
     * Метод фильтрации списка заметок, для показа
     * @return список для показа
     */
    val listToShow: ArrayList<Note>
        get() {
            val list = ArrayList<Note>()
            for (note in notes!!.notes) { //заполняем список по фильтру
                if (filter!!.isShow(note)) list.add(note)
            }
            return list
        }

    private fun initListNotes(view: View) {
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = NoteListAdapter()
        adapter.setList(listToShow)
        recyclerView.adapter = adapter
        (view.findViewById<View>(R.id.messageLayout) as FrameLayout).removeAllViews()
        if (listToShow.size == 0) showEmptyList()
        initAdapterListener(adapter)
    }

    private fun initAdapterListener(adapter: NoteListAdapter) {
        adapter.setListener(object : NotesListClickListener {
            //реализация интерфейса для обработки адаптера recycleView
            override fun onClick(note: Note?) {
                if (isLandscape) {
                    if (note != notes!!.getCurrentNote()) { //отрабатываем только при смене
                        notes!!.setCurrentNote(note)
                        showNotes(note, true)
                    }
                } else {
                    notes!!.setCurrentNote(note)
                    showNotes(note, true)
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onFavoriteClick(note: Note?, item: View?, position: Int) { //при нажатии на звездочку добавляем или удаляем из избранного
                note!!.setFavourite(!note.isFavourite())
                if (note.isFavourite()) item!!.findViewById<ImageView>(R.id.favoriteImageItemListNote)
                        .setImageResource(R.drawable.ic_favorite_yes) else item!!.findViewById<ImageView>(R.id.favoriteImageItemListNote)
                        .setImageResource(R.drawable.ic_favorite_no)
                if (filter!!.isFavoriteShow) {
                    adapter.setList(listToShow)
                    adapter.notifyDataSetChanged()
                }
                if (isLandscape) {
                    notes!!.setCurrentNote(note)
                    showNotes(note, false)
                }
            }

            override fun onLongClick(note: Note?, view: View?, position: Int) { //popup меню на заметке
                val activity: Activity = requireActivity()
                val popupMenu = PopupMenu(activity, view)
                activity.menuInflater.inflate(R.menu.list_popup, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
                    when (menuItem.itemId) {
                        R.id.action_popup_edit -> {
                            notes!!.setCurrentNote(note)
                            showNotes(note, true)
                            return@setOnMenuItemClickListener true
                        }
                        R.id.action_popup_to_favorite -> {
                            note!!.setFavourite(true)
                            adapter.notifyItemChanged(position)
                            return@setOnMenuItemClickListener true
                        }
                        R.id.action_popup_delete -> {
                            notes!!.delete(note!!)
                            adapter.notifyItemRemoved(position)
                            adapter.setList(listToShow)
                            if (listToShow.size == 0) showEmptyList()
                            if (isLandscape) showLandNotes(notes!!.getCurrentNote(), false)
                            return@setOnMenuItemClickListener true
                        }
                    }
                    true
                }
                popupMenu.show()
            }
        })
    }

    /**
     * Создает текствью в messageLayout и выводит сообщение о том, что список пуст
     */
    private fun showEmptyList() {
        val tv = TextView(context)
        tv.setText(R.string.message_empty_list)
        val textViewLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        textViewLayoutParams.setMargins(16, 24, 16, 12)
        tv.layoutParams = textViewLayoutParams
        tv.textSize = 18f
        messageLayout.addView(tv)
    }

    private fun showNotes(note: Note?, anim: Boolean) {
        if (isLandscape) showLandNotes(note, anim) else showPortNotes(note)
    }

    // метод вызывающий показ фрагмента заметки для ланшафтной ориентации
    private fun showLandNotes(note: Note?, anim: Boolean) {
        var note = note
        if (note == null && notes!!.size > 0) { //показываем первый по умолчанию
            notes!!.setCurrentNote(notes!![0])
            note = notes!!.getCurrentNote()
        }
        if (note != null) {
            val noteTextFragment = newInstance(note)
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            // добавляем фрагмент
            if (anim) fragmentTransaction
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_in_right)
                    .replace(R.id.fragmentNoteContainer, noteTextFragment)
                    .commit() else fragmentTransaction
                    .replace(R.id.fragmentNoteContainer, noteTextFragment)
                    .commit()
        }
    }

    // метод вызывающий показ фрагмента заметки для портретной ориентации
    private fun showPortNotes(note: Note?) {
        val noteTextFragment = newInstance(note)
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        // добавляем фрагмент
        fragmentTransaction
                .replace(R.id.fragmentContainer, noteTextFragment)
                .addToBackStack("")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(Constants.NOTES_LIST, notes)
        outState.putParcelable(Constants.FILTER_INDEX, filter)
        outState.putParcelable(Constants.SETTINGS_TAG, settings)
        super.onSaveInstanceState(outState)
    }

    override fun onStop() {
        workSharedPreferences.saveNotes(notes)
        super.onStop()
    }

    companion object {
        fun newInstance(settings: Settings?): NoteListFragment {
            val noteListFragment = NoteListFragment()
            val args = Bundle()
            args.putParcelable(Constants.SETTINGS_TAG, settings)
            noteListFragment.arguments = args
            return noteListFragment
        }
    }
}
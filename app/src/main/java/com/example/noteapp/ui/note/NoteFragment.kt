package com.example.noteapp.ui.note

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.noteapp.R
import com.example.noteapp.data.Note
import com.example.noteapp.ui.adapter.NoteAdapter
import com.example.noteapp.ui.adapter.NoteLoadStateAdapter
import com.example.noteapp.ui.viewmodel.NoteViewModel
import com.example.noteapp.ultis.Status
import kotlinx.android.synthetic.main.fragment_note_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteFragment : Fragment(R.layout.fragment_note_list) {

    private val noteViewModel: NoteViewModel by lazy {
        ViewModelProvider(
            this,
            NoteViewModel.NoteViewModelFactory(requireActivity().application)
        )[NoteViewModel::class.java]
    }
    private val adapter: NoteAdapter by lazy {
        NoteAdapter()
    }
    private var job: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        rv_note.setHasFixedSize(true)
        rv_note.layoutManager = LinearLayoutManager(requireContext())
        rv_note.adapter = adapter.withLoadStateHeaderAndFooter(
            //truyền funtion retry của lib(differ.retry()) adapter vào button retry cùa mình
            header = NoteLoadStateAdapter { adapter::retry },
            footer = NoteLoadStateAdapter { adapter::retry }
        )

        swipe_layout.setOnRefreshListener {
            refreshData()
        }
        refreshData()
    }


    private fun refreshData() {
        swipe_layout.isRefreshing = false
        //lay data tu sever ve
        job?.cancel()
        job = lifecycleScope.launch {
            //collect cái mới nhất -> collectlatest
            noteViewModel.getNotesFromApi().collectLatest {
                adapter.submitData(it)
                swipe_layout.isRefreshing = false
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val controller = findNavController()

        btn_open_add_activity.setOnClickListener {
            controller.navigate(R.id.action_noteFragment_to_addNoteFragment)
        }
    }
}
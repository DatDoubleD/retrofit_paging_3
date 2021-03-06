package com.example.noteapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.noteapp.data.Note
import com.example.noteapp.data.NoteRepository
import com.example.noteapp.ultis.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : ViewModel() {

    private val noteRepository: NoteRepository = NoteRepository(application)
    //room
    fun insertNote(note: Note) = viewModelScope.launch {
        noteRepository.insertNote(note)
    }

    fun updateNote(note: Note) = viewModelScope.launch {
        noteRepository.updateNote(note)
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        noteRepository.deleteNote(note)
    }

    fun getAllNote(): LiveData<List<Note>> = noteRepository.getAllNote()

    fun deleteAllNote() = viewModelScope.launch {
        noteRepository.deleteAllNoteFromDatabase()
    }

    fun insertAllNoteToDatabase(notes:List<Note>) = viewModelScope.launch {
        noteRepository.insertAllNoteToDatabase(notes)
    }
    //retrofit

    //get note use livedata
   /* fun getNotesFromApi() = liveData(Dispatchers.IO) {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(noteRepository.getNotesFromApi()))
        } catch (ex: Exception) {
            emit(Resource.error(null, ex.message ?: "Error"))
        }
    }
*/
    //get note use paging
    fun getNotesFromApi(): Flow<PagingData<Note>> {
        return noteRepository.getNotesFromApi().cachedIn(viewModelScope)
    }


    fun addNoteToServer(note: Note) = liveData(Dispatchers.IO) {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(noteRepository.addNoteToServer(note)))
        } catch (ex: Exception) {
            emit(Resource.error(null, ex.message ?: "Error"))
        }
    }

    class NoteViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return NoteViewModel(application) as T
            }

            throw IllegalArgumentException("Unable construct viewModel")
        }

    }


}
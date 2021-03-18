package com.example.noteapp.data

import androidx.paging.PagingSource
import com.example.noteapp.api.NoteApi
import com.example.noteapp.data.NoteRepository.Companion.NETWORK_PAGE_SIZE

import retrofit2.HttpException
import java.io.IOException

//trang dau tiên dc lấy ra mặc định = 1

//mỗi trang lấy 20 items
const val NOTE_STARTING_PAGE_INDEX = 1

//lấy từng item đưa lên adapter
class NotePagingSource(private val noteApi: NoteApi) : PagingSource<Int, Note>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Note> {
        val position = params.key ?: NOTE_STARTING_PAGE_INDEX
        return try {
            // vị trí page + size -> có danh sach note
            val notes = noteApi.getAllNote(position, params.loadSize)
            //trả về danh sach note
            LoadResult.Page(
                data = notes,//data
                //vị trí = 1 -> trang trước đó == null,
                prevKey = if (position == NOTE_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (notes.isEmpty()) null else position + (params.loadSize / NETWORK_PAGE_SIZE)
            )
        }catch (ex:IOException){
            LoadResult.Error(ex)
        }catch (ex: HttpException){
            LoadResult.Error(ex)
        }
    }
}
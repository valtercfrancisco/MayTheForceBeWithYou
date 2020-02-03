package com.valter.maytheforcebewith_valterfrancisco.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PageKeyedDataSource
import androidx.paging.toLiveData
import com.valter.maytheforcebewith_valterfrancisco.data.db.entity.PeopleData
import com.valter.maytheforcebewith_valterfrancisco.data.db.entity.Person
import com.valter.maytheforcebewith_valterfrancisco.data.repository.SwapiRepository
import com.valter.maytheforcebewith_valterfrancisco.ui.components.ForceDataSource
import com.valter.maytheforcebewith_valterfrancisco.ui.components.ForceDataSourceFactory
import com.valter.maytheforcebewith_valterfrancisco.utils.Outcome
import com.valter.maytheforcebewith_valterfrancisco.utils.launchSafely
import kotlinx.coroutines.Dispatchers

class MainViewModel(
        private val repository: SwapiRepository
) : ViewModel() {

    private val forceDataSourceFactory = ForceDataSourceFactory {
        ForceDataSource(this::loadInitialPage, this::loadPage)
    }

    private var _peopleData = MutableLiveData<Outcome<PeopleData>>()
    internal var peopleData: LiveData<Outcome<PeopleData>> = _peopleData
    internal var people = forceDataSourceFactory.toLiveData(1)

    /**
     * Proxy to ForceDataSource
     */
    private fun loadInitialPage(callback: PageKeyedDataSource.LoadInitialCallback<String, Person>) {
        launchLoadRequest(
                isFirstPage = true
        ) {
            callback.onResult(it.people, null, it.nextPage)
        }
    }

    /**
     * Proxy to ForceDataSource
     */
    private fun loadPage(
            callback: PageKeyedDataSource.LoadCallback<String, Person>,
            pageToLoad: String
    ) {
        launchLoadRequest(pageToLoad) {
            callback.onResult(it.people, it.nextPage)
        }
    }

    private fun launchLoadRequest(
            pageToLoad: String = "",
            isFirstPage: Boolean = false,
            dataSourceCallbackCaller: (PeopleData) -> Unit
    ) {
        viewModelScope.launchSafely(
                _peopleData,
                loading = isFirstPage,
                context = Dispatchers.IO
        ) {
            repository.getPeople(pageToLoad, isFirstPage).also {
                dataSourceCallbackCaller(it)
            }
        }
    }
}

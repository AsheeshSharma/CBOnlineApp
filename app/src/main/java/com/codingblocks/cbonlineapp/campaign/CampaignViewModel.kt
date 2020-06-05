package com.codingblocks.cbonlineapp.campaign

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.cbonlineapp.util.savedStateValue
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.models.Spins
import kotlinx.coroutines.Dispatchers

const val SPINS_LEFT = "spinsLeft"

class CampaignViewModel(
    handle: SavedStateHandle,
    private val repo: CampaignRepository
) : BaseCBViewModel() {

    private var spinsLeft by savedStateValue<Int>(handle, SPINS_LEFT)
    var spinsLiveData = MutableLiveData<Int>(spinsLeft)

    var myWinnings: LiveData<PagedList<Spins>>
    private var leaderboard: LiveData<PagedList<Spins>>
    lateinit var dataSource: DataSource<String, Spins>

    init {
        val config = PagedList.Config.Builder()
            .setPageSize(9)
            .setEnablePlaceholders(true)
            .build()
        myWinnings = initializedPagedListBuilder(config).build()
        leaderboard = initializedPagedListBuilder(config).build()

    }

    fun fetchSpins() {
        runIO {
            when (val response = repo.getSpinStats()) {
                is ResultWrapper.GenericError -> {
                    setError(response.error)
                    spinsLiveData.postValue(spinsLeft)
                }
                is ResultWrapper.Success -> with(response.value) {
                    if (response.value.isSuccessful)
                        response.value.body()?.let {
                            val availableSpins = it.get("availableSpins").asInt
                            val usedSpins = it.get("totalUsedSpins").asInt
                            spinsLeft = availableSpins
                            spinsLiveData.postValue(spinsLeft)
                        }
                    else {
                        spinsLiveData.postValue(spinsLeft)
                    }
                }
            }
        }
    }

    fun drawSpin() = liveData(Dispatchers.IO) {
        when (val response = repo.drawSpin()) {
            is ResultWrapper.GenericError -> {
                setError(response.error)
            }
            is ResultWrapper.Success -> with(response.value) {
                if (response.value.isSuccessful)
                    response.value.body()?.let {
                        emit(it)
                    }
            }
        }
    }


    fun getLeaderboard(): LiveData<PagedList<Spins>> = leaderboard

    private fun initializedPagedListBuilder(config: PagedList.Config):
        LivePagedListBuilder<String, Spins> {

        val dataSourceFactory = object : DataSource.Factory<String, Spins>() {
            override fun create(): DataSource<String, Spins> {
                dataSource = CampaignDataSource(viewModelScope)
                return dataSource
            }
        }
        return LivePagedListBuilder(dataSourceFactory, config)

    }

}

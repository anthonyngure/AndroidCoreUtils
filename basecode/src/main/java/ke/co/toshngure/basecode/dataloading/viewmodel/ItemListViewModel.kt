package ke.co.toshngure.basecode.dataloading.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import ke.co.toshngure.basecode.dataloading.data.ItemBoundaryCallback
import ke.co.toshngure.basecode.dataloading.data.ItemRepository
import ke.co.toshngure.basecode.dataloading.sync.SyncStatesDatabase

class ItemListViewModel<Model, ModelListing> : ViewModel() {

    private val repository = MutableLiveData<ItemRepository<Model, ModelListing>>()

    private val syncClass = MutableLiveData<Class<Model>>()

    val syncState = Transformations.switchMap(syncClass) {
        SyncStatesDatabase.getInstance().syncStates().findByModelLive(it.simpleName)
    }

    private val repoResult = Transformations.map(repository) { it.list() }
    val items = Transformations.switchMap(repoResult) { it }


    fun init(repository: ItemRepository<Model, ModelListing>) {
        this.repository.value = repository
        this.syncClass.value = repository.mItemRepositoryConfig.syncClass
    }
}
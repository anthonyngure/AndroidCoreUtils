package ke.co.toshngure.basecode.dataloading.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import ke.co.toshngure.basecode.dataloading.data.ItemRepository

class ItemListViewModel<Model, ModelListing> constructor() : ViewModel() {

    private val repository = MutableLiveData<ItemRepository<Model, ModelListing>>()


    private val repoResult = Transformations.map(repository) {
        it.list()
    }

    val items = Transformations.switchMap(repoResult) { it.pagedList }!!
    val networkState = Transformations.switchMap(repoResult) { it.networkState }!!
    val refreshState = Transformations.switchMap(repoResult) { it.refreshState }!!

    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }

    fun load(repository: ItemRepository<Model, ModelListing>): Boolean {
        /*if (this.args.value == args) {
            return false
        }*/
        this.repository.value = repository
        return true
    }

    fun retry() {
        val listing = repoResult?.value
        listing?.retry?.invoke()
    }


    fun clear() {
        repoResult.value?.clear?.invoke()
    }
}
package ke.co.toshngure.basecode.dataloading.viewmodel

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import ke.co.toshngure.basecode.dataloading.data.ItemRepository
import ke.co.toshngure.basecode.dataloading.sync.SyncStatesDatabase

class ItemListViewModel<Model, LoadedModel>(private val repository: ItemRepository<Model, LoadedModel>) : ViewModel() {

    private val args = MutableLiveData<Bundle?>()

    private val repoResult = Transformations.map(args) {
        repository.list(args.value)
    }

    val items = Transformations.switchMap(repoResult) {
        it
    }

    val syncState = Transformations.switchMap(repoResult) {
        val model = repository.getSyncClass().simpleName
        SyncStatesDatabase.getInstance().syncStates().findLive(model, repository.getTab())
    }


    fun loadWithArgs(args: Bundle? = null){
        this.args.value = args
    }
}
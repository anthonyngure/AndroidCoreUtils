package ke.co.toshngure.basecode.logging

import androidx.paging.DataSource
import ke.co.toshngure.basecode.dataloading.data.ItemDao
import ke.co.toshngure.basecode.dataloading.data.ItemRepository
import ke.co.toshngure.basecode.dataloading.data.ItemRepositoryConfig

class LogItemRepository : ItemRepository<LogItem, LogItem>() {
    override fun getItemId(item: LogItem): Long {
        return item.id
    }

    override fun getItemRepositoryConfig(): ItemRepositoryConfig<LogItem, LogItem> {
        return ItemRepositoryConfig(syncClass = LogItem::class.java)
    }

    override fun getItemDataSource(): DataSource.Factory<Int, LogItem> {
        val title = arguments?.getString(LogItemsFragment.EXTRA_SUB_TAG)
        return title?.let {
            LogItemsDatabase.getInstance().logItems().getAllBySubTagPaged(it)
        } ?: LogItemsDatabase.getInstance().logItems().getAllPaged()
    }

    override fun getItemDao(): ItemDao<LogItem> {
        return LogItemsDatabase.getInstance().logItems()
    }
}
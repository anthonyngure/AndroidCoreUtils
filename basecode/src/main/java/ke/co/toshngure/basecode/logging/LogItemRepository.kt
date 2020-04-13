package ke.co.toshngure.basecode.logging

import android.os.Bundle
import androidx.paging.DataSource
import ke.co.toshngure.basecode.paging.data.ItemDao
import ke.co.toshngure.basecode.paging.data.ItemRepository
import ke.co.toshngure.basecode.paging.data.ItemRepositoryConfig

class LogItemRepository : ItemRepository<LogItem, LogItem>() {
    override fun getItemId(item: LogItem): Long {
        return item.id
    }

    override fun getItemRepositoryConfig(): ItemRepositoryConfig<LogItem> {
        return ItemRepositoryConfig(syncClass = LogItem::class.java)
    }

    override fun getItemDataSource(args: Bundle?): DataSource.Factory<Int, LogItem> {
        val title = args?.getString(LogItemsFragment.EXTRA_SUB_TAG)
        return title?.let {
            LogItemsDatabase.getInstance().logItems().getAllBySubTagPaged(it)
        } ?: LogItemsDatabase.getInstance().logItems().getAllPaged()
    }

    override fun getItemDao(): ItemDao<LogItem> {
        return LogItemsDatabase.getInstance().logItems()
    }

    override fun deleteAll() {
        super.deleteAll()
        LogItemsDatabase.getInstance().logItems().deleteAll()
    }
}
package ke.co.toshngure.basecode.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import ke.co.toshngure.basecode.logging.BeeLog

/**
 * Created by antho on 5/7/2019
 *
 * @author Anthony Ngure
 */
object ContactUtils {

    private const val TAG = "ContactUtils"

    private const val PICK_CONTACT_REQUEST_CODE = 100

    fun pickContact(fragment: Fragment) {
        val uriContact = ContactsContract.Contacts.CONTENT_URI
        val intent = Intent(Intent.ACTION_PICK, uriContact)
        fragment.startActivityForResult(intent, PICK_CONTACT_REQUEST_CODE)
    }

    fun onActivityResult(
        context: Context,
        onContactSelectedListener: OnContactSelectedListener,
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (requestCode != PICK_CONTACT_REQUEST_CODE || resultCode != Activity.RESULT_OK || data == null) return

        val dataUri = data.data ?: return

        BeeLog.i(TAG, "dataUri = $dataUri")

        val cursor = context.contentResolver.query(
            dataUri,
            null,
            null,
            null,
            null
        )

        BeeLog.i(TAG, "cursor.count => ${cursor?.count}")

        if (cursor?.count == 0) return

        cursor?.moveToNext()

        val contactId = cursor?.getString(
            cursor.getColumnIndex(ContactsContract.Contacts._ID)
        )
        val hasPhoneNumber = cursor?.getString(
            cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
        )

        BeeLog.i(TAG, "contactId => $contactId")
        BeeLog.i(TAG, "hasPhoneNumber => $hasPhoneNumber")

        cursor?.close()

        if (!hasPhoneNumber.equals("1", ignoreCase = true)) return

        val numberCursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
            arrayOf(contactId),
            null
        )

        BeeLog.i(TAG, "numberCursor?.count => ${numberCursor?.count}")

        if (numberCursor?.count == 0) return


        val numbers = arrayListOf<String>()

        numberCursor?.moveToFirst()

        do {
            val number = numberCursor?.getString(
                numberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            )
            BaseUtils.normalizePhoneNumber(number)?.let {
                numbers.add(it)
            }

        } while (numberCursor?.moveToNext() == true)

        numberCursor?.close()

        BeeLog.i(TAG, "Numbers size = ${numbers.size}")

        val uniqueNumbers = numbers.distinct()
        BeeLog.i(TAG, "Unique Numbers size = ${uniqueNumbers.size}")

        if (uniqueNumbers.isEmpty()) return

        if (uniqueNumbers.size == 1) {
            onContactSelectedListener.onContactsSelected(listOf(uniqueNumbers[0]))
        } else {
            val selectedNumbers = mutableListOf<String>()
            AlertDialog.Builder(context)
                .setTitle("Select numbers")
                .setMultiChoiceItems(
                    uniqueNumbers.toTypedArray(),
                    uniqueNumbers.map { false }.toBooleanArray()
                ) { _, which, isChecked ->

                    val selected = uniqueNumbers[which]

                    // Remove selected first
                    selectedNumbers.removeIf { it == selected }

                    if (isChecked) {
                        selectedNumbers.add(selected)
                    }
                }
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    onContactSelectedListener.onContactsSelected(selectedNumbers)
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                }
                .show()
        }


    }

    interface OnContactSelectedListener {
        fun onContactsSelected(numbers: List<String>)
    }
}
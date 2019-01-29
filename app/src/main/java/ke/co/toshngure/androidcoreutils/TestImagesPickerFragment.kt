package ke.co.toshngure.androidcoreutils

import ke.co.toshngure.images.ImagesPickerFragment

class TestImagesPickerFragment : ImagesPickerFragment<Any>() {

    override fun maximumImages(): Int {
        return 100
    }

}

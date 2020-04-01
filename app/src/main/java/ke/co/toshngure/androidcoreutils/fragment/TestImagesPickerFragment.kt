package ke.co.toshngure.androidcoreutils.fragment

import ke.co.toshngure.images.fragment.ImagesPickerFragment

class TestImagesPickerFragment : ImagesPickerFragment<Any>() {

    override fun maximumImages(): Int {
        //return 100
        return 10
        //return 1
    }

}

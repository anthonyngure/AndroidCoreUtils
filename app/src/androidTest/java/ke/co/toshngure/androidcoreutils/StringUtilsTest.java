package ke.co.toshngure.androidcoreutils;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import ke.co.toshngure.androidcoreutils.util.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Created by Anthony Ngure on 6/11/2019
 *
 * @author Anthony Ngure
 */
@RunWith(AndroidJUnit4.class)
public class StringUtilsTest {

    @Test
    public void isEmptyString_worksForNull() {
        assertTrue(StringUtils.isEmptyString(null));
    }

    @Test
    public void isEmptyString_worksForBlank() {
        assertTrue(StringUtils.isEmptyString(""));
    }

    @Test
    public void isEmptyString_worksForSingleBlank() {
        assertTrue(StringUtils.isEmptyString(" "));
    }

    @Test
    public void isEmptyString_worksForMultipleBlank() {
        assertTrue(StringUtils.isEmptyString("    "));
    }
}
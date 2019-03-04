/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ParamSonverter;

import com.qnium.webrunner.helpers.ParamsConverter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author admin
 */
public class TestParamsConverter {

    public TestParamsConverter() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void BasicConversionTest() throws Exception {

        Map<String, String[]> data = new HashMap();

        data.put("name", new String [] {"OK"});
        data.put("id", new String [] {"2"});
        data.put("things", new String [] {"OK1", "OK2", "OK3"});
        data.put("list", new String[] {"0", "1", "2"});
        data.put("isThere", new String[] {"true"});
        data.put("singleInstant", new String[] { "2001-02-03T14:05:06.00" + ZonedDateTime.now().getOffset().toString()} );
        data.put("instantList", new String[] { "2021-10-23T15:25:26.00" + ZonedDateTime.now().getOffset().toString(),
            "2015-12-13T16:15:16.00" + ZonedDateTime.now().getOffset().toString()});
        data.put("isThere", new String[] {"true"});

        TestParams params = (TestParams) ParamsConverter.convert(data, TestParams.class);
        assertEquals("String param", params.name, "OK");
        assertEquals("Int param", params.id, 2);
        Assert.assertArrayEquals("String array param", new String [] {"OK1", "OK2", "OK3"}, params.things);
        Assert.assertArrayEquals(new int[] {0,1,2}, params.list);
        Assert.assertTrue(params.isThere);

        ZonedDateTime singleInstant = params.singleInstant.atZone(ZoneId.systemDefault());
        assertEquals("Single instant year", singleInstant.get(ChronoField.YEAR), 2001);
        assertEquals("Single instant month", singleInstant.get(ChronoField.MONTH_OF_YEAR), 2);
        assertEquals("Single instant day", singleInstant.get(ChronoField.DAY_OF_MONTH), 3);
        assertEquals("Single instant hour", singleInstant.get(ChronoField.HOUR_OF_DAY), 14);
        assertEquals("Single instant minutes", singleInstant.get(ChronoField.MINUTE_OF_HOUR), 5);
        assertEquals("Single instant seconds", singleInstant.get(ChronoField.SECOND_OF_MINUTE), 6);

        assertEquals("List instatn size", params.instantList.length, 2);
        ZonedDateTime secondInstant = params.instantList[1].atZone(ZoneId.systemDefault());
        assertEquals("Single instant year", secondInstant.get(ChronoField.YEAR), 2015);
        assertEquals("Single instant month", secondInstant.get(ChronoField.MONTH_OF_YEAR), 12);
        assertEquals("Single instant day", secondInstant.get(ChronoField.DAY_OF_MONTH), 13);
        assertEquals("Single instant hour", secondInstant.get(ChronoField.HOUR_OF_DAY), 16);
        assertEquals("Single instant minutes", secondInstant.get(ChronoField.MINUTE_OF_HOUR), 15);
        assertEquals("Single instant seconds", secondInstant.get(ChronoField.SECOND_OF_MINUTE), 16);
    }
}

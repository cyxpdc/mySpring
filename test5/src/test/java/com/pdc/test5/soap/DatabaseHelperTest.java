package com.pdc.test5.soap;

import org.junit.Test;
import com.pdc.spring.helper.DatabaseHelper;

public class DatabaseHelperTest {

    @Test
    public void test() {
        DatabaseHelper.update("TRUNCATE TABLE test");
        for (int i = 1; i <= 1000; i++) {
            DatabaseHelper.update("INSERT INTO test (value) VALUES (?)", i);
        }
    }
}

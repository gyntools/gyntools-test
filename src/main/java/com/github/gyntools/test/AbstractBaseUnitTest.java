package com.github.gyntools.test;

import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;

/**
 * Created by rafael on 8/16/16.
 */
public abstract class AbstractBaseUnitTest {

    @BeforeMethod(alwaysRun=true)
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

}

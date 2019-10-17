package com.Czynt.kazdoura;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class EmailLoginPresenterTest {

    /*
            given: initial conditions

            when: the action being triggered

            then:  result

     */


    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();



    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

    }


    @Test
    public void shouldCallSignUpFailed() {


    }
}


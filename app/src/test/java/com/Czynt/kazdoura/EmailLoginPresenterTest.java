package com.Czynt.kazdoura;

import com.Czynt.kazdoura.Login.EmailAuth.EmailLoginModel;
import com.Czynt.kazdoura.Login.EmailAuth.EmailLoginPresenter;
import com.Czynt.kazdoura.Login.EmailAuth.EmailLoginView;

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

    @Mock
    EmailLoginView view;

    @Mock
    EmailLoginModel model;


    private EmailLoginPresenter presenter;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        presenter = new EmailLoginPresenter(model, view);
    }


    @Test
    public void shouldCallSignUpFailed() {


    }
}


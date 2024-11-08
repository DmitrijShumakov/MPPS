package com.example.mpps;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.mpps.Activity.RegisterActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RegisterActivityTest {

    private RegisterActivity registerActivity;

    @Before
    public void setUp() {
        registerActivity = new RegisterActivity();
    }

    @Test
    public void testValidEmail() {
        assertTrue(registerActivity.validateInput("test@example.com", "Password1!", "Password1!", "John", "Doe", "123456789"));
    }

    @Test
    public void testInvalidEmail() {
        assertFalse(registerActivity.validateInput("test@example", "Password1!", "Password1!", "John", "Doe", "123456789"));
    }

    @Test
    public void testPasswordMismatch() {
        assertFalse(registerActivity.validateInput("test@example.com", "Password1!", "Password2!", "John", "Doe", "123456789"));
    }

    @Test
    public void testValidPhoneNumber() {
        assertTrue(registerActivity.validateInput("test@example.com", "Password1!", "Password1!", "John", "Doe", "123456789"));
    }

    @Test
    public void testInvalidPhoneNumber() {
        assertFalse(registerActivity.validateInput("test@example.com", "Password1!", "Password1!", "John", "Doe", "12345"));
    }

    @Test
    public void testEmptyFields() {
        assertFalse(registerActivity.validateInput("", "Password1!", "Password1!", "John", "Doe", "123456789"));
    }

    @Test
    public void testPasswordStrength() {
        assertFalse(registerActivity.isValidPassword("weak"));
        assertTrue(registerActivity.isValidPassword("Password1!"));
    }
}

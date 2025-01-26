package com.example.todoapp;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.todoapp.views.LoginActivity;
import com.example.todoapp.views.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.Tasks;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.hamcrest.Matchers;

import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static org.hamcrest.Matchers.allOf;

import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TodoCreationTest {

    private static final String TEST_EMAIL = "ibowasde@gmail.com";
    private static final String TEST_PASSWORD = "test1234";
    private static final String TEST_TODO_TITLE = "Test Todo " + System.currentTimeMillis();
    private static final String TEST_TODO_DESCRIPTION = "Test Description " + System.currentTimeMillis();

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void setup() throws Exception {
        // First sign out to ensure clean state
        FirebaseAuth.getInstance().signOut();
        
        // Sign in with test credentials
        Tasks.await(
            FirebaseAuth.getInstance().signInWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD),
            30, TimeUnit.SECONDS
        );

        // Verify login was successful
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            throw new IllegalStateException("Login failed. Please ensure test user exists.");
        }

        // Launch MainActivity after successful login
        ActivityScenario.launch(MainActivity.class);
    }

    @Test
    public void testAddTodoBottomSheetDisplayed() {
        // Click the add button to open add todo bottom sheet
        Espresso.onView(withId(R.id.addButton))
                .perform(ViewActions.click());

        // Verify that the bottom sheet elements are displayed
        Espresso.onView(withId(R.id.todoTitleInput))
                .check(ViewAssertions.matches(isDisplayed()));
        
        Espresso.onView(withId(R.id.todoDescriptionInput))
                .check(ViewAssertions.matches(isDisplayed()));
        
        Espresso.onView(withId(R.id.prioritySpinner))
                .check(ViewAssertions.matches(isDisplayed()));
        
        Espresso.onView(withId(R.id.datePickerButton))
                .check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void testAddTodoWithEmptyTitle() {
        // Click the add button to open add todo bottom sheet
        Espresso.onView(withId(R.id.addButton))
                .perform(ViewActions.click());

        // Try to save without entering a title
        Espresso.onView(withId(R.id.saveTodoButton))
                .perform(ViewActions.click());

        // Verify that error is shown
        Espresso.onView(withId(R.id.todoTitleInput))
                .check(ViewAssertions.matches(ViewMatchers.hasErrorText("Title is required")));
    }

    @Test
    public void testAddValidTodo() {
        // Click the add button to open add todo bottom sheet
        Espresso.onView(withId(R.id.addButton))
                .perform(ViewActions.click());

        // Enter todo title
        Espresso.onView(withId(R.id.todoTitleInput))
                .perform(ViewActions.typeText(TEST_TODO_TITLE), ViewActions.closeSoftKeyboard());

        // Enter todo description
        Espresso.onView(withId(R.id.todoDescriptionInput))
                .perform(ViewActions.typeText(TEST_TODO_DESCRIPTION), ViewActions.closeSoftKeyboard());

        // Select priority (Medium)
        Espresso.onView(withId(R.id.prioritySpinner))
                .perform(ViewActions.click());
        Espresso.onView(withText("Medium"))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(ViewActions.click());

        // Click date picker button
        Espresso.onView(withId(R.id.datePickerButton))
                .perform(ViewActions.click());

        // Select a date (today)
        Espresso.onView(withId(android.R.id.button1))
                .perform(ViewActions.click());

        // Click save button
        Espresso.onView(withId(R.id.saveTodoButton))
                .perform(ViewActions.click());

        // Wait for the bottom sheet to close and list to update
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Find the CardView containing both the title and description
        Espresso.onView(allOf(
            withId(R.id.todoTitle),
            withText(TEST_TODO_TITLE),
            isDisplayed()
        )).check(matches(isDisplayed()));

        Espresso.onView(allOf(
            withId(R.id.todoDescription),
            withText(TEST_TODO_DESCRIPTION),
            isDisplayed()
        )).check(matches(isDisplayed()));
    }
} 
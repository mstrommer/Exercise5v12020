package at.ac.fhcampuswien;

import at.ac.fhcampuswien.person.Person;
import at.ac.fhcampuswien.person.PersonManager;
import org.junit.jupiter.api.*;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(2)
class AppTest {

    private PrintStream originalOut;
    private InputStream originalIn;
    private ByteArrayOutputStream bos;
    private PrintStream ps;

    @BeforeAll
    public static void init() {
        System.out.println("Testing Exercise 5");
    }

    @AfterAll
    public static void finish() {
        System.out.println("Finished Testing Exercise 5");
    }

    @BeforeEach
    public void setupStreams() throws IOException {
        originalOut = System.out;
        originalIn = System.in;

        bos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bos));

        PipedOutputStream pos = new PipedOutputStream();
        PipedInputStream pis = new PipedInputStream(pos);
        System.setIn(pis);
        ps = new PrintStream(pos, true);
    }

    @AfterEach
    public void tearDownStreams() {
        // undo the binding in System
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    public void classPerson1() {
        try {
            // check if there are already fields declared
            assertTrue(Person.class.getDeclaredFields().length != 0,"Class Person hasn't declared any members yet.");
            // check if all fields are named correctly, private,...
            assertTrue(Arrays.stream(Person.class.getDeclaredFields()).allMatch(
                    field -> Modifier.toString(field.getModifiers()).equals("private") && (
                            field.getName().equals("first") || field.getName().equals("last")
                                    || field.getName().equals("mobile") || field.getName().equals("age")
                                    || field.getName().equals("note"))
            ), "Please check your field names and modifiers!");
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void classPerson2() {
        try {
            assertEquals(5, Arrays.stream(Person.class.getDeclaredFields()).count(), "Member count of class Person not correct.");
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void classPersonConstructors1() {
        try {
            // check correct headers
            assertEquals(1,
                    Arrays.stream(Person.class.getConstructors()).filter(constructor
                            -> constructor.toString().equals("public at.ac.fhcampuswien.person.Person(java.lang.String,java.lang.String,java.lang.String,int,java.lang.String)")).count(),
                    "Constructor (String,String,String,int,String) missing");
            assertEquals(1,
                    Arrays.stream(Person.class.getConstructors()).filter(constructor
                            -> constructor.toString().equals("public at.ac.fhcampuswien.person.Person(java.lang.String)")).count(),
                    "Constructor (String) missing");
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void classPersonConstructors2() {
        try {
            // check correct splitting of line
            Constructor<?> co = Person.class.getConstructor(String.class);
            Person p = (Person) co.newInstance("Max:Mustermann:+43676111111:25:Student");
            Field mobile = Person.class.getDeclaredField("mobile");
            Field age = Person.class.getDeclaredField("age");
            mobile.setAccessible(true);
            age.setAccessible(true);
            assertEquals("+43676111111",(String)mobile.get(p),"String not correctly splitted.");
            assertEquals(25,(int) age.get(p),"String not correctly splitted.");
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void personToString() {
        try {
            Constructor<?> co = Person.class.getConstructor(String.class, String.class, String.class, int.class, String.class);
            Person p = (Person) co.newInstance("Obi-Wan", "Kenobi", "+4367636723", 99, "Master");
            String expected = "Firstname: Obi-Wan" + System.lineSeparator() +
                    "Lastname: Kenobi" + System.lineSeparator() +
                    "Mobile: +4367636723" + System.lineSeparator() +
                    "Age: 99" + System.lineSeparator() +
                    "Note: Master";
            assertEquals(expected, p.toString(), "toString Format not correct.");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problems might have occurred creating the Object. Also check return types.");
        }
    }

    @Test
    public void hasSameLastName() {
        try {
            Method m = Person.class.getMethod("hasSameLastName", Person.class);
            Constructor<?> co = Person.class.getConstructor(String.class, String.class, String.class, int.class, String.class);
            Person p1 = (Person) co.newInstance("Obi-Wan", "Kenobi", "+4367636723", 99, "Master");
            Person p2 = (Person) co.newInstance("No", "Kenobi", "", 99, "Nothing");
            Person p3 = (Person) co.newInstance("Snoke", "", "Secret", 1000, "Leader");
            assertTrue((boolean) m.invoke(p1, p2));
            assertFalse((boolean) m.invoke(p1, p3));
        } catch (NoSuchMethodException nsme){
            nsme.printStackTrace();
            fail("There should be a method called hasSameLastName.");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problems might have occurred creating the Object. Also check return types.");
        }
    }

    @Test
    public void lastNameStartsWith() {
        try {
            Method m = Person.class.getMethod("lastNameStartsWith", String.class);
            Constructor<?> co = Person.class.getConstructor(String.class, String.class, String.class, int.class, String.class);
            Person p1 = (Person) co.newInstance("Obi-Wan", "Kenobi", "+4367636723", 99, "Master");
            assertTrue((boolean) m.invoke(p1, "Keno"));
            assertFalse((boolean) m.invoke(p1, "eno"));
        } catch (NoSuchMethodException nsme){
            nsme.printStackTrace();
            fail("There should be a method called lastNameStartsWith.");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problems might have occurred creating the Object. Also check return types.");
        }
    }

    @Test
    public void containsInFirstName() {
        try {
            Method m = Person.class.getMethod("containsInFirstName", String.class);
            Constructor<?> co = Person.class.getConstructor(String.class, String.class, String.class, int.class, String.class);
            Person p1 = (Person) co.newInstance("Obi-Wan", "Kenobi", "+4367636723", 99, "Master");
            assertTrue((boolean) m.invoke(p1, "bi"));
            assertFalse((boolean) m.invoke(p1, "ee"));
        } catch (NoSuchMethodException nsme){
            nsme.printStackTrace();
            fail("There should be a method called containsInFirstName.");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problems might have occurred creating the Object. Also check return types.");
        }
    }

    @Test
    public void replaceVowelsInNote() {
        try {
            Method m = Person.class.getMethod("replaceVowelsInNote");
            Constructor<?> co = Person.class.getConstructor(String.class, String.class, String.class, int.class, String.class);
            Person p1 = (Person) co.newInstance("Obi-Wan", "Kenobi", "+4367636723", 99, "Master");
            Field note = Person.class.getDeclaredField("note");
            note.setAccessible(true);
            m.invoke(p1);
            assertEquals("M*st*r", ((String) note.get(p1)));
            assertNotEquals("Master", ((String) note.get(p1)));
        } catch (NoSuchMethodException nsme){
            nsme.printStackTrace();
            fail("There should be a method called replaceVowelsInNote.");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problems might have occurred creating the Object. Also check return types.");
        }
    }

    @Test
    public void replaceInNote() {
        try {
            Method m = Person.class.getMethod("replaceInNote", String.class, String.class);
            Constructor<?> co = Person.class.getConstructor(String.class, String.class, String.class, int.class, String.class);
            Person p1 = (Person) co.newInstance("Obi-Wan", "Kenobi", "+4367636723", 99, "Master of Disaster");
            Field note = Person.class.getDeclaredField("note");
            note.setAccessible(true);
            m.invoke(p1, "ast","***");
            assertEquals("M***er of Dis***er", ((String) note.get(p1)));
            assertNotEquals("Master of Disaster", ((String) note.get(p1)));
        } catch (NoSuchMethodException nsme){
            nsme.printStackTrace();
            fail("There should be a method called replaceInNote.");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problems might have occurred creating the Object. Also check return types.");
        }
    }

    @Test
    public void isAnagramNote() {
        try {
            Method m = Person.class.getMethod("isAnagramNote", Person.class);
            Constructor<?> co = Person.class.getConstructor(String.class, String.class, String.class, int.class, String.class);
            Person p1 = (Person) co.newInstance("Justin", "Timberlake", "+4367636723", 0, "Justin Timberlake");
            Person p2 = (Person) co.newInstance("Justin", "Timberlake", "+4367636723", 0, "I m a Jerk but listen");
            Person p3 = (Person) co.newInstance("Justin", "Timberlake", "+4367636723", 0, "I a Jerk but listen");
            assertTrue((boolean)m.invoke(p1, p2), "Actually an anagram.");
            assertFalse((boolean)m.invoke(p1, p3), "Actually not an anagram.");
        } catch (NoSuchMethodException nsme){
            nsme.printStackTrace();
            fail("There should be a method called isAnagramNote.");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problems might have occurred creating the Object. Also check return types.");
        }
    }

    @Test
    public void classPersonManager1() {
        try {
            // check if there are already fields declared
            assertTrue(PersonManager.class.getDeclaredFields().length != 0,"Class PersonManager hasn't declared any members yet.");
            // check if there is exactly one field declared
            assertEquals(1, Arrays.stream(PersonManager.class.getDeclaredFields()).count(), "Member count of class PersonManager not correct.");
            // check if all fields are named correctly, private,...
            assertTrue(Arrays.stream(PersonManager.class.getDeclaredFields()).allMatch(
                    field -> Modifier.toString(field.getModifiers()).equals("private") &&
                            field.getName().equals("persons") && field.getType().toString().equals(Person[].class.toString())
            ), "Please check your field names, types and modifiers!");
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void classPersonManager2() {
        try {
            // check constructor
            assertEquals(1,
                    Arrays.stream(PersonManager.class.getConstructors()).filter(constructor
                            -> constructor.toString().equals("public at.ac.fhcampuswien.person.PersonManager(at.ac.fhcampuswien.person.Person[])")).count(),
                    "Constructor (Person[]) missing");
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void classPersonManager3() {
        try {
            Method m = PersonManager.class.getMethod("getPersons");
            Constructor<?> co = PersonManager.class.getConstructor(Person[].class);
            Constructor<?> co2 = Person.class.getConstructor(String.class, String.class, String.class, int.class, String.class);
            Person p1 = (Person) co2.newInstance("Justin", "Timberlake", "+4367636723", 0, "Justin Timberlake");
            Person p2 = (Person) co2.newInstance("Justin", "Timberlake", "+4367636723", 0, "I m a Jerk but listen");
            Person [] passed = {p1,p2};
            PersonManager pm = (PersonManager) co.newInstance((Object) passed);
            Person [] retrieved = (Person [])m.invoke(pm);
            assertEquals(2, retrieved.length);
        } catch (NoSuchMethodException nsme){
            nsme.printStackTrace();
            fail("There should be a method called getPersons.");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problems might have occurred creating the Object. Also check return types.");
        }
    }

    @Test
    public void printPersonsWithSameName() {
        try {
            Method m1 = PersonManager.class.getMethod("printPersonsWithSameName", String.class);
            Method m2 = Person.class.getMethod("getName");
            Constructor<?> co = PersonManager.class.getConstructor(Person[].class);
            Constructor<?> co2 = Person.class.getConstructor(String.class, String.class, String.class, int.class, String.class);
            Person p1 = (Person) co2.newInstance("P", "1", "", 0, "");
            Person p2 = (Person) co2.newInstance("P", "2", "", 0, "");
            Person p3 = (Person) co2.newInstance("P", "3", "", 0, "1");
            Person p4 = (Person) co2.newInstance("P", "4", "", 0, "");
            Person p5 = (Person) co2.newInstance("P", "3", "", 0, "2");
            Person [] passed = {p1,p2,p3,p4,p5};
            PersonManager pm = (PersonManager) co.newInstance((Object) passed);
            m1.invoke(pm, "P 3");
            String expected = "Firstname: P" + System.lineSeparator() +
                    "Lastname: 3" + System.lineSeparator() +
                    "Mobile: " + System.lineSeparator() +
                    "Age: 0" + System.lineSeparator() +
                    "Note: 1" + System.lineSeparator() +
                    "Firstname: P" + System.lineSeparator() +
                    "Lastname: 3" + System.lineSeparator() +
                    "Mobile: " + System.lineSeparator() +
                    "Age: 0" + System.lineSeparator() +
                    "Note: 2" + System.lineSeparator();
            assertEquals(expected, bos.toString());
        } catch (NoSuchMethodException nsme){
            nsme.printStackTrace();
            fail("There should be a method called getName & printPersonsWithSameName.");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problems might have occurred creating the Object. Also check return types.");
        }
    }

    @Test
    public void isPalindrome() {
        try {
            Method m = App.class.getMethod("isPalindrome", StringBuilder.class);
            assertTrue((boolean)m.invoke(null, new StringBuilder("Was it a car or a cat I saw")), "'Was it a car or a cat I saw' is actually a palindrome.");
            assertFalse((boolean)m.invoke(null, new StringBuilder("Baby Elephant")), "'Baby Elephant' is actually not a palindrome. Of course we wish.");
        } catch (NoSuchMethodException nsme){
            nsme.printStackTrace();
            fail("There should be a method called isPalindrome.");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problems might have occurred creating the Object. Also check return types.");
        }
    }

    @Test
    public void concatenateStrings() {
        try {
            Method m = App.class.getMethod("concatenateStrings");
            ps.println("First");
            ps.println("Second");
            ps.println("Third");
            String expected = "First" + System.lineSeparator() +
                    "FirstSecond" + System.lineSeparator() +
                    "FirstThirdSecond" + System.lineSeparator();
            m.invoke(null);
            assertEquals(expected, bos.toString());

        } catch (NoSuchMethodException nsme){
            nsme.printStackTrace();
            fail("There should be a method called concatenateStrings.");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Problems might have occurred creating the Object. Also check return types.");
        }
    }
}
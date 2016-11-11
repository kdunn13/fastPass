package edu.gatech.seclass.fastpass;

/**
 * Created by Kevin on 11/6/2016.
 */

public class form {

    public int formID;
    public String firstName;
    public String lastName;

    @Override
    public String toString()
    {
        return firstName + " " + lastName;
    }
}

package hippos;

import utils.Log;

import java.util.StringTokenizer;

public class Person {
    private String name;
    private String firstname1;
    private String firstname2;
    private String lastname;

    public Person(String name) {
        try {
            /* Ei tarvi enään
            for(int i = name.indexOf("(") ; i >= 0; i = -1) {
                name = name.substring(0, i);
            };*/

            this.name = name;

            StringTokenizer st = new StringTokenizer(this.name);

            if(st.hasMoreTokens())
                firstname1 = st.nextToken();
            else
                return;
                //Log.write("Person.Person( " + name + " ): Nimi puuttuu, tai sitä ei pystytä parsimaan");

            while(st.countTokens() > 1)
                st.nextToken();
            if(st.hasMoreTokens())
                lastname = st.nextToken();
            if(st.hasMoreTokens())
                Log.write("Person.Person( " + name + " ) ?? Outo nimimuoto");

            if(firstname1 != null && firstname1.indexOf("-") > 0) {
                StringTokenizer fst = new StringTokenizer(firstname1, "-");
                firstname1 = fst.nextToken();
                firstname2 = fst.nextToken();
            }

        } catch (NullPointerException e) {
            // valmentajaa ei ole

        } catch (Exception e) {
            e.printStackTrace();
            Log.write(e);
        }

    }

    /*
            StringTokenizer st = new StringTokenizer(this.name);

     */
    protected String initSQLSearchStr() {
        StringBuffer sb = new StringBuffer();
        sb.append(firstname1 + "%");
        if (firstname2 != null) {
            sb.append("-" + firstname2 + "%");
        }
        sb.append(" " + lastname);

        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String toString() {
        return name;
    }

    public String getFirstname1() {
        return firstname1;
    }

    public String getFirstname2() {
        return firstname2;
    }

    public String getLastname() {
        return lastname;
    }
}
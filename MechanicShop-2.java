/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class MechanicShop{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	public MechanicShop(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");
			
			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}catch(Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}
	
	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 * 
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate (String sql) throws SQLException { 
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
	    stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;
		
		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 * 
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
		//creates a statement object 
		Statement stmt = this._connection.createStatement (); 
		
		//issues the query instruction 
		ResultSet rs = stmt.executeQuery (query); 
	 
		/*
		 * obtains the metadata object for the returned result set.  The metadata 
		 * contains row and column info. 
		*/ 
		ResultSetMetaData rsmd = rs.getMetaData (); 
		int numCol = rsmd.getColumnCount (); 
		int rowCount = 0; 
	 
		//iterates through the result set and saves the data returned by the query. 
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>(); 
		while (rs.next()){
			List<String> record = new ArrayList<String>(); 
			for (int i=1; i<=numCol; ++i) 
				record.add(rs.getString (i)); 
			result.add(record); 
		}//end while 
		stmt.close (); 
		return result; 
	}//end executeQueryAndReturnResult
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		ResultSetMetaData rsmd = rs.getMetaData ();
      		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;

		//iterates through the result set and count nuber of results.
		if(rs.next()){
			rowCount++;
		}//end while

		/*
		boolean outputHeader = true;
                while (rs.next()){
	 	    if(outputHeader){
	    	        for(int i = 1; i <= numCol; i++){
		            System.out.print(rsmd.getColumnName(i) + "\t");
	            	}
	    
  		    	System.out.println();
	            	outputHeader = false;
	            }
	
                    for (int i=1; i<=numCol; ++i)
         	    	System.out.print (rs.getString (i) + "\t");
	
         	    System.out.println ();
         	    ++rowCount;
      		}
		*/

		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current 
	 * value of sequence used for autogenerated keys
	 * 
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	
	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();
		
		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
	         // ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 * 
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
				"Usage: " + "java [-classpath <classpath>] " + MechanicShop.class.getName () +
		            " <dbname> <port> <user>");
			return;
		}//end if
		
		MechanicShop esql = null;
		
		try{
			System.out.println("(1)");
			
			try {
				Class.forName("org.postgresql.Driver");
			}catch(Exception e){

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}
			
			System.out.println("(2)");
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];
			
			esql = new MechanicShop (dbname, dbport, user, "");
			
			boolean keepon = true;
			while(keepon){
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. AddCustomer");
				System.out.println("2. AddMechanic");
				System.out.println("3. AddCar");
				System.out.println("4. InsertServiceRequest");
				System.out.println("5. CloseServiceRequest");
				System.out.println("6. ListCustomersWithBillLessThan100");
				System.out.println("7. ListCustomersWithMoreThan20Cars");
				System.out.println("8. ListCarsBefore1995With50000Milles");
				System.out.println("9. ListKCarsWithTheMostServices");
				System.out.println("10. ListCustomersInDescendingOrderOfTheirTotalBill");
				System.out.println("11. < EXIT");
				
				/*
				 * FOLLOW THE SPECIFICATION IN THE PROJECT DESCRIPTION
				 */
				switch (readChoice()){
					case 1: AddCustomer(esql); break;
					case 2: AddMechanic(esql); break;
					case 3: AddCar(esql); break;
					case 4: InsertServiceRequest(esql); break;
					case 5: CloseServiceRequest(esql); break;
					case 6: ListCustomersWithBillLessThan100(esql); break;
					case 7: ListCustomersWithMoreThan20Cars(esql); break;
					case 8: ListCarsBefore1995With50000Milles(esql); break;
					case 9: ListKCarsWithTheMostServices(esql); break;
					case 10: ListCustomersInDescendingOrderOfTheirTotalBill(esql); break;
					case 11: keepon = false; break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if				
			}catch(Exception e){
				// ignored.
			}
		}
	}

	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}//end readChoice
	
	public static void AddCustomer(MechanicShop esql){//1
		String fname = "";
		String lname = "";
		String phone = "";
		String address = "";
		String query;
		char[] str_to_char;
		Scanner input = new Scanner(System.in);
		boolean invalid = false;
		int rowCount = 0;
		
		//user input for first name
		System.out.print("Enter customer's first name: ");
		fname = input.nextLine();
		str_to_char = fname.toCharArray();
		for (char c : str_to_char) {
		    if (Character.isDigit(c)) {
			System.out.println("First name can't contain numbers!");
			invalid = true;
			break;
		    }
		}
	 
	 	//user input for last name	
		if (!invalid)
		{
		    System.out.print("Enter customer's last name: ");
                    lname = input.nextLine();
                    str_to_char = lname.toCharArray();
                    for (char c : str_to_char) {
                        if (Character.isDigit(c)) {
                            System.out.println("Last name can't contain numbers!");
                            invalid = true;
                            break;
                        }
                    }
		}

		//user input for phone number
		do {
		    if (!invalid)
		    {
			    System.out.print("Enter customer's phone number in format (xxx)xxx-xxxx: ");
			    phone = input.nextLine();
		        str_to_char = phone.toCharArray();

				if (str_to_char.length != 13){
				    System.out.println("Phone number must be in the format (xxx)xxx-xxxx!");
				    invalid = true;
				    break;
				}

				//reads through every character
				// ( x x x ) x x x - x  x  x  x 
				// 1 2 3 4 5 6 7 8 9 10 11 12 13
				//checks 1st char
				if (str_to_char[0] != '('){
				    System.out.println("Invalid phone number!");
				    invalid = true;
				    break;
				}
				//checks 2nd,3rd,4th chars
				if (str_to_char[1] < '0' || str_to_char[1] > '9' ||
				    str_to_char[2] < '0' || str_to_char[2] > '9' ||
				    str_to_char[3] < '0' || str_to_char[3] > '9') {
	                System.out.println("Invalid phone number!");
	                invalid = true;
				    break;
	            }
				//checks 5th char
				if (str_to_char[4] != ')'){
	                System.out.println("Invalid phone number!");
	                invalid = true;
				    break;
	            }
				//checks 6th,7th,8th chars
				if (str_to_char[5] < '0' || str_to_char[5] > '9' ||
				    str_to_char[6] < '0' || str_to_char[6] > '9' ||
				    str_to_char[7] < '0' || str_to_char[7] > '9'){
	                System.out.println("Invalid phone number!");
	                invalid = true;
				    break;
	            }
				//checks 9th char
				if (str_to_char[8] != '-'){
	                System.out.println("Invalid phone number!");
	                invalid = true;
				    break;
	            }	
				//checks 10th,11th,12th,13th chars
				if (str_to_char[9] < '0' || str_to_char[9] > '9' ||
	                            str_to_char[10] < '0' || str_to_char[10] > '9' ||
	                            str_to_char[11] < '0' || str_to_char[11] > '9' ||
				    str_to_char[12] < '0' || str_to_char[12] > '9'){
	                System.out.println("Invalid phone number!");
	                invalid = true;
				    break;
	            }
		    }

		    break;
		} while(true);

		//user input for address
		if (!invalid) {
            System.out.print("Enter customer's address: ");
            address = input.nextLine();
        }	

		if (!invalid) {
		    rowCount = 0;
		    try {
			    query = "SELECT fname FROM Customer WHERE fname = '" + fname + 
						"' AND lname = '" + lname + "' AND phone = '" + phone + "' AND address = '"
						+ address + "'";

		 		esql.executeQuery(query);
		    }catch (Exception e){
		  		System.err.println(e.getMessage());
		    }

		    //checks if Customer already exists
		    if (rowCount > 0) {
				System.out.println("Customer already exists!");
				return;
		    }
		
		    //execute query
		    try {
                query = "INSERT INTO Customer(id, fname, lname, phone, address) " +
                        "SELECT MAX(id) + 1,'" + fname + "','" + lname + "','" + phone +
                        "','" + address + "' FROM Customer";

                esql.executeUpdate(query);

				//prints inserted Customer
				query = "SELECT * FROM Customer WHERE fname = '" + fname +
	                                "' AND lname = '" + lname + "' AND phone = '" + phone + "' AND address = '"
	                                + address + "'";
				esql.executeQueryAndPrintResult(query);
		
                System.out.println("Customer successfully added to database.");
            }catch (Exception e){ System.err.println(e.getMessage()); } 
	    }
	}
		
	public static void AddMechanic(MechanicShop esql){//2
		String fname = "";
	    String lname = "";
	    String years_of_exp = "";
	    String query;
	    int vin_num_limit = 16;
	    int mechanic_count = 0;
	    char[] str_to_char;
	    Scanner scanner = new Scanner(System.in);
	    boolean valid = false;

	    try {
	    	System.out.print("Please enter mechanic's first name: ");
	    	fname = scanner.nextLine();
	    	str_to_char = fname.toCharArray();
	    	for(char e: str_to_char){
	    		if(!Character.isDigit(e)) {
	            	valid = true;
	    		}
	         	else {
	            	valid = false;
		 	System.out.println("First name must not contain numbers!");	
			return;
	            }
	        } 

		try {
                        System.out.print("Please enter mechanic's last name: ");
                        lname = scanner.nextLine();

                        str_to_char = lname.toCharArray();

                        for (char letter : str_to_char) {
                            if (!Character.isDigit(letter)) {
                                valid = true;
                            }
                            else if(Character.isDigit(letter)){
                                System.out.println("Last name must not contain numbers!");
                                valid = false;
                                return;
                            }
                        }
                     }catch (Exception e) {
                         System.out.println("Please type in the correct response.");
                         return;
                     }

	 		System.out.print("Please enter years of experience: ");
			years_of_exp = scanner.nextLine();	

		try {
		    int int_years = Integer.parseInt(years_of_exp);
		}catch(Exception e) {
		    System.out.println("Years can only contain numbers!");
		    return;
		}

	 	}catch (Exception e) {
	            System.out.println(e.getMessage());
		    return;
	        }

	    if(valid){
	     	try {
	         	query = "INSERT INTO Mechanic(id, fname, lname, experience) " +
	                 	"SELECT MAX(id) + 1,'" + fname + "','" + lname + "','" + years_of_exp + "' FROM Mechanic";

	         	esql.executeUpdate(query);

			query = "SELECT * FROM Mechanic WHERE " + 
				"fname = '" + fname + "' AND lname = '" + lname + "' AND experience = '" + 
				years_of_exp + "'";

			esql.executeQueryAndPrintResult(query);

	         	System.out.println("Mechanic successfully added to database.");
	     	}catch (Exception e){
	        	System.err.println(e.getMessage());
	     	}

	    }
	}
	
	public static void AddCar(MechanicShop esql){//3
  		String vin = "";
        String make = "";
        String model = "";
        String year = "";
        String query;
        int vin_num_limit = 16;
        char[] str_to_char;
        Scanner scanner = new Scanner(System.in);
        boolean valid = false;
	    int rowCount = 0;
	    int int_year;



        System.out.print("Please enter the vin # for this vehicle: ");
        vin = scanner.nextLine();

        if (vin.length() == vin_num_limit) {
            valid = true;
        }
        else {
        	System.out.println("Incorrect VIN");
			return;
        }

	//checks if vin exists already
	try {
		    query = "SELECT * FROM Car WHERE " + 
			        "vin = '" + vin + "'";

		    rowCount = esql.executeQuery(query);
		}catch (Exception e){
		    System.err.println(e.getMessage());
		}

		if (rowCount > 0) {
		   System.out.println("Vin # already exists!");
		   return;
		}

        if(valid) {
            System.out.print("Please enter the make for this vehicle: ");
            make = scanner.nextLine();

            str_to_char = make.toCharArray();
            for (char letter : str_to_char) {
                if (!Character.isDigit(letter)) {
                    valid = true;
                }
                else if(Character.isDigit(letter)){
                        valid = false;
	 	    		System.out.println("Vehicle's make must not contain numbers!");
					return;
                }
            }
        }

        if(valid){
        	System.out.print("Please the vehicle's model: ");
        	model = scanner.nextLine();

            try {
                System.out.print("Please enter a year (YYYY): ");
                year = scanner.nextLine();
                if (year.length() == 4) {
                    valid = true;
                }
            }catch(Exception e){
                System.err.println("Year must be in the format YYYY!");
		    	return;
            }
        }

	    try {
	        int_year = Integer.parseInt(year);	
	    }catch(Exception e){
	 		System.err.println("Year must only contain numbers!");
			return;
	    }

	    if (int_year > 2021) {
			System.out.println("Car does not exist yet!");
			return;
	    }

        if(valid) {
	        try {
	            query = "INSERT INTO Car(vin, make, model, year) " +
	                      "VALUES ('" + vin + "','" + make + "','" + model + "','" + year + "')";

	            esql.executeUpdate(query);

		    query = "SELECT model, vin FROM Car "
                        + "WHERE vin = '" + vin + "'";

		    esql.executeQueryAndPrintResult(query);

		    String pick = "";
		    int cust_id = 0;
		    System.out.println("Car is owned by customer with id: ");
		    pick = scanner.nextLine();

		    cust_id = Integer.parseInt(pick);

		    query = "INSERT INTO Owns(ownership_id, customer_id, car_vin) " + 
			    "SELECT MAX(ownership_id) + 1," + cust_id + ",'" + vin + "' FROM Owns";

		    esql.executeUpdate(query);

	            System.out.println("Car successfully added to database.");
	        }catch (Exception e){
	            System.err.println(e.getMessage());
        	}
        }		
	}
	
	public static void InsertServiceRequest(MechanicShop esql){//4
		
	      String temp_String = "";
        String customer_id = "";
        String car_vin = "";
        String data = "";
        String odometer_string = "";
        String lname = "";
        int odometer = 0;
        String complain = "";
        String query = "";
        int month = 0;
        int day = 0;
        int year = 0;
        String time = "";
        String date = "";
        String pick = "";
	int cust_id = 0;

        boolean valid = false;
        char[] str_to_char;
        int customer_count = 0;
        int job_count = 0;
            System.out.println("----------------------");
            System.out.println("Open a Service Request");
            System.out.println("----------------------");
        Scanner scanner = new Scanner(System.in);
        Scanner input = new Scanner(System.in);


        try {
                System.out.println("Please enter last name of customer: ");
                lname = scanner.nextLine();
                str_to_char = lname.toCharArray();
                for (char e : str_to_char) {
                    if (Character.isDigit(e)) {
                        System.out.println("Please no integers!");
                        return;
                    }
                }
        }catch(Exception e){
        System.err.println(e.getMessage());
        return;
  }

                try{
            query = "SELECT * " +
                   "FROM Customer " +
                    "WHERE lname = '" + lname + "'";
            customer_count = esql.executeQuery(query);
            System.out.println("Total row(s) affected: " + customer_count);
            if(customer_count == 0){
                System.out.println("Last name not found in database. Please enter new customer info!");
                AddCustomer(esql);
		return;
}
        else if(customer_count > 0){
                System.out.println("Customers with the name: " + lname);
                customer_count = esql.executeQueryAndPrintResult(query);
                System.out.println("Confirm the id of customer who initiated request: ");
                pick = scanner.nextLine();

		try{
			cust_id = Integer.parseInt(pick);
			query = "SELECT * FROM Customer " + 
				"WHERE id = " + cust_id + " AND lname = '" + lname + "'";
			customer_count = esql.executeQuery(query); 
		}catch(Exception e){
		    System.err.println(e.getMessage());
		}

		if (customer_count < 1) {
		    System.out.println("Customer id does not match!");
		    return;
		}

                System.out.println("List of cars owned by customer " + pick + ":");

		try{
			cust_id = Integer.parseInt(pick);		
		}catch(Exception e){
			System.err.println("Customer id must be a valid integer!");
		}
	
                query = "SELECT model, vin FROM Car C, Customer S, Owns O "
                        + "WHERE S.lname = '" + lname + "' AND S.id = " + cust_id + " AND O.customer_id = S.id AND O.car_vin = C.vin";
                customer_count = esql.executeQuery(query);

		if (customer_count < 1) {
			System.out.println("Customer does not have a car yet. Please add a new car.");
			AddCar(esql);
		}

                //System.out.println("Total row(s) affected: " + customer_count);
                /*
                query = "SELECT model, vin FROM Car C, Customer S, Owns O "
                        + "WHERE S.lname = '" + lname + "' AND S.id = " + cust_id + " AND O.customer_id = S.id AND O.car_vin = C.vin";
		*/

		query = "SELECT model, vin FROM Car C, Customer S, Owns O "
                        + "WHERE S.lname = '" + lname + "' AND S.id = " + cust_id + " AND O.customer_id = S.id AND O.car_vin = C.vin";
                customer_count = esql.executeQueryAndPrintResult(query);

                System.out.println("Enter the car vin in need of service:");
                car_vin = scanner.nextLine();

		try{
			query = "SELECT model, vin FROM Car C, Customer S, Owns O "
                        + "WHERE S.lname = '" + lname + "' AND S.id = " + cust_id + " AND O.customer_id = S.id AND O.car_vin = C.vin";
	
			customer_count = esql.executeQuery(query);
		}catch(Exception e){
			System.err.println(e.getMessage());
		}

		if (customer_count < 1) {
			System.out.println("Car vin does not match!");
			return;
		}

                System.out.println("Creating Service Request for car with vin " + car_vin + "!");

}

        }catch(Exception e){
            System.err.print(e.getMessage());
        }
	
	/*
        try{
		
	 	System.out.println("Please enter the customer id: ");
		customer_id = scanner.nextLine();
		
                //System.out.println("Please enter the customer id: ");
                //customer_id = scanner.nextLine();
        }catch(Exception e){
                System.err.println("Customer id must be a valid number!");
        }
	
                try{
            byte vin_limit = 16;
            while(true) {
                System.out.println("Please enter the vin # of vehicle: ");
                car_vin = scanner.nextLine();
                if (car_vin.length() == vin_limit) {
                    break;
                }
                else{
                    System.out.println("Please enter 16 characters!");
                }

            }
        }catch(Exception e){
      System.err.println(e.getMessage());
            return;
        }
	*/

                try{
            int year_limit = 0;
            System.out.println("Please enter the month: ");
            month = input.nextInt();
            if(month > 0 && month <= 12){
               date += String.valueOf(month) + "/";
            }
            else{
                System.out.println("That is not a valid month");
                return;
            }
            System.out.println("Please enter the day");
            day = input.nextInt();
            if (day == 30 && (month == 3 || month == 5 || month == 7 ||
                    month == 8 || month == 10 || month == 12)) {
                System.out.println("Day out of range for the month " + String.valueOf(month) + "!");
                return;
            }

            if (day == 31 && (month == 4 || month == 6 || month == 9 ||
                    month == 11)) {
                System.out.println("Day out of range for the month " + String.valueOf(month) + "!");
                return;
            }

            if (day > 28 && month == 2) {
        System.out.println("Day out of range for the month " + String.valueOf(month) + "!");
                return;
            }
            date += String.valueOf(day) + "/";
            while(true) {
                System.out.println("Please enter the year");
                year = input.nextInt();
                year_limit = String.valueOf(year).length();

                if (year_limit == 4) {
              date += String.valueOf(year) + " ";
                    break;
                } else {
                    System.out.println("Please enter a year with 4 integers!");
                }
            }
            System.out.println(date);

        }catch(Exception e){
            System.err.println(e.getMessage());
            return;
        }

                try {
            while(true) {
                System.out.println("Please enter mileage: ");
                odometer = input.nextInt();
                if (odometer > 0) {
                odometer_string = String.valueOf(odometer);
                    break;
                } else {
                    System.out.println("Please enter a number higher than 0!");
                }
            }
        }catch(Exception e){
               System.err.println(e.getMessage());
               return;
            }

                System.out.println("Please enter complaint: ");
                complain = scanner.nextLine();
        try {
            query = "INSERT INTO Service_Request(rid, customer_id, car_vin, date, odometer, complain) " +
                     "SELECT MAX(rid) + 1," + cust_id + ",'" + car_vin + "','" + date + "'," +
                                     odometer + ",'" + complain + "' FROM Service_Request";

            esql.executeUpdate(query);

//Print service request
                query = "SELECT * FROM Service_Request WHERE customer_id = '" + cust_id + "' AND complain = '"
                        + complain + "'";
                esql.executeQueryAndPrintResult(query);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return;
        }

                System.out.println("Service Request successfully added.");
        }

	
	public static void CloseServiceRequest(MechanicShop esql) throws Exception{//5
		Scanner input = new Scanner(System.in);
		char[] str_to_char;

		String rid = "";
		String mid = "";
		String date_input = "";
		String service_date = "";
		int month = 0;
		int day = 0;
		int year = 0;
		String date= "";
		String comment = "";
		String bill = "";
		String query;
		int service_count = 0;  //used to check if service request id exists
		int mechanic_count = 0; //used to check if mechanic id exists
		int rowCount = 0;
		int bill_int = 0;
	
		//user input service id
		System.out.print("Enter service request id: ");
		rid = input.nextLine();

		try {
            int convert_service;
            convert_service = Integer.parseInt(rid);
        }catch (Exception e){
            System.err.println("Service id must be a valid number!");
            return;
        }
		
		//checks if service id exists
		try {
		    query = "SELECT rid " +
			        "FROM   Service_Request " +
			        "WHERE  rid = " + rid;

		    service_count = esql.executeQuery(query);
		}catch (Exception e){
		    System.err.println(e.getMessage());
		    return;
		}

		if (service_count == 0) {
		    System.out.println("Service request does not exist!");
		    return;
		}

		//checks if service request is already closed
		try {
            query = "SELECT c.rid " +
                    "FROM Closed_Request c " +
                    "WHERE c.rid = " + rid;
		
		    rowCount = esql.executeQuery(query);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return;
        }

		if (rowCount > 0) {
		    System.out.println("Service Request " + rid + " is already closed!");
		    return;
		}

		//user input mechanic id
		System.out.print("Enter mechanic id: ");
        mid = input.nextLine();

		try { 
		   int convert_mech;
		   convert_mech = Integer.parseInt(mid);  
		}catch (Exception e){
		   System.err.println("Mechanic id must be a number!");
           return;
		}

		//checks if mechanic id exists
		try {
            query = "SELECT id " +
                    "FROM   Mechanic " +
                    "WHERE  id = " + mid;

            mechanic_count = esql.executeQuery(query);
        }catch (Exception e){
            System.err.println(e.getMessage());
        	return;
        }

		if (mechanic_count < 1) {
            System.out.println("Mechanic does not exist!");
		    return;
        }

		//user input month
		System.out.print("Enter month: ");
		date_input = input.nextLine();

		//checks if month is valid
		try {
		    month = Integer.parseInt(date_input);
		}catch (Exception e){
		    System.out.println("Invalid month!");
		    return;
		}

		if (month < 1 || month > 12) {
		    System.out.println("Invalid range! Month must be in range 1-12.");
		    return;
		}
		else {
		    date = date + String.valueOf(month) + "/"; 
		}

		//user input day of month
		System.out.print("Enter day: ");	
		date_input = input.nextLine();

		//checks if day is valid
		try {
		    day = Integer.parseInt(date_input);
		}catch (Exception e){
		    System.out.println("Invalid day!");
		    return;
		}

		if (day < 0 || day > 31) {
		    System.out.println("Invalid range! Month must be in range 1-31.");
		    return;
		}

		if (day == 30 && (month == 3 || month == 5 || month == 7 || 
			          month == 8 || month == 10 || month == 12)) {
		    System.out.println("Day out of range for the month " + String.valueOf(month) + "!");
		    return;
		}

		if (day == 31 && (month == 4 || month == 6 || month == 9 || 
				  month == 11)) {
		    System.out.println("Day out of range for the month " + String.valueOf(month) + "!");
                    return;
		}
		
		if (day > 28 && month == 2) {
		    System.out.println("Day out of range for the month " + String.valueOf(month) + "!");
		    return;
		}

		date = date + String.valueOf(day) + "/";

		//user input year
		System.out.print("Enter year in format YYYY: ");
		date_input = input.nextLine();

		//checks if year is valid
		try {
		    year = Integer.parseInt(date_input);
        }catch (Exception e){
		    System.out.println("Invalid year!");
		    return;
		}

		if (date_input.length() != 4) {
		    System.out.println("Year must be in format YYYY!");
		    return;
		} 

		if (date_input.length() < 1) {
		    System.out.println("Year must be positive!");
		    return;
		}

		date += String.valueOf(year);
	
		try {
		    query = "SELECT rid " + 
			    "FROM   Service_Request " + 
			    "WHERE  rid = " + rid + " AND date <= '" + date + "'";

		    rowCount = esql.executeQuery(query);
		}catch (Exception e){
		    System.err.println(e.getMessage());
		}

		if (rowCount == 0) {
		    System.out.println("Closed service date must be greater than service request date!");
		    return;
		}

		//user input comment
		System.out.print("Enter comment for service request: ");
                comment = input.nextLine();

		//user input bill
		System.out.print("Enter the service bill: ");
		bill = input.nextLine();

		try {
		    bill_int = Integer.parseInt(bill);
		}catch (Exception e){
		    System.err.println("Invalid input for bill amount!");
		    return;
		}	

		if (bill_int < 0) {
	 	    System.out.println("Bill must be greater than 0!");
		    return;
		}

		try {
		    query = "INSERT INTO Closed_Request(wid, rid, mid, date, comment, bill) " + 
		 	    "SELECT MAX(wid) + 1,'" + rid + "','" + mid + "','" + date + "','" +
			            comment + "','" + bill + "' FROM Closed_Request";

		    esql.executeUpdate(query);

		    query = "SELECT * FROM Closed_Request WHERE " + 
			    "rid = '" + rid + "'";
		    esql.executeQueryAndPrintResult(query);
		}catch (Exception e){
		    System.out.println(e.getMessage());
		    return;
		}

		System.out.println("Service Request " + rid + " successfully closed.");
	}
	
	public static void ListCustomersWithBillLessThan100(MechanicShop esql){//6
		String query;

		try {
		    query = "SELECT DISTINCT fname, lname, c.bill, c.date, c.comment " + 
			    "FROM   Customer, Service_Request s, Closed_Request c " + 
			    "WHERE  id = s.customer_id AND s.rid = c.rid AND c.bill < 100 " +
			    "ORDER BY date ASC";
		  
		    int rowCount = esql.executeQueryAndPrintResult(query);  		    
		    System.out.println("total row(s): " + rowCount);
		}catch (Exception e){
		    System.err.println(e.getMessage());
		}
		
	}
	
	public static void ListCustomersWithMoreThan20Cars(MechanicShop esql){//7
		try{
			String query = "SELECT cars.fname, cars.lname, cars.numCars FROM (SELECT Owns.customer_id, Customer.fname, Customer.lname, COUNT(*) numCars FROM Owns,Customer WHERE Customer.id = Owns.customer_id GROUP BY Owns.customer_id, Customer.fname, Customer.lname) AS cars WHERE numCars > 20";
			
			int rowCount = esql.executeQueryAndPrintResult(query);
			System.out.println ("total row(s): " + rowCount);
		}catch(Exception e){
			System.err.println (e.getMessage());
		}
	}
	
	public static void ListCarsBefore1995With50000Milles(MechanicShop esql){//8
		try{
			String query = "SELECT Car.make, Car.model, Car.year, Service_Request.odometer FROM Car,Service_Request WHERE Service_Request.car_vin = Car.vin AND Service_Request.odometer < 50000 AND Car.year < 1995";
			int rowCount = esql.executeQueryAndPrintResult(query);
			System.out.println ("total row(s): " + rowCount);
		}catch(Exception e){
			System.err.println (e.getMessage());
		}
	}
	
	public static void ListKCarsWithTheMostServices(MechanicShop esql){//9
		try{
            System.out.print("The number of cars you would like to see (k): ");
            String res = in.readLine();
            int response = Integer.parseInt(res);
            if(1 > response){
                throw new NumberFormatException("Value should be positive and larger than 0.");
            }
            String query = "SELECT make, model, R.sreq FROM Car AS C, ( SELECT car_vin, COUNT(rid) AS sreq FROM Service_Request GROUP BY car_vin ) AS R WHERE R.car_vin = C.vin ORDER BY R.sreq DESC LIMIT "+response+";";
            int rowCount = esql.executeQueryAndPrintResult(query);
            System.out.println("total row(s): " + rowCount);
        }
        catch(Exception e){
            System.out.print("Your input is invalid! ");
            System.out.println(e.getMessage());
        }
	}
	
	public static void ListCustomersInDescendingOrderOfTheirTotalBill(MechanicShop esql){//9
		try{
			String query = "SELECT C.fname , C.lname, Total FROM Customer AS C, (SELECT sr.customer_id, SUM(CR.bill) AS Total FROM Closed_Request AS CR, Service_Request AS SR WHERE CR.rid = SR.rid GROUP BY SR.customer_id) AS A WHERE C.id=A.customer_id ORDER BY A.Total DESC;";
			int rowCount = esql.executeQueryAndPrintResult(query);
			System.out.println("total row(s): " + rowCount);
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
}

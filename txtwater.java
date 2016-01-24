import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import java.util.Scanner;
import java.util.*;
import java.text.*;
import java.io.*;
/*This is a program that can be used to count the number of encoder state changes
 * that occur before the user interrupts the program by entering in 'p' to print values
 * Must be run as sudo 	for WiringPi setup
 */
 
 public class txtwater {
        //results[0] = water in mL over whole period of time
	//results[1] = water in mL that day 
	//results[2] = water in mL that month
	//results[3] = water in mL that year
	//date[0] = day
	//date[1] = month
	//date[2] = year
	static int[] results = new int[4];
	static int[] date = new int[3];
	public static int[] returnResults(){
                return results;
        }
        public static void main(String args[]) throws InterruptedException {
		String fileName = "waterdata.txt";
		String fileName2 = "date.txt";
	        // This will reference one line at a time
	        String line = null;
		int data;
		//get stored values of water data
	        try {
        		// FileReader reads text files in the default encoding.
        		FileReader fileReader = new FileReader(fileName);

       	            	// Always wrap FileReader in BufferedReader.
            	    	BufferedReader bufferedReader = new BufferedReader(fileReader);

            		for(int i = 0;(line = bufferedReader.readLine()) != null; i++) {
                		data = Integer.parseInt(line);
				System.out.println(data);
				results[i] = data;
            		}

           		 // Always close files.
           		 bufferedReader.close();
        	}
        	catch(FileNotFoundException ex) {
            		System.out.println("Unable to open file '" + fileName + "'"); 
        	}
        	catch(IOException ex) {
            		System.out.println("Error reading file '"  + fileName + "'");
		}
		//Get stored date
                try {
                        // FileReader reads text files in the default encoding.
                        FileReader fileReader = new FileReader(fileName2);

                        // Always wrap FileReader in BufferedReader.
                        BufferedReader bufferedReader = new BufferedReader(fileReader);

                        for(int i = 0;(line = bufferedReader.readLine()) != null; i++) {
                                data = Integer.parseInt(line);
                                System.out.println(data);
                                date[i] = data;
                        }

                         // Always close files.
                         bufferedReader.close();
                }
                catch(FileNotFoundException ex) {
                        System.out.println("Unable to open file '" + fileName + "'");
                }
                catch(IOException ex) {
                        System.out.println("Error reading file '"  + fileName + "'");
                }

		//Date previousDate = new Date();
		//Create format for dates
		SimpleDateFormat day = new SimpleDateFormat("dd");
		SimpleDateFormat month = new SimpleDateFormat("MM");
		SimpleDateFormat year = new SimpleDateFormat("yyyy");
        	//declare gpio pins being tested
        	System.out.println("Currently testing GPIO pins 15");

        	// create gpio controller
        	final GpioController gpio = GpioFactory.getInstance();

        	// provision given gpio pins as input pins with pull up resistors enabled
        	final GpioPinDigitalInput water = gpio.provisionDigitalInputPin(RaspiPin.GPIO_15, PinPullResistance.PULL_UP); 
		//final GpioPinDigitalInput wire2 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_24, PinPullResistance.PULL_UP);

        	// create and register gpio pin listener
        	water.addListener(new GpioPinListenerDigital() {
        	    @Override
            	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                	// everytime pin changes state add to count
                	results[0]++;//mL of water over whole course of program
			//Every pin change is equivalent to 1 mL of water and since we're measuring in liters we'll add .001 each time the pin changes
			results[1]++;//Milliliters used that day
			results[2]++;//Milliliters used that month
			results[3]++;//Milliliters used that year
			        try {
            				// Assume default encoding.
            				FileWriter fileWriter =
                			new FileWriter(fileName);

			                // Always wrap FileWriter in BufferedWriter.
            				BufferedWriter bufferedWriter =
                			new BufferedWriter(fileWriter);

            				// Note that write() does not automatically
            				// append a newline character.
					//This overwrites old data with new data
            				//System.out.println(results[0]+" "+results[1]);
					bufferedWriter.write(String.valueOf(results[0]));
			                bufferedWriter.newLine();
            				bufferedWriter.write(String.valueOf(results[1]));
         			        bufferedWriter.newLine();
            				bufferedWriter.write(String.valueOf(results[2]));
                                        bufferedWriter.newLine();
            				bufferedWriter.write(String.valueOf(results[3]));
					bufferedWriter.flush();
            				// Always close files.
            				bufferedWriter.close();
        			}
        			catch(IOException ex) {
            				System.out.println("Error writing to file '" + fileName + "'");
            				// Or we could just do this:
            				// ex.printStackTrace();
       				}

        	    }

	        }); 
		//Run an infinite loop
		try{
			// Assume default encoding.
			FileWriter fileWriter =
                	new FileWriter(fileName2);

            		// Always wrap FileWriter in BufferedWriter.
            		BufferedWriter bufferedWriter =
                	new BufferedWriter(fileWriter);

			while(true){
				//Find current date
				Date currentDate = new Date();
				//Create a Scanner to read user input
        			Scanner input = new Scanner(System.in);
				//Look for character inputs 
        			char print = input.next().charAt(0);
				//Enter 'p' to print values 
        			if (print == 'p') {
					System.out.println("mL of water:" + results[0]);
					System.out.printf("Liters of water used today: %d\n", (results[1]/1000));
					System.out.printf("Liters of water used this month: %d\n", (results[2]/1000));
					System.out.printf("liters of water  used this year: %d\n", (results[3]/1000));
					//System.out.println(currentDate + " " + previousDate);
					//System.out.println(day.format(currentDate) + " " + day.format(previousDate));
				}
				//If the previous day is not equal to the current day ex. yesterday was the 12th and now it's 
				//the 13th the water used today resets and the current date becomes the previous date
				if (date[0] != Integer.parseInt(day.format(currentDate))){
					System.out.println("Reset day");
					results[1] = 0;
					//reset the previous date
					date[0] = Integer.parseInt(day.format(currentDate));
					date[1] = Integer.parseInt(month.format(currentDate));
					date[2] = Integer.parseInt(year.format(currentDate));
					bufferedWriter.write(String.valueOf(date[0]));
            				bufferedWriter.newLine();
            				bufferedWriter.write(String.valueOf(date[1]));
					bufferedWriter.newLine();
            				bufferedWriter.write(String.valueOf(date[2]));
					//previousDate=currentDate;
				}
				if (date[1] != Integer.parseInt(month.format(currentDate))){
                	        	System.out.println("Reset month");
					results[2] = 0;
					//Since the date is reset daily no need to reset it when the month changes
					//'Cause if the month has changed so has the day
                	        	//previousDate=currentDate;
                		}
 				if (date[2] != Integer.parseInt(year.format(currentDate))){
                		        System.out.println("Reset year");
					results[3] = 0;
					//^^see month explanation
                		        //previousDate=currentDate;
                		}
				returnResults();

			}
		}
		catch(IOException ex) {
        		System.out.println("Error writing to file '"+ fileName + "'");
            		// Or we could just do this:
            		// ex.printStackTrace();
        	}
	}
}

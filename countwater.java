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
/*This is a program that can be used to count the number of encoder state changes
 * that occur before the user interrupts the program by entering in 'p' to print values
 * Must be run as sudo 	for WiringPi setup
 */
 
 public class countwater {
    //initialize counter for both pins
	static int countwater = 0;
	static float Lperday = 0;
	static float Lpermonth = 0;
	static float Lperyear = 0; 
	static float[] results = new float[2];
	public static float[] returnResults(){
                return results;
        }
        public static void main(String args[]) throws InterruptedException {
		Date previousDate = new Date();
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
                	countwater++;
			//Every pin change is equivalent to 1 mL of water and since we're measuring in liters we'll add .001 each time the pin changes
			Lperday +=.001;
			Lpermonth +=.001;
			Lperyear +=.001;
			results[0] = Lperday;
			results[1] = Lpermonth;
			results[2] = Lperyear;

        	    }

	        }); 
		//Run an infinite loop
		while(true){
			//Find current date
			Date currentDate = new Date();
			//Create a Scanner to read user input
        		Scanner input = new Scanner(System.in);
			//Look for character inputs 
        		char print = input.next().charAt(0);
			//Enter 'p' to print values 
        		if (print == 'p') {
				System.out.println("mL of water:" + countwater);
				System.out.println("Water used today:" + Lperday);
				System.out.println("Water used this month:" + Lpermonth);
				System.out.println("Water used this year:" + Lperyear);
				//System.out.println(currentDate + " " + previousDate);
				//System.out.println(day.format(currentDate) + " " + day.format(previousDate));
			}
			//If the previous day is not equal to the current day ex. yesterday was the 12th and now it's 
			//the 13th the water used today resets and the current date becomes the previous date
			if (Integer.parseInt(day.format(previousDate)) != Integer.parseInt(day.format(currentDate))){
				System.out.println("Reset day");
				Lperday = 0;
				previousDate=currentDate;
			}
			if (Integer.parseInt(month.format(previousDate)) != Integer.parseInt(month.format(currentDate))){
                	        System.out.println("Reset month");
				Lpermonth = 0;
                	        previousDate=currentDate;
                	}
 			if (Integer.parseInt(year.format(previousDate)) != Integer.parseInt(year.format(currentDate))){
                	        System.out.println("Reset year");
				Lperyear = 0;
                	        previousDate=currentDate;
                	}
			returnResults();

		}

	}
}


package com.mycompany.boniuk_math.math.engine;


 	import java.io.FileNotFoundException;
	import java.io.FileOutputStream;
	import java.io.OutputStream;

	import com.mycompany.boniuk_math.com.itextpdf.text.*;
	import com.mycompany.boniuk_math.com.itextpdf.text.Font.FontFamily;
	import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfWriter;

	import java.util.Calendar;
        import java.text.SimpleDateFormat;

	/** Generates pdf with 56 2-digit subtraction problems
	 * @author VaniaSpeedy
	 * */
	public class Subtraction {
		final static int ARRAY_LENGTH = 56;//number of problems (number pairs)
            /** This is the main method, renamed to run for ease of use
     * @param page number
     * @return creates PDF files in same directory as script
     */
	    public void run(int addme) throws FileNotFoundException, DocumentException
                    //public static void main(String[] args) throws FileNotFoundException, DocumentException
	    {
	    //---------------------------------
	    	// Create new PDF document
	        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
	        // Create an output stream of PDF file.
	        String fileName = "Subtraction Worksheet__" + currentTime() + "p" + addme + ".pdf";
	        OutputStream out = new FileOutputStream(fileName);
	        // Get a PdfWriter instance to write in PDF document.
	        PdfWriter.getInstance(document, out);
	        // Open the PDF document
	        document.open();
	    //----------------------------------------

           //generate arrays (not function since can't return two arrays[])
	        int [] array = new int[ARRAY_LENGTH]; //
	        int [] array2 = new int[ARRAY_LENGTH]; //bottom numbers
	        for (int i = 0; i <ARRAY_LENGTH; i++)
	        {
	            int number = (int) (Math.random() * 89 + 11);
	            array[i] = number; //assign random to array1 value i

	            int number2 = (int) (Math.random() * 89 + 11);
	            array2[i] = number2; //assign random to array2 value i
	        }
            /* pre print
	        printArray(array);
	        System.out.println();
	        printArray(array2);
	        System.out.println();
	        System.out.println();
	        */

	        // check for validity, swap if necessary
	        for (int i=0; i < ((ARRAY_LENGTH)); i++)
	        if (array[i] < array2[i])
	        {
	        	int tmp = array[i];
	        	array[i] = array2[i];
	        	array2[i] = tmp;
	        }

	        /* afterprint
	        printArray(array);
	        System.out.println();
	        printArray(array2);
	        System.out.println();
	        System.out.println();
	        */
	        //---------------------------
	        //print header and padding of page
	        final int HEADER_FONT_SIZE = 22;
	      		Paragraph header = new Paragraph("   Two digit subtraction with some regrouping", new Font(FontFamily.HELVETICA, HEADER_FONT_SIZE, Font.BOLD, new BaseColor(0, 0, 0)) );
	            document.add(header);
	            Paragraph headerPadding = new Paragraph("     ", new Font(FontFamily.HELVETICA, HEADER_FONT_SIZE, Font.BOLD, new BaseColor(0, 0, 0)) );
	            document.add(headerPadding);

	      	//main loop - prints 7 rows
	            int counterTop = 0;//set counter, used to advance array[] by number of probs in a row
                int counterBottom =0;//set counter, used to advance array2 by number of problems
	            final int NUMBER_OF_ROWS=7;
                for (int h=0; h < NUMBER_OF_ROWS; h++)
	      		{
	      			final int FONT_SIZE = 20;
	      			//create top half of addition problems
	      			String topRow = "   " + array[h+counterTop] + "   " + array[h+1+counterTop] + "   " + array[h+2+counterTop] + "   " + array[h+3+counterTop] + "   " + array[h+4+counterTop] + "   " + array[h+5+counterTop] + "   " + array[h+6+counterTop] + "   " + array[h+7+counterTop];
	      	        //add top half to document
	      	        Paragraph topHalf = new Paragraph(topRow, new
	             		 Font(FontFamily.COURIER, FONT_SIZE, Font.NORMAL, new BaseColor(0, 0, 0)) );
	                document.add(topHalf);

	                // start creating bottom half
	                //add spacer to lead numbers
	                Chunk spacer = new Chunk ("  ", FontFactory.getFont(FontFactory.COURIER, FONT_SIZE, Font.NORMAL, new BaseColor(0, 0, 0)));
	                document.add(spacer);
	      			//add bottom half to document
	                final int NUMBER_OF_PROBLEMS = 8;

	      			for(int i=0; i<NUMBER_OF_PROBLEMS; i++) // run this 8 times, for 8 problems
	      			{
	      				String problem = "-" + array2[i+counterBottom];

	      				Chunk underline = new Chunk(problem, FontFactory.getFont(FontFactory.COURIER, FONT_SIZE, Font.NORMAL, new BaseColor(0, 0, 0)));
	      	        	// Set the underline of text
	      	        	underline.setUnderline(0.1f, -2f); //underline.setUnderline(0.1f, -2f);
	      	        	// Add the chunk element in document
	      	        	document.add(underline);
	      	        	//add space
	      	            Chunk space = new Chunk ("  ", FontFactory.getFont(FontFactory.COURIER, FONT_SIZE, Font.NORMAL, new BaseColor(0, 0, 0)));
	      	            document.add(space);
	      	     	}
	      			//increase counters for next set
	      			counterTop += 7;
	      			counterBottom +=8;

	      			//print empty line for answers
	                Paragraph answerSection = new Paragraph("  ", new Font(FontFamily.COURIER, 20, Font.NORMAL, new BaseColor(0, 0, 0)) );
	                document.add(answerSection);

	      		}//end main loop, each run is a row

	        // Close the document after use.
	      		String author = "VaniaSpeedy";
	      		String title = "Math Addition Worksheet";
	      		document.addAuthor(author);
	      		document.addCreationDate();
	      		document.addTitle(title);
	      		document.close();

	      	/*
	      	// DEBUG :: output to console if negative results
	      		for (int i=0;i<ARRAY_LENGTH;i++)
	      		{
	      			if (array[i]-array2[i]<0)
	      			{
	      				System.out.println("error for i="+i);
	      			}
	      		}
	      		*/

	    }//end main


	    /**Creates a string with the current time and date
	     * @return string with current date and time
	     * */
	    public static String currentTime() {
	    	//DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
	    	String DATE_FORMAT_NOW = "yyyy-MMM-dd__HH-mm-";
	        Calendar cal = Calendar.getInstance();
	        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
	        return sdf.format(cal.getTime());

	      }//end currentTime

	    /**Prints the array on one line
	     * @param array to be printed
	     * */
	    //used for debug
		public static void printArray(int[] array)
		{
			for (int i=0; i<ARRAY_LENGTH; i++)
				System.out.print(array[i] + " ");
	       	System.out.println();
		}//end printArray

	}



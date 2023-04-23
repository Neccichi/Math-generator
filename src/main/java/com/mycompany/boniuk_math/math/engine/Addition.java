/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.boniuk_math.math.engine;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
 
import com.mycompany.boniuk_math.com.itextpdf.text.*;
import com.mycompany.boniuk_math.com.itextpdf.text.Font.FontFamily;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfWriter;

import java.util.Calendar;
import java.text.SimpleDateFormat;

/** Generates pdf with 56 2-digit addition problems
 * @author VaniaSpeedy
 * */
public class Addition {
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
        String fileName = "Addition Worksheet__" + currentTime() + "p" + addme + ".pdf";
        OutputStream out = new FileOutputStream(fileName);
        // Get a PdfWriter instance to write in PDF document.
        PdfWriter.getInstance(document, out);
        // Open the PDF document
        document.open();
    //----------------------------------------


        int[] array = generateArray(); //generate array

        //print header and padding
      		Paragraph header = new Paragraph("    Two digit addition with some regrouping", new Font(FontFamily.HELVETICA, 24, Font.BOLD, new BaseColor(0, 0, 0)) );
            document.add(header);
            Paragraph headerPadding = new Paragraph("     ", new Font(FontFamily.HELVETICA, 24, Font.BOLD, new BaseColor(0, 0, 0)) );
            document.add(headerPadding);

      	//main loop - prints 8 rows

            int counterTop = 0;//set counter, used to advance array[] by 7
      		for (int h=0; h < 7; h++)
      		{
      			final int FONT_SIZE = 20;
      			//create top half of addition problems
      			String topRow = "   " + array[h+counterTop] + "   " + array[h+1+counterTop] + "   " + array[h+2+counterTop] + "   " + array[h+3+counterTop] + "   " + array[h+4+counterTop] + "   " + array[h+5+counterTop] + "   " + array[h+6+counterTop] + "   " + array[h+7+counterTop];

      	        //add top half to document
      	        Paragraph topHalf = new Paragraph(topRow, new
             		 Font(FontFamily.COURIER, FONT_SIZE, Font.NORMAL, new BaseColor(0, 0, 0)) );
                document.add(topHalf);

      			//add bottom half to document
                //add spacer preceding line
                Chunk spacer = new Chunk ("  ", FontFactory.getFont(FontFactory.COURIER, FONT_SIZE, Font.NORMAL, new BaseColor(0, 0, 0)));
                document.add(spacer);

      			for(int i=0; i<8; i++) // run this 8 times, for 8 problems
      			{
      				String problem = "+" + array[127-i-counterTop];
      				Chunk underline = new Chunk(problem, FontFactory.getFont(FontFactory.COURIER, FONT_SIZE, Font.NORMAL, new BaseColor(0, 0, 0)));
      	        	// Set the underline of text
      	        	underline.setUnderline(0.1f, -2f); //underline.setUnderline(0.1f, -2f);
      	        	// Add the chunk element in document
      	        	document.add(underline);

      	        	//add space
      	            Chunk space = new Chunk ("  ", FontFactory.getFont(FontFactory.COURIER, FONT_SIZE, Font.NORMAL, new BaseColor(0, 0, 0)));
      	            document.add(space);
      	     	}

      			//print empty line for answers
                Paragraph answerSection = new Paragraph("  ", new Font(FontFamily.COURIER, 20, Font.NORMAL, new BaseColor(0, 0, 0)) );
                document.add(answerSection);

      			//increase counter for next set
      			counterTop += 7;
      		}

        // Close the document after use.
      		String author = "VaniaSpeedy";
      		String title = "Math Addition Worksheet";
      		document.addAuthor(author);
      		document.addCreationDate();
      		document.addTitle(title);
      		document.close();

    }//end main

    public static int[] generateArray()
    {
        int [] randomArray = new int[128]; //
        for (int i = 0; i <128; i++)
        {
            int number = (int) (Math.random() * 89 + 11);
            randomArray[i] = number; //assign random to array value i
        }
        return randomArray;
    }// end generateArray

    public static String currentTime() {
    	//DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
    	String DATE_FORMAT_NOW = "yyyy-MMM-dd__HH-mm-";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());

      }//end currentTime
}

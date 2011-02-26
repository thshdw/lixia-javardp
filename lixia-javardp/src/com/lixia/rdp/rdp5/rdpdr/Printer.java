package com.lixia.rdp.rdp5.rdpdr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.HashAttributeSet;
import javax.print.attribute.standard.PrinterName;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;

import com.lixia.rdp.RdpPacket;
import com.lixia.rdp.RdpPacket_Localised;

public class Printer extends RdpdrDevice {

	File  printFile=null;
	PrintService printService = null;
	
	public Printer() {
		super(Rdpdr.RDPDR_DTYP_PRINT);
	}

	@Override
	public void register(String optarg, int port) {
		this.name = "PRN"+port;
		String driver = "HP Color LaserJet 8500 PS";//"MS Publisher Imagesetter";
		String printerName = "mydeskjet";
		int flags = 0;
		if (port == 1)
			flags |= Rdpdr.RDPDR_PRINTER_ANNOUNCE_FLAG_DEFAULTPRINTER;
		
		String cache_data = null;
		int cache_data_len = 0;
		int size = 24  + (printerName.length() + 1) * 2 + (driver.length() + 1) * 2 + cache_data_len;
		deviceData = new RdpPacket_Localised(size);
		deviceData.setLittleEndian32(flags);
		deviceData.setLittleEndian32(0); /* CodePage, reserved */
		deviceData.setLittleEndian32(0); /* PnPNameLen */
		byte[] driver_bytes = null;
		try {
			driver_bytes = driver.getBytes("UTF-16LE");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if(driver_bytes!=null)
			deviceData.setLittleEndian32(driver_bytes.length+2);/* DriverNameLen */
		byte[] name_bytes = null;
		try {
			name_bytes = printerName.getBytes("UTF-16LE");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if(name_bytes!=null)
			deviceData.setLittleEndian32(name_bytes.length+2);/* PrintNameLen */

		deviceData.setLittleEndian32(cache_data_len); /* CachedFieldsLen */
		
		if(driver_bytes!=null){
			deviceData.copyFromByteArray(driver_bytes, 0, deviceData.getPosition(), driver_bytes.length);
			deviceData.incrementPosition(driver_bytes.length);
			deviceData.setLittleEndian16(0);
		}
		if(name_bytes!=null){
			deviceData.copyFromByteArray(name_bytes, 0, deviceData.getPosition(), name_bytes.length);
			deviceData.incrementPosition(name_bytes.length);
			deviceData.setLittleEndian16(0);
		}
	}

	@Override
	public void create(String filename) {
		// find the printing service
		//http://www.coderanch.com/t/385989/java/java/send-raw-data-printer
		AttributeSet attributeSet = new HashAttributeSet();  
		attributeSet.add(new PrinterName(filename, null));
		PrintService[] services = PrintServiceLookup.lookupPrintServices(null, attributeSet);
		if(services.length > 0){
			if(printFile != null){
				printFile.deleteOnExit();
				printFile = null;
			}
			try {
				printFile = File.createTempFile("printer_"+name,".redirect");
			} catch (IOException e) {
				e.printStackTrace();
			}
			printService = services[0];
		}
		
	}

	@Override
	public void write(RdpPacket data, int length, int offset) {
		byte[] outbytes = new byte[length];
		data.copyToByteArray(outbytes, 0, data.getPosition(), length);
		FileOutputStream output;
		try {
			output = new FileOutputStream(printFile,true);
			output.write(outbytes);
			output.close();	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		InputStream in = null;
		try{
			if(printService == null){
				return;
			}
			if(printFile == null){
				return;
			}
			DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
			in = new FileInputStream(printFile);
			
			DocPrintJob job = printService.createPrintJob();   
			Doc doc = new SimpleDoc(in, flavor, null);   
			  
			// monitor print job events   
			//log.debug("preparing print job monitor");   
			PrintJobWatcher watcher = new PrintJobWatcher(job);   
			  
			// print it   
			//log.debug("start printing");   
			job.print(doc, null);
			  
			// wait for the print job is done   
			//log.debug("waiting for the printing to finish");   
			watcher.waitForDone();   
			  
			//log.debug("done !");   

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (PrintException e) {
			e.printStackTrace();
		}finally{
			if (in != null) try { in.close(); } catch(Exception e) {}
			if(printFile != null){
				printFile.deleteOnExit();
				printFile = null;
			}
			printService = null;
		}
	}
	class PrintJobWatcher {
	    // true iff it is safe to close the print job's input stream
	    boolean done = false;

	    PrintJobWatcher(DocPrintJob job) {
	        // Add a listener to the print job
	        job.addPrintJobListener(new PrintJobAdapter() {
	            public void printJobCanceled(PrintJobEvent pje) {
	                allDone();
	            }
	            public void printJobCompleted(PrintJobEvent pje) {
	                allDone();
	            }
	            public void printJobFailed(PrintJobEvent pje) {
	                allDone();
	            }
	            public void printJobNoMoreEvents(PrintJobEvent pje) {
	                allDone();
	            }
	            void allDone() {
	                synchronized (PrintJobWatcher.this) {
	                    done = true;
	                    PrintJobWatcher.this.notify();
	                }
	            }
	        });
	    }
	    public synchronized void waitForDone() {
	        try {
	            while (!done) {
	                wait();
	            }
	        } catch (InterruptedException e) {
	        }
	    }
	}

}

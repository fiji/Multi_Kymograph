/*-
 * #%L
 * Measure velocities of moving structures in an image time series.
 * %%
 * Copyright (C) 2004 - 2017 Fiji development team
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

package sc.fiji.multiKymograph;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.Macro;
import ij.WindowManager;
import ij.plugin.PlugIn;

import java.awt.image.ColorModel;

/**
 * Performs a walking average on a stack.
 * Please sent comments to: Arne Seitz (seitz@embl.de)
 */
public class WalkingAverage_ implements PlugIn {
	
	final String makroString=Macro.getOptions();
	
	public void run(String arg) 
	{
       	ImagePlus imp = WindowManager.getCurrentImage();
        
        if(imp==null){
        	IJ.noImage();
           	return ;
        }
		IJ.run("8-bit");
		int numStacks = imp.getStackSize();
		int average=1;
		
		if(numStacks==1) {
        	IJ.error("Must call this plugin on image stack.");
        	return;
        }
        if (makroString!=null){
        	int start=makroString.indexOf("average=");
        	int end=makroString.length()-1;
        	average=Integer.parseInt(makroString.substring(start+8,end));
        }
       
        if (makroString==null){
        	String sPrompt = "Number of frames to average ";
        	average = (int)IJ.getNumber(sPrompt,4);
        }
       
        if(average==IJ.CANCELED) return;
        if(average >=numStacks) {
			IJ.error("Sorry. This is not possible");
			return;
		}
						
            		
        if(!imp.lock())
        return;    // exit if in use
            
        
        walkav(imp,numStacks,average);
            
        imp.unlock();
            
    	}

    
	protected void walkav(ImagePlus imp, int numImages, int numSubStacks) 
	{
        		
        ImageStack stack = imp.getStack();
        
        ColorModel cm = imp.createLut().getColorModel();
                                    
        ImageStack ims = new ImageStack(stack.getWidth(), stack.getHeight(), cm);
     	String sStackName = "walkAv";
                    		
		byte[] pixels;
		int dimension = stack.getWidth()*stack.getHeight();
		int [] sum = new int [dimension];
		int stop =stack.getSize();
		
        for (int s=0; s<=stop-numSubStacks;s++){
			
			for (int j=0; j<dimension;j++) {
				sum [j]=0;
			}
			
			for (int i=1+s; i<=numSubStacks+s; i++) {
				pixels = (byte[]) stack.getPixels(i);
								
				for (int j=0; j<dimension;j++) {
					sum [j]+=0xff & pixels[j];
				}
			}
			byte [] average = new byte [dimension];
		
			for (int j=0; j<dimension;j++) {
				average[j] = (byte) ((sum[j]/numSubStacks) & 0xff);
			}
			ims.addSlice("RollAverage"+s,average);
		
		}
		
		ImagePlus nimp = new ImagePlus(sStackName,ims); 
		nimp.setStack(sStackName,ims);
        nimp.show();			

	}  
}


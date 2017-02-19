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
import ij.WindowManager;
import ij.gui.NewImage;
import ij.plugin.PlugIn;
import ij.process.Blitter;
import ij.process.ImageProcessor;

/** Calculates Difference Image on a 8-Bit Image Stack
      please send comments to seitz@embl.de
*/

public class StackDifference_ implements PlugIn {
	double [] profile;
	
	
	public void run(String arg) 
	{
		ImagePlus imp = WindowManager.getCurrentImage();

        if(imp==null){ 
        	IJ.noImage();
            return ;
        }
		int numStacks = imp.getStackSize();
        if(numStacks==1){
        	IJ.error("Must call this plugin on image stack.");
            return;
        }
		
		int bd=imp.getBitDepth();
		if (bd !=8 || numStacks==1){
			IJ.error("Sorry! This Plugin only works on 8-Bit Image Stacks");
			return;
		}
		
		String sPrompt = "Gap between frames ";
        int numDiff = (int)IJ.getNumber(sPrompt,1);
       
        if(numDiff==IJ.CANCELED) return;
		if(numDiff>=numStacks){
			
			IJ.error("Sorry, this makes no sense");
			return;
		}
            
            		
        if(!imp.lock())return;    // exit if in use
        
		ImageProcessor ip = imp.getProcessor();
		
		int w=ip.getWidth();
		int h=ip.getHeight();
		
		ImagePlus overlay = NewImage.createByteImage("Difference Image", w,h,numStacks-numDiff, NewImage.FILL_BLACK);
		ImageProcessor over_ip = overlay.getProcessor();
		for (int i =1;i<=numStacks-numDiff;i++){
		
			imp.setSlice(i);
			imp.getProcessor();
			overlay.setSlice(i);
			over_ip.copyBits(ip,0,0,Blitter.COPY);
			imp.setSlice(i+numDiff);
			over_ip = overlay.getProcessor();
			overlay.setSlice(i);
			over_ip.copyBits(ip,0,0,Blitter.DIFFERENCE);
			
			
		}
		overlay.show();
		
		imp.unlock();
	}

            	
}

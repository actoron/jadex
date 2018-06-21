/*

JAdhoc ver 0.2 - Java AODV (RFC 3561) Protocol Handler
Copyright 2003-2004 ComNets, University of Bremen

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

*/

package jadex.android.bluetooth.routing.dsdv.info;


/**
* This class provide common information for the current
* invocation of the Protocol Handler.
*
* @author : Asanga Udugama
* @date : 28-jul-2003
* @email : adu@comnets.uni-bremen.de
*
*/
public class CurrentInfo {
	public static int lastSeqNum;

	public static final int GREATER = 1;
	public static final int EQUAL =	0;
	public static final int LESS = (-1);


	/**
	* Constructor that creates a object and initializes
	* the current variables
	*/
	public CurrentInfo() {
		lastSeqNum = 0;
	}
        
        public static void setOwnSeqNum(int num) {
            lastSeqNum = num;
        }

	/**
	* Method to increment the local Sequence Number
	* TODO : code for number rollover
	*
	* @return int - the incremented sequence number
	*/
	public static int incrementOwnSeqNum() {
            lastSeqNum = lastSeqNum+2;  //DSDV increments on whole numbers
            return lastSeqNum;
	}

	/**
	* Method to compare Destination Sequence Numbers. Returns
	* 1, 0 or -1 based on whether first number is greater, equal
	* or less than the second number, respectively.
	*
	* TODO : code for number rollover
	*
	* @param int firstNum - 1st number to compare
	* @param int secondNum - 2nd number to compare
	* @return int - 1, 0 or -1 based on greater, equal or less
	*/
	public static int destSeqCompare(int firstNum, int secondNum) {
		if(firstNum > secondNum)
			return GREATER;
		else if(firstNum < secondNum)
			return LESS;
		else
			return EQUAL;
	}
}

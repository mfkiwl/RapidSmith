/*
 * Copyright (c) 2010 Brigham Young University
 * 
 * This file is part of the BYU RapidSmith Tools.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License..
 * 
 */
package edu.byu.ece.rapidSmith.device.helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.regex.Pattern;

import edu.byu.ece.rapidSmith.device.WireDirection;
import edu.byu.ece.rapidSmith.device.WireType;

/**
 * This is a helper class to WireEnumerator to find wireType and wireDirection information.
 * @author Chris Lavin
 * Created on: May 4, 2010
 */
public class WireExpressions implements Serializable {

	private static final long serialVersionUID = -600702803514300547L;
	// Regular Expressions
	private Pattern north;
	private Pattern south;
	private Pattern east;
	private Pattern west;
	private Pattern horizontal;
	private Pattern vertical;
	private Pattern eastSouth;
	private Pattern eastNorth;
	private Pattern northEast;
	private Pattern northWest;
	private Pattern southEast;
	private Pattern southWest;
	private Pattern westNorth;
	private Pattern westSouth;
	
	private Pattern omux;
	private Pattern doubleType;
	private Pattern hex;
	private Pattern longType;
	private Pattern clk;
	private Pattern bounce;
	private Pattern omux_output;
	private Pattern to_bufg;
	private Pattern pent;
	private Pattern double_turn;
	private Pattern pent_turn;
	private Pattern int_sink;
	private Pattern int_source;
	private Pattern int_conn;
	private Pattern triple;
	private Pattern hept;
	private Pattern triple_turn;
	private Pattern hept_turn;	
	
	public WireExpressions(){
		// These patterns are mostly compatible for the Virtex 4 and 5
		north = Pattern.compile("(N[26]BEG[0-9]|N[LR][25]BEG(0|1|2|_S0|_N2))");
		south = Pattern.compile("(S[26]BEG[0-9]|S[LR][25]BEG(0|1|2|_S2))");
		east = Pattern.compile("(E[26]BEG[0-9]|E[LR][25]BEG(0|1|2))");
		west = Pattern.compile("(W[26]BEG[0-9]|WL2BEG(0|1|2|_S0)|WL5BEG(1|2)|WR2BEG(0|1)|WR5BEG(0|1|2))");
		horizontal = Pattern.compile("LH(0|6|8|12|16|18|24)");
		vertical = Pattern.compile("LV(0|6|8|12|16|18|24)");
		eastSouth = Pattern.compile("(ES[25]BEG(0|1|2))");
		eastNorth = Pattern.compile("EN[25]BEG(0|1|2)");
		northEast = Pattern.compile("(NE[25]BEG(0|1|2|_N2)|ER2BEG_S0)");
		northWest = Pattern.compile("(NW[25]BEG(0|1|2|_N2)|WL5BEG_S0)");
		southEast = Pattern.compile("SE[25]BEG(0|1|2)");
		southWest = Pattern.compile("(SW[25]BEG(0|1|2)|WR2BEG_N2)");
		westNorth = Pattern.compile("(WN[25]BEG(0|1|2|_S0))");
		westSouth = Pattern.compile("WS[25]BEG(0|1|2|_S0)");
		
		omux = Pattern.compile("OMUX[0-9]+");
		doubleType = Pattern.compile("([NSEW]2(BEG|MID|END)[0-9]|(EL|NR|SR|WL)2(BEG|MID|END)[0-2]|(ER|NL|NR|SL|WR)2(BEG|MID|END)[1-2])");
		hex = Pattern.compile("[NSEW]6(BEG|MID|END)[0-9]");
		longType = Pattern.compile("L[HV](0|6|8|12|16|18|24)");
		clk = Pattern.compile("(.*CLK.*|.*CMT.*)");
		bounce = Pattern.compile(".*BOUNCE.*");
		omux_output = Pattern.compile("OMUX_[NSEW][NSEW]*[0-9]+");
		to_bufg = Pattern.compile(".*TO_BUFG.*");
		pent = Pattern.compile("((EL|ER|NL|SL|SR|WR)5(BEG|MID|END)[0-2]|(NR|WL)5(BEG|MID|END)(1|2|_N2))");
		double_turn = Pattern.compile("((ES|EN|NE|NW|SE|SW|WN|WS)2(BEG|MID|END)(0|1|2|_N2|S0)|(ER|NL|WL)2BEG_S0|(NL|ER|SL)2(MID|END)0|(NR|SL|WR)2BEG_N2)");
		pent_turn = Pattern.compile("((ES|EN|NE|NW|SE|SW|WN|WS)2(BEG|MID|END)(0|1|2|_N2|S0)|WL5BEG_S0|WL5MID0|WL5END0)");
		int_sink = Pattern.compile(".*_B[0-9]+$");
		int_source = Pattern.compile(".*_(OUTS|BOT|TOP)[0-9]+$");
		int_conn = Pattern.compile(".*_INT[0-9]*$");
		triple = Pattern.compile("[NS]2END_[NS][0-9]");
		hept = Pattern.compile("[NS]6END_[NS][0-9]");
		triple_turn = Pattern.compile("[EW]2END_[NS][0-9]");
		hept_turn = Pattern.compile("[EW]6END_[NS][0-9]");

	}
	
	public WireType getWireType(String s){
		if(Pattern.matches(omux.pattern(), s)){
			return WireType.OMUX;
		}else if(Pattern.matches(doubleType.pattern(), s)){
			return WireType.DOUBLE;
		}else if(Pattern.matches(hex.pattern(), s)){
			return WireType.HEX;
		}else if(Pattern.matches(longType.pattern(), s)){
			return WireType.LONG;
		}else if(Pattern.matches(bounce.pattern(), s)){
			return WireType.BOUNCE;
		}else if(Pattern.matches(omux_output.pattern(), s)){
			return WireType.OMUX_OUTPUT;
		}else if(Pattern.matches(to_bufg.pattern(), s)){
			return WireType.TO_BUFG;
		}else if(Pattern.matches(pent.pattern(), s)){
			return WireType.PENT;
		}else if(Pattern.matches(double_turn.pattern(), s)){
			return WireType.DOUBLE_TURN;
		}else if(Pattern.matches(pent_turn.pattern(), s)){
			return WireType.PENT_TURN;
		}else if(Pattern.matches(int_sink.pattern(), s)){
			return WireType.INT_SINK;
		}else if(Pattern.matches(int_source.pattern(), s)){
			return WireType.INT_SOURCE;
		}else if(Pattern.matches(int_conn.pattern(), s)){
			return WireType.INT_CONN;	
		}else if(Pattern.matches(triple.pattern(), s)){
			return WireType.TRIPLE;
		}else if(Pattern.matches(hept.pattern(), s)){
			return WireType.HEPT;
		}else if(Pattern.matches(triple_turn.pattern(), s)){
			return WireType.TRIPLE_TURN;
		}else if(Pattern.matches(hept_turn.pattern(), s)){
			return WireType.HEPT_TURN;
		}
		return WireType.OTHER;
	}
	
	public WireDirection getWireDirection(String s){
		if(Pattern.matches(clk.pattern(), s)){
			return WireDirection.CLK;
		}else if(Pattern.matches(north.pattern(), s)){
			return WireDirection.NORTH;
		}else if(Pattern.matches(south.pattern(), s)){
			return WireDirection.SOUTH;
		}else if(Pattern.matches(east.pattern(), s)){
			return WireDirection.EAST;
		}else if(Pattern.matches(west.pattern(), s)){
			return WireDirection.WEST;
		}else if(Pattern.matches(horizontal.pattern(), s)){
			return WireDirection.HORIZONTAL;
		}else if(Pattern.matches(vertical.pattern(), s)){
			return WireDirection.VERTICAL;
		}else if(Pattern.matches(eastSouth.pattern(), s)){
			return WireDirection.EASTSOUTH;
		}else if(Pattern.matches(eastNorth.pattern(), s)){
			return WireDirection.EASTNORTH;
		}else if(Pattern.matches(northEast.pattern(), s)){
			return WireDirection.NORTHEAST;
		}else if(Pattern.matches(northWest.pattern(), s)){
			return WireDirection.NORTHWEST;
		}else if(Pattern.matches(southEast.pattern(), s)){
			return WireDirection.SOUTHEAST;
		}else if(Pattern.matches(southWest.pattern(), s)){
			return WireDirection.SOUTHWEST;
		}else if(Pattern.matches(westNorth.pattern(), s)){
			return WireDirection.WESTNORTH;
		}else if(Pattern.matches(westSouth.pattern(), s)){
			return WireDirection.WESTSOUTH;
		}
		return WireDirection.NONE;
	}
	
	public static void main(String[] args){
		if(args.length != 2){
			System.out.println("USAGE: <wireNames.txt> <output.txt>");
		}
		WireExpressions w = new WireExpressions();
		try {
			BufferedReader br = new BufferedReader(new FileReader(args[0]));
			BufferedWriter wr = new BufferedWriter(new FileWriter(args[1]));
			String line = null;
			while((line = br.readLine()) != null){
				line = line.trim();
				wr.write(line + " " + w.getWireType(line).name() + " " + w.getWireDirection(line).name() + "\n");
			}
			br.close();
			wr.close();
		} catch(FileNotFoundException e){
			e.printStackTrace();
		} catch(IOException e){
			e.printStackTrace();
		}
	}
}

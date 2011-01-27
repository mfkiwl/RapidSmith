/*
 * Copyright (c) 2010 Brigham Young University
 * 
 * This file is part of the BYU RapidSmith Tools.
 * 
 * BYU RapidSmith Tools is free software: you may redistribute it 
 * and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 2 of 
 * the License, or (at your option) any later version.
 * 
 * BYU RapidSmith Tools is distributed in the hope that it will be 
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * A copy of the GNU General Public License is included with the BYU 
 * RapidSmith Tools. It can be found at doc/gpl2.txt. You may also 
 * get a copy of the license at <http://www.gnu.org/licenses/>.
 * 
 */
package edu.byu.ece.rapidSmith.device;

import java.io.Serializable;
import java.util.HashMap;

import edu.byu.ece.rapidSmith.util.FamilyType;
import edu.byu.ece.rapidSmith.util.PartNameTools;

/**
 * This class represents the primitive sites found in XDLRC files.  Theses
 * represent places on the FPGA where a particular instance of a primitive (or
 * XDL 'inst') can reside.
 * @author Chris Lavin
 */
public class PrimitiveSite implements Serializable{

	private static final long serialVersionUID = 4891980590392076374L;
	/** Name of the primitive site with X and Y coordinates (ie. SLICE_X0Y0) */
	protected String name;
	/** The primitive type of the site */
	protected PrimitiveType type;
	/** The tile where this site resides */
	protected Tile tile;
	/** Keeps track of all the in/out pins in the primitive with their wire enumeration value */
	protected HashMap<String,Integer> pins;
	/** Keeps track of extra site types on which primitive types can be placed */
	@SuppressWarnings("unchecked")
	public static HashMap<PrimitiveType, PrimitiveType[]>[] compatibleTypesArray = new HashMap[FamilyType.values().length];
	
	/**
	 * Constructor for a new PrimitiveSite
	 */
	public PrimitiveSite(){
		name = null;
		tile = null;
		pins = new HashMap<String,Integer>();
	}
	
	/**
	 * Gets and returns the name of this primitive site (ex: SLICE_X4Y6).
	 * @return The unique name of this primitive site.
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Sets the name of this primitive site (ex: SLICE_X5Y7).
	 * @param name the name to set.
	 */
	public void setName(String name){
		this.name = name;
	}
	
	/**
	 * Gets and returns the Tile where this primitive site resides.
	 * @return The tile where this primitive site is.
	 */
	public Tile getTile(){
		return tile;
	}
	
	/**
	 * Sets the tile where this primitive site is.
	 * @param location the tile location to set.
	 */
	public void setTile(Tile location){
		this.tile = location;
	}
	
	/**
	 * Gets the input/output pins of this primitive site and
	 * the internal to external name mappings.
	 * @return the pins of this primitive site.
	 */
	public HashMap<String, Integer> getPins(){
		return pins;
	}
	
	/**
	 * Sets the input/output pins of this primitive site and
	 * the internal to external name mappings. 
	 * @param pins The mapping of input/output pins from internal to 
	 * external names.
	 */
	public void setPins(HashMap<String, Integer> pins) {
		this.pins = pins;
	} 
	
	/**
	 * Adds a pin mapping to the primitive site.
	 * @param internalPinName The name of the wire internal to the 
	 * primitive site.
	 * @param externalPin The external wire integer of the mapping.
	 */
	public void addPin(String internalPinName, Integer externalPin){
		this.pins.put(internalPinName, externalPin);
	}
	
	/**
	 * Gets the external wire enumeration of the name of the wire corresponding to the internal wire
	 * name.
	 * @param internalName The internal wire name in the primitive.
	 * @return The corresponding external wire enum (Integer) name of the internal wire name. 
	 */
	public Integer getExternalPinName(String internalName){
		return this.pins.get(internalName);
	}
	
	/**
	 * Sets the native type of primitive of this primitive site.
	 * @param type the type to set.
	 */
	public void setType(PrimitiveType type){
		this.type = type;
	}
	/**
	 * Gets and returns the native type of primitive of this primitive site.
	 * @return the type
	 */
	public PrimitiveType getType(){
		return type;
	}

	/**
	 * This method will check if the PrimitiveType otherType can be placed
	 * at this primitive site.  Most often only if they are
	 * equal can this be true.  However there are a few special cases that require
	 * extra handling.  For example a SLICEL can reside in a SLICEM site but not 
	 * vice versa.  
	 * @param otherType The primitive type to try to place on this site.
	 * @return True if otherType can be placed at this primitive site, false otherwise.
	 */
	public boolean isCompatiblePrimitiveType(PrimitiveType otherType){
		// All primitive types can reside in a site of their own type
		if(type.equals(otherType)){
			return true;
		}
		// If its not an exact match, lets check if this site can accommodate
		// the otherType 
		FamilyType baseFamilyType = tile.getDevice().getFamilyType();
		PrimitiveType[] compatibleTypes = compatibleTypesArray[baseFamilyType.ordinal()].get(otherType);
		if(compatibleTypes == null){
			return false;
		}
		// Check if this site is in the compatible type list for otherType
		// (NOTE: These arrays are generally very short, so hopefully this will
		// not be a bottleneck)
		for(PrimitiveType compatibleType : compatibleTypes){
			if(compatibleType.equals(this.type)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This method gets the type of otherSite and calls the other method
	 * public boolean isCompatiblePrimitiveType(PrimitiveType otherType);
	 * See that method for more information.
	 * @param otherSite The other site to see if its type is compatible with this site.
	 * @return True if compatible, false otherwise.
	 */
	public boolean isCompatiblePrimitiveType(PrimitiveSite otherSite){
		return isCompatiblePrimitiveType(otherSite.type);
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public int hashCode(){
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj){
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		PrimitiveSite other = (PrimitiveSite) obj;
		if(name == null){
			if(other.name != null)
				return false;
		}
		else if(!name.equals(other.name))
			return false;
		if(type == null){
			if(other.type != null)
				return false;
		}
		else if(!type.equals(other.type))
			return false;
		return true;
	}
	
	// Static initialization stuff
	static{
		HashMap<PrimitiveType, PrimitiveType[]> compatibleSites;
		for (FamilyType specificFamilyType : FamilyType.values()){
			compatibleSites = new HashMap<PrimitiveType, PrimitiveType[]>();
			switch (PartNameTools.getBaseTypeFromFamilyType(specificFamilyType)){
				case SPARTAN2:
					compatibleSites.put(PrimitiveType.IOB,
							new PrimitiveType[]{PrimitiveType.PCIIOB});
					break;
				case SPARTAN2E:
					compatibleSites.put(PrimitiveType.IOB,
							new PrimitiveType[]{PrimitiveType.DLLIOB, PrimitiveType.PCIIOB});
					break;
				case SPARTAN3:
					compatibleSites.put(PrimitiveType.IOB,
							new PrimitiveType[]{PrimitiveType.DIFFM, PrimitiveType.DIFFS});
					compatibleSites.put(PrimitiveType.SLICEL,
							new PrimitiveType[]{PrimitiveType.SLICEM});
					break;
				case SPARTAN3A:
					compatibleSites.put(PrimitiveType.DIFFM,
							new PrimitiveType[]{PrimitiveType.DIFFMLR, PrimitiveType.DIFFMTB});
					compatibleSites.put(PrimitiveType.DIFFMI,
							new PrimitiveType[]{PrimitiveType.DIFFMLR, PrimitiveType.DIFFMTB});
					compatibleSites.put(PrimitiveType.DIFFMI_NDT,
							new PrimitiveType[]{PrimitiveType.DIFFMLR, PrimitiveType.DIFFMTB});
					compatibleSites.put(PrimitiveType.DIFFS,
							new PrimitiveType[]{PrimitiveType.DIFFSLR, PrimitiveType.DIFFSTB});
					compatibleSites.put(PrimitiveType.DIFFSI,
							new PrimitiveType[]{PrimitiveType.DIFFSLR, PrimitiveType.DIFFSTB});
					compatibleSites.put(PrimitiveType.DIFFSI_NDT,
							new PrimitiveType[]{PrimitiveType.DIFFSLR, PrimitiveType.DIFFSTB});		
					compatibleSites.put(PrimitiveType.IBUF,
							new PrimitiveType[]{PrimitiveType.DIFFMLR, PrimitiveType.DIFFMTB, PrimitiveType.DIFFSLR, PrimitiveType.DIFFSTB, PrimitiveType.DIFFSI_NDT, PrimitiveType.DIFFMI_NDT});
					compatibleSites.put(PrimitiveType.IOB,
							new PrimitiveType[]{PrimitiveType.IBUF, PrimitiveType.DIFFMLR, PrimitiveType.DIFFMTB, PrimitiveType.DIFFSLR, PrimitiveType.DIFFSTB});
					compatibleSites.put(PrimitiveType.IOBLR,
							new PrimitiveType[]{PrimitiveType.DIFFSLR, PrimitiveType.DIFFMLR});
					compatibleSites.put(PrimitiveType.SLICEL,
							new PrimitiveType[]{PrimitiveType.SLICEM});
					break;
				case SPARTAN3ADSP:
					compatibleSites.put(PrimitiveType.DIFFM,
							new PrimitiveType[]{PrimitiveType.DIFFMLR, PrimitiveType.DIFFMTB});
					compatibleSites.put(PrimitiveType.DIFFMI,
							new PrimitiveType[]{PrimitiveType.DIFFMLR, PrimitiveType.DIFFMTB});
					compatibleSites.put(PrimitiveType.DIFFMI_NDT,
							new PrimitiveType[]{PrimitiveType.DIFFMLR, PrimitiveType.DIFFMTB});
					compatibleSites.put(PrimitiveType.DIFFS,
							new PrimitiveType[]{PrimitiveType.DIFFSLR, PrimitiveType.DIFFSTB});
					compatibleSites.put(PrimitiveType.DIFFSI,
							new PrimitiveType[]{PrimitiveType.DIFFSLR, PrimitiveType.DIFFSTB});
					compatibleSites.put(PrimitiveType.DIFFSI_NDT,
							new PrimitiveType[]{PrimitiveType.DIFFSLR, PrimitiveType.DIFFSTB});
					compatibleSites.put(PrimitiveType.IBUF,
							new PrimitiveType[]{PrimitiveType.DIFFMI_NDT, PrimitiveType.DIFFSI_NDT, PrimitiveType.DIFFMLR, PrimitiveType.DIFFMTB, PrimitiveType.DIFFSLR, PrimitiveType.DIFFSTB});
					compatibleSites.put(PrimitiveType.IOB,
							new PrimitiveType[]{PrimitiveType.IBUF, PrimitiveType.DIFFMLR, PrimitiveType.DIFFMTB, PrimitiveType.DIFFSLR, PrimitiveType.DIFFSTB});
					compatibleSites.put(PrimitiveType.IOBLR,
							new PrimitiveType[]{PrimitiveType.IOBLR, PrimitiveType.DIFFMLR, PrimitiveType.DIFFSLR});
					compatibleSites.put(PrimitiveType.SLICEL,
							new PrimitiveType[]{PrimitiveType.SLICEM});
					break;
				case SPARTAN3E:
					compatibleSites.put(PrimitiveType.DIFFMI,
							new PrimitiveType[]{PrimitiveType.DIFFM});
					compatibleSites.put(PrimitiveType.DIFFSI,
							new PrimitiveType[]{PrimitiveType.DIFFS});
					compatibleSites.put(PrimitiveType.IBUF,
							new PrimitiveType[]{PrimitiveType.IOB, PrimitiveType.DIFFM, PrimitiveType.DIFFMI, PrimitiveType.DIFFS, PrimitiveType.DIFFSI});
					compatibleSites.put(PrimitiveType.IOB,
							new PrimitiveType[]{PrimitiveType.DIFFM, PrimitiveType.DIFFS});
					compatibleSites.put(PrimitiveType.SLICEL,
							new PrimitiveType[]{PrimitiveType.SLICEM});
					break;
				case SPARTAN6:
					compatibleSites.put(PrimitiveType.BUFG,
							new PrimitiveType[]{PrimitiveType.BUFGMUX});
					compatibleSites.put(PrimitiveType.BUFIO2FB_2CLK,
							new PrimitiveType[]{PrimitiveType.BUFIO2FB});
					compatibleSites.put(PrimitiveType.BUFIO2_2CLK,
							new PrimitiveType[]{PrimitiveType.BUFIO2});
					compatibleSites.put(PrimitiveType.DCM_CLKGEN,
							new PrimitiveType[]{PrimitiveType.DCM});
					compatibleSites.put(PrimitiveType.IOB,
							new PrimitiveType[]{PrimitiveType.IOBM, PrimitiveType.IOBS});
					compatibleSites.put(PrimitiveType.IODRP2,
							new PrimitiveType[]{PrimitiveType.IODELAY2});
					compatibleSites.put(PrimitiveType.IODRP2_MCB,
							new PrimitiveType[]{PrimitiveType.IODELAY2});
					compatibleSites.put(PrimitiveType.ISERDES2,
							new PrimitiveType[]{PrimitiveType.ILOGIC2});
					compatibleSites.put(PrimitiveType.OSERDES2,
							new PrimitiveType[]{PrimitiveType.OLOGIC2});
					compatibleSites.put(PrimitiveType.SLICEL,
							new PrimitiveType[]{PrimitiveType.SLICEM});
					compatibleSites.put(PrimitiveType.SLICEX,
							new PrimitiveType[]{PrimitiveType.SLICEL, PrimitiveType.SLICEM});
					break;
				case VIRTEX:
					compatibleSites.put(PrimitiveType.IOB,
							new PrimitiveType[]{PrimitiveType.PCIIOB});
					break;
				case VIRTEX2:
					compatibleSites.put(PrimitiveType.IOB,
							new PrimitiveType[]{PrimitiveType.DIFFM, PrimitiveType.DIFFS});
					break;
				case VIRTEX2P:
					compatibleSites.put(PrimitiveType.IOB,
							new PrimitiveType[]{PrimitiveType.DIFFM, PrimitiveType.DIFFS});
					break;
				case VIRTEX4:
					compatibleSites.put(PrimitiveType.BUFG,
							new PrimitiveType[]{PrimitiveType.BUFGCTRL});
					compatibleSites.put(PrimitiveType.IOB,
							new PrimitiveType[]{PrimitiveType.IOBM, PrimitiveType.IOBS, PrimitiveType.LOWCAPIOB});
					compatibleSites.put(PrimitiveType.IOBM,
							new PrimitiveType[]{PrimitiveType.NOMCAPIOB});
					compatibleSites.put(PrimitiveType.IOBS,
							new PrimitiveType[]{PrimitiveType.NOMCAPIOB});
					compatibleSites.put(PrimitiveType.IPAD,
							new PrimitiveType[]{PrimitiveType.IOBM, PrimitiveType.IOBS, PrimitiveType.LOWCAPIOB});
					compatibleSites.put(PrimitiveType.ILOGIC,
							new PrimitiveType[]{PrimitiveType.ISERDES});
					compatibleSites.put(PrimitiveType.OLOGIC,
							new PrimitiveType[]{PrimitiveType.OSERDES});
					compatibleSites.put(PrimitiveType.SLICEL,
							new PrimitiveType[]{PrimitiveType.SLICEM});	
					break;
				case VIRTEX5:
					compatibleSites.put(PrimitiveType.BUFG,
							new PrimitiveType[]{PrimitiveType.BUFGCTRL});
					compatibleSites.put(PrimitiveType.FIFO36_72_EXP,
							new PrimitiveType[]{PrimitiveType.RAMBFIFO36});
					compatibleSites.put(PrimitiveType.FIFO36_EXP,
							new PrimitiveType[]{PrimitiveType.RAMBFIFO36});
					compatibleSites.put(PrimitiveType.IOB,
							new PrimitiveType[]{PrimitiveType.IOBM, PrimitiveType.IOBS});
					compatibleSites.put(PrimitiveType.IPAD,
							new PrimitiveType[]{PrimitiveType.IOBM, PrimitiveType.IOBS});
					compatibleSites.put(PrimitiveType.ISERDES,
							new PrimitiveType[]{PrimitiveType.ILOGIC});
					compatibleSites.put(PrimitiveType.OSERDES,
							new PrimitiveType[]{PrimitiveType.OLOGIC});		
					compatibleSites.put(PrimitiveType.RAMB18X2,
							new PrimitiveType[]{PrimitiveType.RAMBFIFO36});
					compatibleSites.put(PrimitiveType.RAMB18X2SDP,
							new PrimitiveType[]{PrimitiveType.RAMBFIFO36});
					compatibleSites.put(PrimitiveType.RAMB36SDP_EXP,
							new PrimitiveType[]{PrimitiveType.RAMBFIFO36});
					compatibleSites.put(PrimitiveType.RAMB36_EXP,
							new PrimitiveType[]{PrimitiveType.RAMBFIFO36});
					compatibleSites.put(PrimitiveType.RAMBFIFO18,
							new PrimitiveType[]{PrimitiveType.RAMBFIFO36});
					compatibleSites.put(PrimitiveType.RAMBFIFO18_36,
							new PrimitiveType[]{PrimitiveType.RAMBFIFO36});
					compatibleSites.put(PrimitiveType.SLICEL,
							new PrimitiveType[]{PrimitiveType.SLICEM});
					break;			
				case VIRTEX6:
					compatibleSites.put(PrimitiveType.BUFG,
							new PrimitiveType[]{PrimitiveType.BUFGCTRL});
					compatibleSites.put(PrimitiveType.FIFO36E1,
							new PrimitiveType[]{PrimitiveType.RAMBFIFO36E1});
					compatibleSites.put(PrimitiveType.ISERDESE1,
							new PrimitiveType[]{PrimitiveType.ILOGICE1});
					compatibleSites.put(PrimitiveType.IOB,
							new PrimitiveType[]{PrimitiveType.IOBM, PrimitiveType.IOBS});
					compatibleSites.put(PrimitiveType.IPAD,
							new PrimitiveType[]{PrimitiveType.IOBM, PrimitiveType.IOBS});
					compatibleSites.put(PrimitiveType.OSERDESE1,
							new PrimitiveType[]{PrimitiveType.OLOGICE1});
					compatibleSites.put(PrimitiveType.RAMB18E1,
							new PrimitiveType[]{PrimitiveType.FIFO18E1});
					compatibleSites.put(PrimitiveType.RAMB36E1,
							new PrimitiveType[]{PrimitiveType.RAMBFIFO36E1});	
					compatibleSites.put(PrimitiveType.SLICEL,
							new PrimitiveType[]{PrimitiveType.SLICEM});		
					break;
				case VIRTEXE:
					compatibleSites.put(PrimitiveType.IOB,
							new PrimitiveType[]{PrimitiveType.DLLIOB, PrimitiveType.PCIIOB});
					break;
				case ARTIX7:
					break;
				case KINTEX7:
					break;
				case VIRTEX7:
					break;
				default:
					break;
			}
			compatibleTypesArray[specificFamilyType.ordinal()] = compatibleSites;
		}
	}
}

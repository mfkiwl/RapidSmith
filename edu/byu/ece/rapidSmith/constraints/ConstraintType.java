/*
 * Copyright (c) 2010-2011 Brigham Young University
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
package edu.byu.ece.rapidSmith.constraints;

/**
 * This provides an enumeration of all Xilinx constraints as given in the 
 * 13.1 Constraints Guide.
 * Created on: May 5, 2011
 */
public enum ConstraintType {
	/** Area Group */
	AREA_GROUP,
	/** Asynchronous Register */
	ASYNC_REG,
	/** BEL */
	BEL,
	/** Block Name */
	BLKNM,
	/** BUFG */
	BUFG,
	/** Clock Dedicated Route */
	CLOCK_DEDICATED_ROUTE,
	/** Collapse */
	COLLAPSE,
	/** Component Group */
	COMPGRP,
	/** Configuration Mode */
	CONFIG_MODE,
	/** CoolCLOCK */
	COOL_CLK,
	/** Data Gate */
	DATA_GATE,
	/** DCI Cascade */
	DCI_CASCADE,
	/** DCI Value */
	DCI_VALUE,
	/** Default */
	DEFAULT,
	/** Diff_Term*/
	DIFF_TERM ,
	/** Directed Routing */
	DIRECTED_ROUTING,
	/** Disable */
	DISABLE,
	/** Drive */
	DRIVE,
	/** Drop Specifications */
	DROP_SPEC,
	/** Enable */
	ENABLE,
	/** Enable Suspend */
	ENABLE_SUSPEND,
	/** Fast */
	FAST,
	/** Feedback */
	FEEDBACK,
	/** File */
	FILE,
	/** Float */
	FLOAT,
	/** From Thru To */
	FROM_THRU_TO,
	/** From To */
	FROM_TO,
	/** FSM Style*/
	FSM_STYLE ,
	/** Hierarchical Block Name */
	HBLKNM,
	/** HIODELAY Group*/
	HIODELAY_GROUP ,
	/** Hierarchical Lookup Table Name */
	HLUTNM,
	/** H Set*/
	H_SET ,
	/** HU Set*/
	HU_SET ,
	/** Input Buffer Delay Value */
	IBUF_DELAY_VALUE,
	/** IFD Delay Value*/
	IFD_DELAY_VALUE ,
	/** In Term*/
	IN_TERM ,
	/** Input Registers */
	INREG,
	/** Internal Vref Bank */
	INTERNAL_VREF_BANK,
	/** IOB */
	IOB,
	/** Input Output Block Delay */
	IOBDELAY,
	/** Input Output Delay Group */
	IODELAY_GROUP,
	/** Input Output Standard */
	IOSTANDARD,
	/** Keep */
	KEEP,
	/** Keeper */
	KEEPER,
	/** Keep Hierarchy */
	KEEP_HIERARCHY,
	/** Location */
	LOC,
	/** Locate */
	LOCATE,
	/** Lock Pins */
	LOCK_PINS,
	/** Lookup Table Name */
	LUTNM,
	/** Map */
	MAP,
	/** Mark Debug*/
	MARK_DEBUG,
	/** Maximum Fanout */
	MAX_FANOUT,
	/** Maximum Delay */
	MAXDELAY,
	/** Maximum Product Terms */
	MAXPT,
	/** Maximum Skew */
	MAXSKEW,
	/** MCB Performance */
	MCB_PERFORMANCE,
	/** Master Input Output Delay Group */
	MIODELAY_GROUP,
	/** No Delay */
	NODELAY,
	/** No Reduce */
	NOREDUCE,
	/** Offset In */
	OFFSET_IN,
	/** Offset Out */
	OFFSET_OUT,
	/** Open Drain */
	OPEN_DRAIN,
	/** Out Term*/
	OUT_TERM ,
	/** Period */
	PERIOD,
	/** Pin */
	PIN,
	/** Post CRC */
	POST_CRC,
	/** Post CRC Action */
	POST_CRC_ACTION,
	/** Post CRC Frequency */
	POST_CRC_FREQ,
	/** Post CRC INIT Flag */
	POST_CRC_INIT_FLAG,
	/** Post CRC Signal */
	POST_CRC_SIGNAL,
	/** Post CRC Source */
	POST_CRC_SOURCE,
	/** Priority */
	PRIORITY,
	/** Prohibit */
	PROHIBIT,
	/** Pulldown */
	PULLDOWN,
	/** Pullup */
	PULLUP,
	/** Power Mode */
	PWR_MODE,
	/** Registers */
	REG,
	/** Relative Location */
	RLOC,
	/** Relative Location Origin */
	RLOC_ORIGIN,
	/** Relative Location Range */
	RLOC_RANGE,
	/** Save Net Flag */
	SAVE_NET_FLAG,
	/** Schmitt Trigger */
	SCHMITT_TRIGGER,
	/** SIM Collision Check */
	SIM_COLLISION_CHECK,
	/** Slew */
	SLEW,
	/** Slow */
	SLOW,
	/** Stepping */
	STEPPING,
	/** Suspend */
	SUSPEND,
	/** System Jitter */
	SYSTEM_JITTER,
	/** Temperature */
	TEMPERATURE,
	/** Timing Ignore */
	TIG,
	/** Timing Group */
	TIMEGRP,
	/** Timing Specifications */
	TIMESPEC,
	/** Timing Name */
	TNM,
	/** Timing Name Net */
	TNM_NET,
	/** Timing Point Synchronization */
	TPSYNC,
	/** Timing Thru Points */
	TPTHRU,
	/** Timing Specification Identifier */
	TSidentifier,
	/** U SET */
	U_SET,
	/** Use Internal VREF */
	USE_INTERNAL_VREF,
	/** Use LUTNM*/
	USE_LUTNM ,
	/** Use Low Skew Lines */
	USELOWSKEWLINES,
	/** Use Relative Location */
	USE_RLOC,
	/** VCCAUX */
	VCCAUX,
	/** Voltage */
	VOLTAGE,
	/** VREF */
	VREF,
	/** Wire And */
	WIREAND,
	/** XBLKNM */
	XBLKNM,

}

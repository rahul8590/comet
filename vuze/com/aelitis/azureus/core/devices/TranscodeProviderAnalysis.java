/*
 * Created on Mar 2, 2009
 * Created by Paul Gardner
 * 
 * Copyright 2009 Vuze, Inc.  All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 2 of the License only.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */


package com.aelitis.azureus.core.devices;

public interface 
TranscodeProviderAnalysis 
{
	public static final int PT_TRANSCODE_REQUIRED	= 1;	// Boolean
	public static final int PT_DURATION_MILLIS		= 2;	// Long
	public static final int PT_VIDEO_WIDTH			= 3;	// Long
	public static final int PT_VIDEO_HEIGHT			= 4;	// Long
	
	public static final int PT_FORCE_TRANSCODE		= 5;	// Boolean (set)

	
	public void
	cancel();
	
	public boolean
	getBooleanProperty(
		int		property );
	
	public void
	setBooleanProperty(
		int			property,
		boolean		value );
	
	public long
	getLongProperty(
		int		property );
}

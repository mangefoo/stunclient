/*    
    This file is part of the STUN Client.
    
    Copyright (C) 2010  Magnus Eriksson <eriksson.mag@gmail.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.kodholken.stunclient;

public class LoggerFactory {
	private static Logger.Observer observer;
	
	public static Logger getLogger(Object c) {
		return new Logger(c.getClass().getName());
	}
	
	public static void setObserver(Logger.Observer observer) {
		LoggerFactory.observer = observer;
	}
	
	public static Logger.Observer getObserver() {
		return observer;
	}
}

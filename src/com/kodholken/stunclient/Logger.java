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

public class Logger {
	public interface Observer {
		public void onLogEntry(String logEntry);
	}
	
	private String className;
	
	public Logger(String className) {
		this.className = className;
	}
	
	public void debug(String message) {
		LoggerFactory.getObserver().onLogEntry(message);
		System.out.println(className + ": " + message);
	}
}

/*
 * Chat System - P2P
 *     Copyright (C) 2014 LIVET BOUTOILLE
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.insatoulouse.chatsystem.ni.tcp;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by tlk on 27/11/14.
 */
public class TcpSocket extends ServerSocket {

    private static TcpSocket instance;

    private TcpSocket() throws IOException {
    }

    public static synchronized TcpSocket getInstance() throws IOException
    {
        if(instance == null)
        {
            instance = new TcpSocket();
        }
        return instance;
    }

}
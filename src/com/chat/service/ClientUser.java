package com.chat.service;

import java.net.Socket;

import com.chat.common.User;

public class ClientUser {

	public boolean checkUser(User u,Socket s)
	{
		return new ClientConServer().sendLoginInfoToServer(u,s);
	}
	
}

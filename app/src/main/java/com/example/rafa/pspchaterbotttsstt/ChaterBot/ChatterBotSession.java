package com.example.rafa.pspchaterbotttsstt.ChaterBot;


public interface ChatterBotSession {
    ChatterBotThought think(ChatterBotThought thought) throws Exception;

    String think(String text) throws Exception;
}

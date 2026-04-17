package com.example.stomp.chat.document.enum_type;

public enum ChatChapter {
    STAND_BY, // A chapter which waits for both user's entrance.
    APPEALING, // A chapter which explains the situation in each to LLM.
    ARGUING, // A chapter which argues right and wrong based on each's appealing.
    JUDJED; // A chapter where LLM makes the final decision.
}

package com.example.telegrambot.domain;

public enum Position {

    // input data of lessons
    INPUT_LESSON,
    INPUT_FORMAT,
    INPUT_DAY,
    INPUT_TEACHER,
    INPUT_LINK,
    INPUT_NUMBER_FOR_REMOVE,

    // output data of lessons
    SELECTION_OF_THE_DAY_FOR_INPUT,

    // input data of teacher
    INPUT_TEACHER_FIRSTNAME,
    INPUT_TEACHER_LASTNAME,
    INPUT_PHONE_NUMBER,
    INPUT_EMAIL,
    INPUT_POSITION,
    INPUT_TEACHER_NUMBER_FOR_REMOVE,

    INPUT_TEACHER_NUMBER_FOR_EDIT,
    INPUT_CHOICE_FOR_EDIT,

    // input data for edit teachers
    EDIT_FIRST_NAME,
    EDIT_LAST_NAME,
    EDIT_POSITION,
    EDIT_PHONE_NUMBER,
    EDIT_EMAIL,

    NONE
}

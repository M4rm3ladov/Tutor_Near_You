package com.example.tutornearyou;

import com.example.tutornearyou.Model.TutorInfoModel;

public class CommonClass {
    public static final String TUTOR_INFO_REFERENCE = "TutorInfo";
    public static final String TUTORS_LOCATION_REFERENCES = "TutorsLocation";
    public static TutorInfoModel currentUser;

    public static String buildWelcomeMessage() {
        if (CommonClass.currentUser != null){
            return ("Welcome: ")
                    + (CommonClass.currentUser.getFirstName())
                    + (" ")
                    + (CommonClass.currentUser.getLastName().toString());
        }else{
            return "";
        }
    }
}

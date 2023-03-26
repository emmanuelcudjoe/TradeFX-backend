package com.cjvisions.tradefx_backend.services;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class UserSMSFeedback implements FeedBackMessenger{
    public static final String ACCOUNT_SID = "AC727ec208f20ba847a8a28b44ef93fa31";
    public static final String AUTH_TOKEN = "77c9ca91df717556d46c5f468e1d9ba2";

    @Override
    public void sendMessageToUser(String contact, String messageBody) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        try {
            Message message = Message.creator(
                    new PhoneNumber(contact), // To number
                    new PhoneNumber("+14344044703"), // From number
                    messageBody // SMS body
            ).create();

            System.out.println(message.getSid());
        } catch (final ApiException e) {
            System.err.println(e);
        }
    }
}

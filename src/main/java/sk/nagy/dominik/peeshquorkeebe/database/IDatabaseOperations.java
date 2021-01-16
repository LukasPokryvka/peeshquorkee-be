package sk.nagy.dominik.peeshquorkeebe.database;

import sk.nagy.dominik.peeshquorkeebe.restapi.chat.ChatHistory;
import sk.nagy.dominik.peeshquorkeebe.restapi.chat.ChatMessageReceived;
import sk.nagy.dominik.peeshquorkeebe.restapi.login.UserLoginRequest;
import sk.nagy.dominik.peeshquorkeebe.restapi.login.UserLoginResponse;
import sk.nagy.dominik.peeshquorkeebe.restapi.register.UserRegisterRequest;
import sk.nagy.dominik.peeshquorkeebe.restapi.register.UserRegisterResponse;

import java.sql.Timestamp;

public interface IDatabaseOperations {
    UserLoginResponse userLogin(UserLoginRequest userLoginRequest);
    ChatHistory lastTenMessages(String timestamp);
    ChatMessageReceived chatMessageReceived(String nickname, String message, String email, Timestamp timestamp);
    UserRegisterResponse userRegisterResponse(UserRegisterRequest userRegisterRequest);
}
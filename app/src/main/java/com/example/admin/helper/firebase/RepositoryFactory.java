package com.example.admin.helper.firebase;

public class RepositoryFactory {
    public static UserRepository getUserRepository() {
        return new UserRepository();
    }

}
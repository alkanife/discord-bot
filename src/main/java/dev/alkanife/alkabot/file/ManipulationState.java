package dev.alkanife.alkabot.file;

public enum ManipulationState {

    SUCCESS,
    ERROR_READ,
    ERROR_WRITE,
    ERROR_DELETE,
    FILE_DONT_EXISTS,
    FILE_IS_DIRECTORY,
    PARENT_DIRECTORY_ERROR,
    NO_CONTENT_GIVEN;

    public boolean failed() {
        return this != SUCCESS;
    }

    public boolean succeed() {
        return this == SUCCESS;
    }

}

package ar.edu.itba.pod.client;

import java.util.concurrent.TimeUnit;

public class Util {
    public static final String GENERIC_ERROR_MESSAGE = "Oops! Something unexpected went wrong. Try again.";
    public static final String INVALID_ARGUMENT_MESSAGE = "Oops! Invalid arguments were sent. Try again.";
    public static final String IO_ERROR_MESSAGE = "Oops! Something went wrong when accessing the provided path. Try again.";
    public static final String SERVER_UNAVAILABLE_MESSAGE = "Oops! The server is unreachable on the provided address. Try again.";
    public static final int SYSTEM_TIMEOUT = 1;
    public static final TimeUnit SYSTEM_TIMEOUT_UNIT = TimeUnit.MINUTES;
}

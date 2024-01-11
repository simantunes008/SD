package Client;

import Server.Task;
import Server.User;

import java.io.IOException;

public interface IClient {
    /**
     * Registers a user with the server.
     *
     * @param user The user to be registered.
     * @return True if registration is successful; false otherwise.
     */
    boolean register(User user) throws IOException;

    /**
     * Authenticates a user with the server.
     *
     * @param user The user to be authenticated.
     * @return True if authentication is successful; false otherwise.
     */
    boolean authenticate(User user) throws IOException;

    /**
     * Requests the server to perform a task.
     *
     * @param task The task to be executed.
     * @return The result of the task.
     */
    byte[] request(Task task) throws IOException;

    /**
     * Retrieves the available memory from the server.
     *
     * @return The available memory in bytes.
     */
    int getAvailableMemory() throws IOException;

    /**
     * Retrieves the number of pending tasks from the server.
     *
     * @return The number of pending tasks.
     */
    int getPendingTasks() throws IOException;

    /**
     * Closes the connection to the server.
     */
    void closeConnection() throws IOException;

}

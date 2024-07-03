package ex3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.util.Arrays.asList;

class ContactManager {
    private HashMap<String, Contact> contacts = new HashMap<>();
    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    // @TODO
    public void update(Contact c) {
        try {
            readWriteLock.writeLock().lock();
            contacts.put(c.name(), c);
            System.out.println("Contact updated: " + c.name());
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    // @TODO
    public ContactList getContacts() {
        try {
            readWriteLock.readLock().lock();
            ContactList cl = new ContactList();
            cl.addAll(contacts.values());
            return cl;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }
}

class ServerWorker implements Runnable {
    private Socket socket;
    private ContactManager manager;

    public ServerWorker(Socket socket, ContactManager manager) {
        this.socket = socket;
        this.manager = manager;
    }

    // @TODO
    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            Contact c;
            while ((c = Contact.deserialize(in)) != null) {
                manager.update(c);
            }

            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}



public class Server {

    public static void main (String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(12345);
        ContactManager manager = new ContactManager();
        // example pre-population
        manager.update(new Contact("John", 20, 253123321, null, asList("john@mail.com")));
        manager.update(new Contact("Alice", 30, 253987654, "CompanyInc.", asList("alice.personal@mail.com", "alice.business@mail.com")));
        manager.update(new Contact("Bob", 40, 253123456, "Comp.Ld", asList("bob@mail.com", "bob.work@mail.com")));

        while (true) {
            Socket socket = serverSocket.accept();
            Thread worker = new Thread(new ServerWorker(socket, manager));
            worker.start();
        }
    }

}

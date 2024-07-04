package ex1_2_3_4;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

class ContactList extends ArrayList<Contact> {

    // @TODO
    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(size());
        for (Contact c : this)
            c.serialize(out);
    }

    // @TODO
    public static ContactList deserialize(DataInputStream in) throws IOException {
        int size = in.readInt();
        ContactList cl = new ContactList();
        for (int i = 0; i < size; i++)
            cl.add(Contact.deserialize(in));
        return cl;
    }

}

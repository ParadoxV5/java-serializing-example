/*
  Serialize   = convert OOP object to data that can be saved or shared
  Deserialize = recreate the object from serialized data
*/

/**
  {@link java.io.Serializable} is a blank interfact that marks
  implementing classes of objects to be, well, serializable.
  <p>
  Without it, Java will throw {@link java.io.NotSerializableException}
  if attempting to serialize an object of such class
*/
public class MyObject implements java.io.Serializable {
  /**
    Even IDEs will prompt you to add a version ID to your serializable.
    <p>
    It marks versions so deserializing data from incompatible
    (outdated) versions throws {@link java.io.InvalidClassException}
    instead of just using default values.
    <p>
    This is not mandatory, but highly recommended.
  */
  private static final long serialVersionUID = 1;
  
  
  //    By default, all instance (non-static) fields    //
  // are serialized (given that they are Serializable). //
  
  /** Primitives are natually serializable because they are, well, “primal”. */
  public double value;
  /** @see #value */
  public boolean mark;
  
  /** Most of Java’s built-in classes are preconfigured to be serializable. */
  public String name;
  
  /**
    {@link java.util.GregorianCalendar} inherits from the
    serializable abstract class {@link java.util.Calendar}.
    @see #name
  */
  public java.util.GregorianCalendar date;
  /** Whether or not an array is serializable depends on its contents. */
  public MyObject[] array;
  /**
    {@link java.util.ArrayList}s are similar to arrays. They do implement
    serializable themselves but still depends on their contents be serializable.
    @see #array
    @see #name
  */
  public java.util.ArrayList<MyObject> list;
  
  
  // `transient` marks a field to not serialize for a variety of reasons. //
  
  /**
    {@link java.io.BufferedOutputStream} is NOT serializable.
    (Java Serialization to BufferedOutputStream: You are not prepared!)
  */
  protected transient java.io.BufferedOutputStream output;
  /** Technially this IS serializable, but why serialize it? */
  transient String temporaryDiscordConnectionSecret;
  
  
  /** Methods and static stuff are NOT serialized because why bother? */
  public void addAllFromArrayToList() {
    for(MyObject obj: array) list.add(obj);
  }
  /** @see #addAllFromArrayToList() */
  public static double defaultValue = Math.PI;
  /** @see #addAllFromArrayToList() */
  public MyObject() {
    value = defaultValue;
  }
  /** @see #addAllFromArrayToList() */
  public static void main(@SuppressWarnings("javadoc") String[] __) {
    final String FILE_PATH = ".out.bin";
    
    /*
      Java serialization includes information about the class (naturally),
      a unique ID (to mark that it’s already serialized, in case of recursive
      referencing) and all instance stuff that are serialized.
    */
    final MyObject OBJ = new MyObject();
    // These two are serialized.
    OBJ.value = Math.random() * Math.random() / Math.random();
    // This won’t cause an infinite conversion loop as serialization remembers.
    OBJ.array = new MyObject[] {OBJ};
    /*
      This is NOT serialized.
      (Disclaimer: Not really anything Discord, I just used this for an  easily-
      understandable name for demonstration purposes. Also a shoutout, Wynaut?)
    */
    OBJ.temporaryDiscordConnectionSecret = "wumpus";
    
    // Demonstrate de/serialization using try-with-resource blocks //
    
    /*
      java.io.ObjectOutputStream is a java.io.FilterOutputStream
      that can serialize (translate) your objects before data
      goes to its behind-the-scenes java.io.OutputStream.
    */
    try(java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(
      new java.io.FileOutputStream(FILE_PATH)
    )) {
      // ObjectOutputStream individually defines writeObject(Object).
      out.writeObject(OBJ);
    } catch(java.io.IOException e) {
      e.printStackTrace();
      return;
    }
    
    final MyObject objectIn;
    // Similarly, java.io.ObjectInputStream is the java.io.InputStream version.
    try(java.io.ObjectInputStream in = new java.io.ObjectInputStream(
      new java.io.FileInputStream(FILE_PATH)
    )) {
      // Ditto …
      objectIn = (MyObject)in.readObject();
      // Note the casting: ObjectInputStream’s Java code don’t know your class.
    } catch(
      // Can’t cast: class of object read in is not MyObject
      ClassCastException |
      // Can’t read object because the compiled Java
      // code (“classfile”) for its class is missing.
      ClassNotFoundException |
      /* All exceptions regarding IO, here also including subclasses:
        * java.io.FileNotFoundException
        * java.io.InvalidClassException:
            See its and #serialVersionUID’s Javadocs for details.
      */
      java.io.IOException
    e) {
      e.printStackTrace();
      return;
    }
    
    // Test: These should all be true. //
    
    // What’s written is equivalent (not *technically SAME ONE*) to the OG …
    System.out.println(OBJ.value          == objectIn.value         );
    System.out.println(OBJ.array.length   == objectIn.array.length  );
    System.out.println(OBJ.array[0].value == objectIn.array[0].value);
    System.out.println(OBJ.value          == objectIn.array[0].value);
    // … if you ignore the stuff are not serialized.
    System.out.println(objectIn.temporaryDiscordConnectionSecret == null);
    System.out.println(OBJ.temporaryDiscordConnectionSecret !=
      objectIn.temporaryDiscordConnectionSecret
    );
    // Hmm, so when overriding Object#equals(Object),
    // we better not count the stuff that aren’t serialized! (;
  }
}
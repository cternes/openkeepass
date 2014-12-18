openkeepass
===========

openkeepass is a java library for reading KeePass databases. It is the only java library that supports **KeePass 2.x database files**.  

*Only KeePass files created with version 2.x are supported. KeePass files created with version 1.x are NOT supported.* 

Features included so far:

- Password or Keyfile credentials: openkeepass can open password protected databases as well as keyfile protected databases.
- Easy to learn API: openkeepass has a simple API with convenient methods that makes it easy to read data from a KeePass database.
- Very lean: openkeepass tries to keep the necessary dependencies to an absolute minimum.

Requirements
=============
Before using this library make sure that you have the Java Cryptography Extension (JCE) installed on your system. 

You can download JCE here:

- [Download JCE for JDK 7](http://www.oracle.com/technetwork/java/embedded/embedded-se/downloads/jce-7-download-432124.html)
- [Download JCE for JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)

Examples
=============

The basic usage is very simple. This example will show you how to retrieve all entries and the top groups of the KeePass database.  

    // Open Database
	KeePassFile database = KeePassDatabase.getInstance("Database.kdbx").openDatabase("MasterPassword");
		
	// Retrieve all entries
	List<Entry> entries = database.getEntries();
	for (Entry entry : entries) {
		System.out.println("Title: " + entry.getTitle() + " Password: " + entry.getPassword());
	}

	// Retrieve all top groups
	List<Group> groups = database.getTopGroups();
	for (Group group : groups) {
		System.out.println(group.getName());
	}

You can also search for specific entries in the database:

	// Search for single entry
	Entry sampleEntry = database.getEntryByTitle("Sample Entry");
	System.out.println("Title: " + sampleEntry.getTitle() + " Password: " + sampleEntry.getPassword());

	// Search for all entries that contain 'Sample' in title
	List<Entry> entriesByTitle = database.getEntriesByTitle("Sample", false);
	for (Entry entry : entriesByTitle) {
		System.out.println("Title: " + entry.getTitle() + " Password: " + entry.getPassword());
	}
	
For more usages have a look into the unit test classes.
[![Build Status](https://travis-ci.org/cternes/openkeepass.svg?branch=master)](https://travis-ci.org/cternes/openkeepass)

openkeepass
===========

openkeepass is a java library for reading and writing KeePass databases. It is an intuitive java library that supports **KeePass 2.x database files**.  

*Only KeePass files created with version 2.x are supported. KeePass files created with version 1.x are NOT supported.* 

Features included so far:

- Reading and writing support for KeePass 2.x
- Password or Keyfile credentials: openkeepass can open password protected databases as well as keyfile protected databases.
- Android Support: Will run on Android devices.
- Easy to learn API: openkeepass has a simple API with convenient methods that makes it easy to read data from a KeePass database.
- Very lean: openkeepass tries to keep the necessary dependencies to an absolute minimum.
- Backward compatible until Java 6

Installation
=============

The easiest way is to add openkeepass as a maven dependency. 

	<dependency>
	    <groupId>de.slackspace</groupId>
    	<artifactId>openkeepass</artifactId>
	    <version>0.8.1</version>
	</dependency>

Prerequisites
=============

Before using this library make sure that you have the Java Cryptography Extension (JCE) installed on your system. 

You can download JCE here:

- [Download JCE for JDK 7](http://www.oracle.com/technetwork/java/embedded/embedded-se/downloads/jce-7-download-432124.html)
- [Download JCE for JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)

Android
=============

Android users should apply the following dependency to avoid an error regarding build-in xml libraries:

    compile ('de.slackspace:openkeepass:0.6.0') {
        exclude module: 'stax'
        exclude module: 'stax-api'
        exclude module: 'xpp3'
    }

Examples for reading
=============

The basic usage is very simple. This example will show you how to retrieve all entries and the top groups of the KeePass database.  

```java
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
```

You can also search for specific entries in the database:

```java
	// Search for single entry
	Entry sampleEntry = database.getEntryByTitle("Sample Entry");
	System.out.println("Title: " + sampleEntry.getTitle() + " Password: " + sampleEntry.getPassword());

	// Search for all entries that contain 'Sample' in title
	List<Entry> entriesByTitle = database.getEntriesByTitle("Sample", false);
	for (Entry entry : entriesByTitle) {
		System.out.println("Title: " + entry.getTitle() + " Password: " + entry.getPassword());
	}
```

Open a database with a key file:

```java
	// Open database with keyfile
	KeePassFile database = KeePassDatabase.getInstance("DatabaseProtectedByKeyfile.kdbx").openDatabase(new File("Keyfile.key"));
		
	// Print all entries		
	List<Entry> entries = database.getEntries();
	for (Entry entry : entries) {
		System.out.println(entry.getTitle() + ":" + entry.getPassword());
	}
```

Retrieve custom string fields (Advanced tab) from a database:

```java		
	// Retrieve all properties including custom string fields of an entry
	Set<Property> properties = database.getEntryByTitle("1st Entry").getProperties();
	for (Property property : properties) {
		System.out.println(property.getKey() + ":" + property.getValue());
	}
```
	
For more usages have a look into the unit test classes.
	
Examples for writing
=============
	
If you want to start writing a new KeePass file from scratch you first have to build up your database model. This will be done using the provided builders.
After the model has been constructed you can use the __KeePassDatabase__ class to write the KeePass database to a stream.

```java	
	// Build KeePass model
	Group root = new GroupBuilder()
					.addEntry(new EntryBuilder("First entry").username("Peter").password("Peters secret").build())
					.addGroup(new GroupBuilder("Banking")
							.addEntry(new EntryBuilder("Second entry").username("Paul").password("secret").build())
							.build())
					.build();
				
	KeePassFile keePassFile = new KeePassFileBuilder("writingDB")
					.addTopGroups(root)
					.build();
				
	// Write KeePass file to disk
	KeePassDatabase.write(keePassFile, "MasterPassword", new FileOutputStream("Database.kdbx"));
```
	
For more usages have a look into the unit test classes.

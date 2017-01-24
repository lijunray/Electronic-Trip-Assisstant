package com.nyu.cs9033.eta.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Person implements Parcelable {
	
	// Member fields should exist here, what else do you need for a person?
	// Please add additional fields
	private int position;
	private String name;
	private String currentLocation;
	private String phoneNumber;

	/**
	 * Parcelable creator. Do not modify this function.
	 */
	public static final Parcelable.Creator<Person> CREATOR = new Parcelable.Creator<Person>() {
		public Person createFromParcel(Parcel p) {
			return new Person(p);
		}

		public Person[] newArray(int size) {
			return new Person[size];
		}
	};
	
	/**
	 * Create a Person model object from a Parcel. This
	 * function is called via the Parcelable creator.
	 * 
	 * @param p The Parcel used to populate the
	 * Model fields.
	 */
	public Person(Parcel p) {
		this.position = p.readInt();
		this.name = p.readString();
		this.currentLocation = p.readString();
		this.phoneNumber = p.readString();
	}

	/**
	 * Create a Person model object from arguments
	 *
	 * @param name Add arbitrary number of arguments to
	 * instantiate Person class based on member variables.
	 */
	public Person(String name, String currentLocation, String phoneNumber) {
		this.name = name;
		this.currentLocation = currentLocation;
		this.phoneNumber = phoneNumber;
	}

	public Person(int position, String name, String currentLocation, String phoneNumber) {
		this.position = position;
		this.name = name;
		this.currentLocation = currentLocation;
		this.phoneNumber = phoneNumber;
	}

	/**
	 * Serialize Person object by using writeToParcel.  
	 * This function is automatically called by the
	 * system when the object is serialized.
	 * 
	 * @param dest Parcel object that gets written on 
	 * serialization. Use functions to write out the
	 * object stored via your member variables. 
	 * 
	 * @param flags Additional flags about how the object 
	 * should be written. May be 0 or PARCELABLE_WRITE_RETURN_VALUE.
	 * In our case, you should be just passing 0.
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(position);
		dest.writeString(name);
		dest.writeString(currentLocation);
		dest.writeString(phoneNumber);
	}

	public int getPosition() {
		return position;
	}

	public String getName() {
		return name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getCurrentLocation() {
		return currentLocation;
	}
	/**
	 * Feel free to add additional functions as necessary below.
	 */
	
	/**
	 * Do not implement
	 */
	@Override
	public int describeContents() {
		// Do not implement!
		return 0;
	}
}

package com.nyu.cs9033.eta.models;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.Time;

import java.util.ArrayList;
import java.util.List;

import com.nyu.cs9033.eta.models.Person;

public class Trip implements Parcelable {
	
	// Member fields should exist here, what else do you need for a trip?
	// Please add additional fields
	private int _id;
	private String name;
	private String location;
	private List<Person> friends;
	private String startTime;
	private String endTime;
	private String description;
	private long webId;
	private int status;
	private boolean isArrived;

	/**
	 * Parcelable creator. Do not modify this function.
	 */
	public static final Parcelable.Creator<Trip> CREATOR = new Parcelable.Creator<Trip>() {
		public Trip createFromParcel(Parcel p) {
			return new Trip(p);
		}

		public Trip[] newArray(int size) {
			return new Trip[size];
		}
	};

	public Trip () {
		this.friends = new ArrayList<Person>();
	}
	
	/**
	 * Create a Trip model object from a Parcel. This
	 * function is called via the Parcelable creator.
	 * 
	 * @param p The Parcel used to populate the
	 * Model fields.
	 */
	public Trip(Parcel p) {
		this();
		this._id = p.readInt();
		this.name = p.readString();
		this.location = p.readString();
		p.readTypedList(friends, Person.CREATOR);
		this.startTime = p.readString();
		this.endTime = p.readString();
		this.description = p.readString();
		this.webId = p.readLong();
		this.status = p.readInt();
		this.isArrived = p.readByte() != 0;
	}

	/**
	 * Create a Trip model object from arguments
	 *
	 * @param name  Add arbitrary number of arguments to
	 * instantiate Trip class based on member variables.
	 */
	public Trip(String name, String location, List<Person> friends, String startTime,
				String endTime, String description, long webId, int status) {
		this.name = name;
		this.location = location;
		this.friends = friends;
		this.startTime = startTime;
		this.endTime = endTime;
		this.description = description;
		this.webId = webId;
		this.status = status;
	}

	public Trip(int _id, String name, String location, List<Person> friends, String startTime,
				String endTime, String description, long webId, int status) {
		this(name, location, friends, startTime, endTime, description, webId, status);
		this._id = _id;
	}

	public Trip(int _id, String name, String location, List<Person> friends, String startTime,
				String endTime, String description, long webId, int status, boolean isArrived) {
		this(_id, name, location, friends, startTime, endTime, description, webId, status);
		this.isArrived = isArrived;
	}

	/**
	 * Serialize Trip object by using writeToParcel. 
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
		dest.writeInt(_id);
		dest.writeString(name);
		dest.writeString(location);
		dest.writeTypedList(friends);
		dest.writeString(startTime);
		dest.writeString(endTime);
		dest.writeString(description);
		dest.writeLong(webId);
		dest.writeInt(status);
		dest.writeByte((byte) (isArrived ? 1 : 0));
	}
	
	/**
	 * Feel free to add additional functions as necessary below.
	 */

	public int get_id () {
		return _id;
	}

	public String getName() {
		return name;
	}

	public String getLocation() {
		return location;
	}

	public List<Person> getFriends() {
		return friends;
	}

	public String getStartTime() {
		return startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public String getDescription() {
		return description;
	}

	public long getWebId() {
		return webId;
	}

	public int getStatus() {

		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isArrived() {
		return isArrived;
	}

	public void setIsArrived(boolean isArrived) {
		this.isArrived = isArrived;
	}

	/**
	 * Do not implement
	 */
	@Override
	public int describeContents() {
		// Do not implement!
		return 0;
	}
}

package com.adarshhasija.ahelp;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class ExamParcelable implements Parcelable {
	
	private String parseId;
	private long time;
	private String locationId;
	private String subjectId;
	private String notes;
	

	public ExamParcelable(long time, String locationId) {
		super();
		this.time = time;
		this.locationId = locationId;
	}

	public String getParseId() {
		return parseId;
	}

	public void setParseId(String parseId) {
		this.parseId = parseId;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(parseId);
		parcel.writeLong(time);
		parcel.writeString(locationId);
		parcel.writeString(subjectId);
		parcel.writeString(notes);
	}
	
	public ExamParcelable(Parcel p) {
		parseId = p.readString();
		time = p.readLong();
		locationId = p.readString();
		subjectId = p.readString();
		notes = p.readString();
	}
	
	public static final Creator<ExamParcelable> CREATOR = new Creator<ExamParcelable>() {

		@Override
		public ExamParcelable createFromParcel(Parcel parcel) {
			return new ExamParcelable(parcel);
		}

		@Override
		public ExamParcelable[] newArray(int size) {
			return new ExamParcelable[size];
		}
		
	};

}

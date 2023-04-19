package com.cosimogiani.museum.model;

import java.util.Objects;

public class Work extends BaseEntity {
	
	private Artist artist;
	private String title;
	private String type;
	private String description;
	
	public Work(String id, Artist artist, String title, String type, String description) {
		super(id);
		this.artist = artist;
		this.title = title;
		this.type = type;
		this.description = description;
	}
	
	public Work(Artist artist, String title, String type, String description) {
		this.artist = artist;
		this.title = title;
		this.type = type;
		this.description = description;
	}
	
	public Work() {
		this.artist = null;
		this.title = "test";
		this.type = "test";
		this.description = "test";
	}
	
	public Artist getArtist() {
		return artist;
	}
	
	public void setArtist(Artist artist) {
		this.artist = artist;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Work other = (Work) obj;
		return Objects.equals(artist, other.artist)
				&& Objects.equals(title, other.title)
				&& Objects.equals(type, other.type)
				&& Objects.equals(description, other.description);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(artist, title, type, description);
	}
	
	@Override
	public String toString() {
		return title + " - " + type + " - " + description;
	}

}

package com.cosimogiani.museum.model;

import java.util.Objects;

public class Artist extends BaseEntity {
	
	private String name;
	
	public Artist(String id, String name) {
		super(id);
		this.name = name;
	}
	
	public Artist(String name) {
		this.name = name;
	}
	
	public Artist() {
		this.name = "test";
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Artist other = (Artist) obj;
		return Objects.equals(name, other.name) 
				&& Objects.equals(id, other.id);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}
	
	@Override
	public String toString() {
		return name;
	}

}

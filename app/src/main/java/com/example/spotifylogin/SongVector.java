package com.example.spotifylogin;

public class SongVector {
	private String id;
	private Double[] vector;

	public SongVector(String id, Double[] vector) {
		super();
		this.id = id;
		this.vector = vector;
	}

	public String getId() {
		return id;
	}

	public Double[] getVector() {
		return vector;
	}

}

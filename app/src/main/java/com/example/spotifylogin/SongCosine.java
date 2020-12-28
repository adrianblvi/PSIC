package com.example.spotifylogin;

public class SongCosine implements Comparable<SongCosine> {

	private String id;
	private Double cosine;

	public SongCosine(String id, Double cosine) {
		super();
		this.id = id;
		this.cosine = cosine;
	}

	public String getId() {
		return id;
	}

	public Double getCosine() {
		return cosine;
	}

	@Override
	public int compareTo(SongCosine cos) {
		if (cos.getCosine() < this.cosine) {
			return -1;
		} else {
			return 1;
		}
	}

}

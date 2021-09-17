package com.appcity.app.subscripciones.request;

public class InscripcionesComentarios {

	
	private String username;

	private String comentarios;
	
	private Integer likes;

	public InscripcionesComentarios() {
	}

	public InscripcionesComentarios(String username, String comentarios, Integer likes) {
		super();
		this.username = username;
		this.comentarios = comentarios;
		this.likes = likes;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getComentarios() {
		return comentarios;
	}

	public void setComentarios(String comentarios) {
		this.comentarios = comentarios;
	}

	public Integer getLikes() {
		return likes;
	}

	public void setLikes(Integer likes) {
		this.likes = likes;
	}
	
}
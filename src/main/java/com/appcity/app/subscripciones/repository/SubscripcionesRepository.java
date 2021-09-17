package com.appcity.app.subscripciones.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import com.appcity.app.subscripciones.models.Subscripciones;

public interface SubscripcionesRepository extends MongoRepository<Subscripciones, String>{

	@RestResource(path = "buscar-name")
	public Subscripciones findByNombre(@Param("name") String nombre);
	
}

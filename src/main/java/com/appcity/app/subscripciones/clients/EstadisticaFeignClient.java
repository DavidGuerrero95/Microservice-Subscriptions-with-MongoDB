package com.appcity.app.subscripciones.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "app-estadistica")
public interface EstadisticaFeignClient {

	@PutMapping("/estadistica/obtenerEstadistica/{nombre}")
	public void obtenerEstadistica(@PathVariable("nombre") String nombre);
	
}

package com.appcity.app.subscripciones.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "app-notificaciones")
public interface NotificacionesFeignClient {

	@PostMapping("/notificaciones/suscripciones")
	public void enviarMensajeSuscripciones(@RequestParam String nombre, @RequestParam String username);

	@PostMapping("/notificaciones/inscripciones")
	public void enviarMensajeInscripciones(@RequestParam String nombre, @RequestParam String username);

}

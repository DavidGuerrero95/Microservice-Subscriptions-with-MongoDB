package com.appcity.app.subscripciones.controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.appcity.app.subscripciones.clients.EstadisticaFeignClient;
import com.appcity.app.subscripciones.clients.NotificacionesFeignClient;
import com.appcity.app.subscripciones.models.Subscripciones;
import com.appcity.app.subscripciones.repository.SubscripcionesRepository;
import com.appcity.app.subscripciones.request.InscripcionesComentarios;

@RestController
public class SubscripcionesController {

	@Autowired
	SubscripcionesRepository subscripciones;

	@Autowired
	EstadisticaFeignClient estadistica;

	@Autowired
	NotificacionesFeignClient notificaciones;

	@PostMapping("/subscripciones/crear")
	@ResponseStatus(code = HttpStatus.CREATED)
	public void crearSubscripciones(@RequestParam("nombre") String nombre) {
		Subscripciones proyecto = new Subscripciones();
		proyecto.setNombre(nombre);
		proyecto.setSubscripciones(new ArrayList<String>());
		proyecto.setCuestionarios(new ArrayList<String>());
		proyecto.setLike(new ArrayList<String>());
		proyecto.setDislike(new ArrayList<String>());
		proyecto.setComentarios(new ArrayList<List<String>>());
		subscripciones.save(proyecto);
	}

	@GetMapping("/subscripciones/obtenerProyectoByNombre/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public Subscripciones getProyectosByNombre(@PathVariable("nombre") String nombre) {
		return subscripciones.findByNombre(nombre);
	}

	@PutMapping("/subscripciones/inscripciones/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public void inscripcionesProyecto(@PathVariable("nombre") String nombre,
			@RequestBody InscripcionesComentarios inscripciones) {
		Subscripciones proyecto = subscripciones.findByNombre(nombre);
		if (!proyecto.getSubscripciones().contains(inscripciones.getUsername())) {
			List<String> subs = proyecto.getSubscripciones();
			subs.add(inscripciones.getUsername());
			proyecto.setSubscripciones(subs);
			subscripciones.save(proyecto);
			estadistica.obtenerEstadistica(nombre);
			notificaciones.enviarMensajeSuscripciones(nombre, inscripciones.getUsername());
		}

	}

	@GetMapping("/subscripciones/verificarInscripcion/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public Boolean verificarInscripcion(@PathVariable("nombre") String nombre,
			@RequestBody InscripcionesComentarios inscripciones) {
		try {
			Subscripciones proyecto = subscripciones.findByNombre(nombre);
			return proyecto.getSubscripciones().contains(inscripciones.getUsername());
		} catch (Exception e) {
			return null;
		}
	}

	@PutMapping("/subscripciones/anularInscripciones/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public String anularInscripcionesProyecto(@PathVariable("nombre") String nombre,
			@RequestBody InscripcionesComentarios inscripciones) {
		try {
			Subscripciones proyecto = subscripciones.findByNombre(nombre);
			if (proyecto.getSubscripciones().contains(inscripciones.getUsername())) {
				List<String> subs = proyecto.getSubscripciones();
				subs.remove(inscripciones.getUsername());
				proyecto.setSubscripciones(subs);
				subscripciones.save(proyecto);
				estadistica.obtenerEstadistica(nombre);
				return "Eliminar suscripcion de proyecto: " + nombre + " de manera Exitosa!";
			} else {
				return "El usuario: " + inscripciones.getUsername() + " no está registrado en este proyecto";
			}
		} catch (Exception e) {
			return "Proyecto: " + nombre + " no existe";
		}
	}

	@PutMapping("/subscripciones/comentarios/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public String comentariosProyecto(@PathVariable("nombre") String nombre,
			@RequestBody InscripcionesComentarios comentarios) {
		try {
			Subscripciones proyecto = subscripciones.findByNombre(nombre);
			List<List<String>> coment = proyecto.getComentarios();
			List<String> usuarios = new ArrayList<String>();
			Calendar c = Calendar.getInstance();

	        String dia = Integer.toString(c.get(Calendar.DATE));
	        String mes = Integer.toString(c.get(Calendar.MONTH));
	        String annio = Integer.toString(c.get(Calendar.YEAR));
	        String fecha = dia+"/"+mes+"/"+annio;
	        
	        Integer hora = c.get(Calendar.HOUR_OF_DAY);
	        Integer minutos = c.get(Calendar.MINUTE);
	        String tiempo = hora+":"+minutos;
			usuarios.add(comentarios.getUsername());
			usuarios.add(comentarios.getComentarios());
			usuarios.add(fecha);
			usuarios.add(tiempo);
			coment.add(usuarios);
			subscripciones.save(proyecto);
			estadistica.obtenerEstadistica(nombre);
			return "Comentario añadido al proyecto: " + nombre + " de forma Exitosa!";
		} catch (Exception e) {
			return "Proyecto: " + nombre + " no existe";
		}
	}

	@GetMapping("/subscripciones/verComentarios/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public List<List<String>> verComentarios(@PathVariable("nombre") String nombre) {
		try {
			Subscripciones proyecto = subscripciones.findByNombre(nombre);
			return proyecto.getComentarios();
		} catch (Exception e) {
			return null;
		}
	}

	@PutMapping("/subscripciones/inscribirCuestionario/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public void inscribirCuestionario(@PathVariable String nombre, @RequestParam String usuario) {
		try {
			Subscripciones proyecto = subscripciones.findByNombre(nombre);
			List<String> cuest = proyecto.getCuestionarios();
			if (!cuest.contains(usuario)) {
				cuest.add(usuario);
				proyecto.setCuestionarios(cuest);
				subscripciones.save(proyecto);
				notificaciones.enviarMensajeInscripciones(nombre, usuario);
			}
		} catch (Exception e) {
			System.out.println("ERROR" + e.getLocalizedMessage());
		}
	}

	@GetMapping("/subscripciones/verificarCuestionario/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public Boolean verificarCuestionario(@PathVariable("nombre") String nombre,
			@RequestBody InscripcionesComentarios cuestionario) {
		try {
			Subscripciones proyecto = subscripciones.findByNombre(nombre);
			return proyecto.getCuestionarios().contains(cuestionario.getUsername());
		} catch (Exception e) {
			return null;
		}
	}

	@PutMapping("/subscripciones/likes/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public String likes(@PathVariable("nombre") String nombre, @RequestBody InscripcionesComentarios likes) {
		try {
			Subscripciones proyecto = subscripciones.findByNombre(nombre);
			List<String> like = proyecto.getLike();
			List<String> dislike = proyecto.getDislike();
			String usuario = likes.getUsername();
			if (likes.getLikes() == 0) {
				if (dislike.contains(usuario))
					dislike.remove(usuario);
				if (like.contains(usuario))
					like.remove(usuario);
			} else if (likes.getLikes() == 1) {
				if (dislike.contains(usuario))
					dislike.remove(usuario);
				if (!like.contains(usuario)) {
					like.add(usuario);
				}
			} else if (likes.getLikes() == 2) {
				if (like.contains(usuario))
					like.remove(usuario);
				if (!dislike.contains(usuario)) {
					dislike.add(usuario);
				}
			}
			proyecto.setLike(like);
			proyecto.setDislike(dislike);
			subscripciones.save(proyecto);
			estadistica.obtenerEstadistica(nombre);
			return "Añadido exitosamente";
		} catch (Exception e) {
			return "error";
		}
	}

	@GetMapping("/subscripciones/revisarLikes/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public Integer revisarLikes(@PathVariable("nombre") String nombre, @RequestBody InscripcionesComentarios username) {
		Subscripciones proyecto = subscripciones.findByNombre(nombre);
		if (proyecto.getLike().contains(username.getUsername())) {
			return 1;
		} else if (proyecto.getDislike().contains(username.getUsername())) {
			return 2;
		} else {
			return 0;
		}
	}

	@DeleteMapping("/suscripciones/borrar/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public void borrarSuscripciones(@PathVariable String nombre) {
		Subscripciones proyecto = subscripciones.findByNombre(nombre);
		String id = proyecto.getId();
		subscripciones.deleteById(id);
	}
	
	@PutMapping("/suscripciones/arreglarSuscripciones")
	@ResponseStatus(code = HttpStatus.OK)
	public void arreglarNotificaciones(){
		Calendar c = Calendar.getInstance();

        String dia = Integer.toString(c.get(Calendar.DATE));
        String mes = Integer.toString(c.get(Calendar.MONTH));
        String annio = Integer.toString(c.get(Calendar.YEAR));
        String fecha = dia+"/"+mes+"/"+annio;
        
        Integer hora = c.get(Calendar.HOUR_OF_DAY);
        Integer minutos = c.get(Calendar.MINUTE);
        String tiempo = hora+":"+minutos;
        
        
        List<Subscripciones> listaSuscripciones = subscripciones.findAll();
        
        for(int i=0; i<listaSuscripciones.size();i++) {
        	List<List<String>> mensajes = listaSuscripciones.get(i).getComentarios();
        	for(int j=0; j<mensajes.size();j++) {
        		List<String> msnjs = mensajes.get(j);
        		msnjs.add(fecha);
        		msnjs.add(tiempo);
        		System.out.println(msnjs);
        		mensajes.set(j, msnjs);
        	}
        	listaSuscripciones.get(i).setComentarios(mensajes);
        	subscripciones.save(listaSuscripciones.get(i));
        }
	
	}


}

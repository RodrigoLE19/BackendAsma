package com.Rodrigo.RespiraFacilAPI;

import com.Rodrigo.RespiraFacilAPI.entities.Usuario;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RespiraFacilApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RespiraFacilApiApplication.class, args);

		Usuario usuario= new Usuario();
	}

}

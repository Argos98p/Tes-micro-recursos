package com.turisup.resources;

import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import com.complexible.stardog.jena.SDJenaFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.VCARD;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ResourcesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResourcesApplication.class, args);
	/*
		try (AdminConnection aAdminConnection = AdminConnectionConfiguration.toServer("https://sd-e3dfa127.stardog.cloud:5820")
				.credentials("ricardo", "Chocolate619")
				.connect()) {
			// If the database already exists, we'll drop it and create a fresh copy
			if (aAdminConnection.list().contains("Turismo2")) {


			}
			try (

					Connection aConn = ConnectionConfiguration
							.to("Turismo2")
							.server("https://sd-e3dfa127.stardog.cloud:5820")
							.credentials("ricardo", "Chocolate619")
							.connect()){

				System.out.println("works");

				// some definitions
				String personURI    = "http://turis-ucuenca/user/0000/";
				String fullName     = "Ricardo Jarro";


				Model model =SDJenaFactory.createModel(aConn);
				model.begin();
				Resource person = model.createResource(personURI);
				person.addProperty(RDF.type, FOAF.Person);
				person.addProperty(FOAF.name,"Test usuario");
				person.addProperty(FOAF.mbox,"test-usuario@gmail.com");
				person.addProperty(FOAF.nick,"nick-test");
				model.commit();

			}



		}finally {

			}*/
	}

}

package com.turisup.resources.repository;

import com.complexible.stardog.StardogException;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;

public class DBConnection {

    public static Connection createConnection()  {
            return  ConnectionConfiguration
                    .to("Turismo2")
                    .server("https://sd-e3dfa127.stardog.cloud:5820")
                    .credentials("ricardo", "Chocolate619")
                    .connect();
    }
}

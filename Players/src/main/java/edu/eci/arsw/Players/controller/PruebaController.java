package edu.eci.arsw.Players.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
@RequestMapping("/pruebas")
public class PruebaController {

    @GetMapping()
    public String prueba() {
        return "PRUEBA DE CONTROLADOR";
    }

}
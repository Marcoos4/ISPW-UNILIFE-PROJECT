package it.ispw.unilife.graphicontrollercli;

import it.ispw.unilife.exception.DAOException;

import java.util.Scanner;

public interface CLIContoller {
    void start(Scanner scanner) throws DAOException;
}

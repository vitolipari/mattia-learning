package com.example.demo.rest;

import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.*;

@RestController
@RequestMapping("/")
public class FunRestController {
    //inserimento parte autenticazione
    private final String filename = "/Users/mattiaartioli/Desktop/UserFile.txt";

    public static NavigableMap<String, String> fileToMap (String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String userLine;
        TreeMap<String, String> userMap = new TreeMap<>();
        while ((userLine = reader.readLine()) != null) {
            String[] users = userLine.split(";");
            for(String user : users) {
                String[] kv = user.split(",");
                userMap.put(kv[0],kv[1]);
            }
        }

        reader.close();
        return userMap;
    }
    public static String fileToString(String filePath) throws Exception{
        String input;
        Scanner sc = new Scanner(new File(filePath));
        StringBuffer sb = new StringBuffer();
        while (sc.hasNextLine()) {
            input = sc.nextLine();
            sb.append(input);
        }
        return sb.toString();
    }
    //map endpoint url
    @GetMapping("/users")
    public String getUsers() throws IOException {
        NavigableMap<String, String> userMap = fileToMap(filename);
        return userMap.values().toString();

    }

    @GetMapping("/user/{userId}")
    public String getUserById(@PathVariable("userId") Integer userId) throws IOException {
        NavigableMap<String, String> userMap = fileToMap(filename);
        String userFound = userMap.get(userId.toString());
        return Objects.requireNonNullElse(userFound, "Utente non trovato");
    }

    @PostMapping("/user")
    public String saveNewUser(@RequestParam(value = "name") String name) throws IOException {
        NavigableMap<String, String> userMap = fileToMap(filename);
        int newUserId = Integer.parseInt(userMap.lastKey())+1;
        String newUser = newUserId + "," + name + ";";
        FileWriter fw = new FileWriter(filename,true);
        fw.write(newUser);
        fw.close();
        return Integer.toString(newUserId);
    }

    @PutMapping("/user/{userId}")
    public String changeUserName(@PathVariable("userId") Integer userId, @RequestParam(value = "name") String name) throws Exception {
        NavigableMap<String, String> userMap = fileToMap(filename);
        if (userMap.containsKey(userId.toString())) {
            String userNameToChange = userMap.get(userId.toString());
            String myFileInString = fileToString(filename);
            String toChange = userId+","+userNameToChange+";";
            String changed = userId+","+name+";";
            String newFile = myFileInString.replace(toChange, changed);
            FileWriter fw = new FileWriter(filename);
            fw.write(newFile);
            fw.close();
            return "Modificato nome utente con id: " + userId + " con il nome di: " + name;
        } else {
            return "Utente non trovato";
        }
    }

    @DeleteMapping("/user/{userId}")
    public String deleteUserById(@PathVariable("userId") Integer userId) throws Exception {
        NavigableMap<String, String> userMap = fileToMap(filename);
        String userNameRemoved = userMap.get(userId.toString());
        String userRemoved = userMap.remove(userId.toString());

        String myFileInString = fileToString(filename);
        String toDelete = userId+","+userNameRemoved+";";
        String newFile = myFileInString.replace(toDelete, "");
        FileWriter fw = new FileWriter(filename);
        fw.write(newFile);
        fw.close();
        return userRemoved != null ? "Rimosso utente " + userNameRemoved + " con id: " + userId : "Utente non trovato";
    }

}

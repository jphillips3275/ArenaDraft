/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *
 * FXML Doc controller for ArenaDraft
 */
package arenadraft;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author Jackson
 */

public class FXMLDocumentController implements Initializable {
    
    @FXML
    private Text title;
    @FXML
    private ImageView leftImage;
    @FXML
    private ImageView middleImage;      //slots for the left, middle, and right images to be displayed
    @FXML
    private ImageView rightImage;
    
    Image hero1, hero2, hero3;
    String class1, class2, class3;
    String save1, save2, save3;
    boolean started = false;        //helps us differentiate to display either the heros or the cards
    int n = 1;                      //keeps track of what card we're on so we don't go above 30
    File clickSound = new File("src/OtherArtOrSounds/Hub_Click.wav");
    //Ban order: 0Classic, 1nax, 2goblins, 3blackrock, 4grand tournament, 5explorers, 6old gods, 7karazhan, 8gagetzan, 9ungoro, 10frozenthrone, 11kobolds, 12witchwood, 13boomsday, 14rumble, 15rise of shadows, 16uldum, 17dragons, 18outland, 19scholomance, 20darkmoon, 21core, 22barrens
    Boolean[] bans = {false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, true, false, false, true, true, true, true, true};
    int ExpantionRandMax = 23;      //keeps track of the number of expantions in the pool
    int DHExpantionRandMax = 22;    //does the same but different to account for the way demon hunter is calculated
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        started = false;
        int index1 = randomPortrait();
        int index2 = randomPortrait();          //makes sure he hero portraits aren't duplicated
        while(index2 == index1){
            index2 = randomPortrait();
        }
        int index3 = randomPortrait();
        while(index3 == index1 || index3 == index2){
            index3 = randomPortrait();
        }
        class1 = startHeroImage(leftImage, index1);
        class2 = startHeroImage(middleImage, index2);        //puts the hero image on the screen
        class3 = startHeroImage(rightImage, index3);
        save1 = class1; save2 = class2; save3 = class3;      //keeps track of what class we're using for the purpose of card paths and saving to the text document
    }
    
    public void onClickImage1() throws IOException{          //all this happens if we click the left card
        try {
            playSound(clickSound);
        } catch (MalformedURLException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (LineUnavailableException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);          //all this makes a noise when you click
        }
        switch (n){
            case 30:
                title.setText("Select your final card");            //if we're on our last card, set the text to that
                break;
            default:
                title.setText("Select card number " + n);           //normally it just tells us what card we're on
        }
        if (n > 30){
            title.setText("Your deck is finished");
            if (n == 31){
                save1 = trimPath(save1, class1);                //if we're above 30 cards, stops all interaction except for saving the last card
                save(save1, n);
                n++;                                            //still need n++ because otherwise we're stuck saving every single time we click a card since n = 31
            }
            return;
        }
        if (started == false){
            save(save1, n);
            class2 = class1;                                //just saves the name of the class to the text doc and makes sure there won't be fuck ups with the other saves
            class3 = class1;                                //only does this if we haven't started yet, then tells the program we have started
            started = true;
        } else {
            save1 = trimPath(save1, class1);                //saves the card we chose to the text document
            save(save1, n);
        }
        
        String path = getRandomCard(class1);
        String path2 = getRandomCard(class1);              //gets us a path to a random card that exists in our files
        String path3 = getRandomCard(class1);
        while (path.equals(path2) || path.equals(path3) || path2.equals(path3)){
            path = getRandomCard(class1);
            path2 = getRandomCard(class1);                  //if any cards are duplicated, reroll all cards until we get different cards every time
            path3 = getRandomCard (class1);
        }
        if ("Legendary".equals(path) || "Legendary".equals(path2) || "Legendary".equals(path3))
        {
            path = getLegendaryCard(class1);
            path2 = getLegendaryCard(class1);               //if any of the cards roll a legendary, make every card a legendary
            path3 = getLegendaryCard(class1);
            while (path.equals(path2) || path.equals(path3) || path2.equals(path3)){
                path = getLegendaryCard(class1);
                path2 = getLegendaryCard(class1);           //if any of the cards are the same, roll new legendaries
                path3 = getLegendaryCard (class1);
            }
        }
        save1 = path; save2 = path2; save3 = path3;         //sets up the card paths to be saved to the text doc
        drawCards(path, leftImage);
        drawCards(path2, middleImage);                      //puts the image of all 3 selected cards on the screen
        drawCards(path3, rightImage);
        n++;                                                //increments the number of cards we're at
    }
    public void onClickImage2() throws IOException{         //does the same as onClickImage1() but focusing on the middle image instead
        try {
            playSound(clickSound);
        } catch (MalformedURLException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (LineUnavailableException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        switch (n){
            case 30:
                title.setText("Select your final card");
                break;
            default:
                title.setText("Select card number " + n);
        }
        if (n > 30){
            title.setText("Your deck is finished");
            if (n == 31){
                save2 = trimPath(save2, class2);
                save(save2, n);
                n++;
            }
            return;
        }
        if (started == false){
            save(save2, n);
            class1 = class2;
            class3 = class2;
            started = true;
        } else {
            save2 = trimPath(save2, class2);
            save(save2, n);
        }
        
        String path = getRandomCard(class2);
        String path2 = getRandomCard(class2);
        String path3 = getRandomCard(class2);
        while (path.equals(path2) || path.equals(path3) || path2.equals(path3)){
            path = getRandomCard(class2);
            path2 = getRandomCard(class2);
            path3 = getRandomCard (class2);
        }
        if ("Legendary".equals(path) || "Legendary".equals(path2) || "Legendary".equals(path3))
        {
            path = getLegendaryCard(class2);
            path2 = getLegendaryCard(class2);
            path3 = getLegendaryCard(class2);
            while (path.equals(path2) || path.equals(path3) || path2.equals(path3)){
                path = getLegendaryCard(class2);
                path2 = getLegendaryCard(class2);
                path3 = getLegendaryCard (class2);
            }
        }
        save1 = path; save2 = path2; save3 = path3;
        drawCards(path, leftImage);
        drawCards(path2, middleImage);
        drawCards(path3, rightImage);
        n++;
    }
    public void onClickImage3() throws IOException{                 //does the same as onClickImage() 1 and 2 but focused on image 3
        try {
            playSound(clickSound);
        } catch (MalformedURLException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (LineUnavailableException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        switch (n){
            case 30:
                title.setText("Select your final card");
                break;
            default:
                title.setText("Select card number " + n);
        }
        if (n > 30){
            title.setText("Your deck is finished");
            if (n == 31){
                save3 = trimPath(save3, class3);
                save(save3, n);
                n++;
            }
            return;
        }
        if (started == false){
            save(save3, n);
            class1 = class3;
            class2 = class3;
            started = true;
        }  else {
            save3 = trimPath(save3, class3);
            save(save3, n);
        }
        String path = getRandomCard(class3);
        String path2 = getRandomCard(class3);
        String path3 = getRandomCard(class3);
        while (path.equals(path2) || path.equals(path3) || path2.equals(path3)){
            path = getRandomCard(class3);
            path2 = getRandomCard(class3);
            path3 = getRandomCard (class3);
        }
        if ("Legendary".equals(path) || "Legendary".equals(path2) || "Legendary".equals(path3))
        {
            path = getLegendaryCard(class3);
            path2 = getLegendaryCard(class3);
            path3 = getLegendaryCard(class3);
            while (path.equals(path2) || path.equals(path3) || path2.equals(path3)){
                path = getLegendaryCard(class3);
                path2 = getLegendaryCard(class3);
                path3 = getLegendaryCard (class3);
            }
        }
        save1 = path; save2 = path2; save3 = path3;
        drawCards(path, leftImage);
        drawCards(path2, middleImage);
        drawCards(path3, rightImage);
        n++;
    }
    
    public void save(String data, int n) throws IOException{            //this is what saves the names to the text doc
        File file = new File("deckList.txt");       //creates us a new file
        if (started == false){
            file.delete();                          //deletes any past files at the beginning of the program
        }
        if(file.createNewFile()){
            System.out.println("File created");     //tells us the file has been created
        } else {
            System.out.println(data);               //tells us what card is being printed to the file
        }
        if (n == 1){
            data = data + "\n";                     //if this is the name of the class (n = 0), just do a new line after the word
        } else {
            data = n-1 + ") " + data + "\n";          //if this is a card (any other n) print the card number we're on, then the card name
        }
        Files.write(Paths.get("deckList.txt"), data.getBytes(), StandardOpenOption.APPEND);         //write that stuff to the file
    }
    
    public String trimPath(String path, String heroClass){          //this whole thing is dumb but it removes parts of the file path to make it just the names we want for saving
        String newString = path.replace("src\\arenadraft\\cards\\", "");        //need to find a way to make this whole method more efficient
        newString = newString.replace("Common\\", ""); newString = newString.replace("Rare\\", ""); 
        newString = newString.replace("Epic\\", ""); newString = newString.replace("Legendary\\", "");
        newString = newString.replace(heroClass + "\\", ""); newString = newString.replace("Neutral\\", "");
        newString = newString.replace(".png", "");
        newString = newString.replace("BlackrockMountain\\", ""); newString = newString.replace("Boomsday\\", "");
        newString = newString.replace("Classic\\", ""); newString = newString.replace("Darkmoon\\", "");
        newString = newString.replace("Dragons\\", ""); newString = newString.replace("Explorers\\", "");
        newString = newString.replace("FrozenThrone\\", ""); newString = newString.replace("Gagetzan\\", "");
        newString = newString.replace("GoblinsVsGnomes\\", ""); newString = newString.replace("GrandTournament\\", "");
        newString = newString.replace("Karazhan\\", ""); newString = newString.replace("Kobolds\\", "");
        newString = newString.replace("Naxxramas\\", ""); newString = newString.replace("OldGods\\", "");
        newString = newString.replace("Outland\\", ""); newString = newString.replace("RiseOfShadows\\", "");
        newString = newString.replace("Rumble\\", ""); newString = newString.replace("Scholomance\\", "");
        newString = newString.replace("Uldum\\", ""); newString = newString.replace("UnGoro\\", "");
        newString = newString.replace("Witchwood\\", ""); newString = newString.replace("Barrens\\", "");
        newString = newString.replace("Core\\", "");
        
        return newString;       //returns the string that's just the name of the file without any of the extra fluff from the path
    }
    
    public void drawCards(String path, ImageView side){     //this puts all the cards on the screen, side being if we're putting it on the left, middle, or right
        String newString = path.replace("\\", "/");
        newString = newString.replace("src/", "");          //changes parts of the path so the setImage will function
        newString = newString.replace("arenadraft/", "");
        Image card = new Image(getClass().getResourceAsStream(newString));
        side.setImage(card);
    }
    
    public String getRandomCard(String heroClass){         //this will get us a random card from our chosen class
        Random rand = new Random();
        int expantion = rand.nextInt(ExpantionRandMax);     //gets us a random expantion number
        while(bans[expantion] == false){            //tied to the array of bools at the top of the program
            expantion = rand.nextInt(ExpantionRandMax);     //if the expaniton isn't enabled, get us another number
        }
        if ("DemonHunter".equals(heroClass)){       //demon hunter is special since it doesn't have all the expantions
            expantion = rand.nextInt((DHExpantionRandMax - 18) + 1) + 18; //((max - min) + 1) + min
            while(bans[expantion] == false){
            expantion = rand.nextInt((DHExpantionRandMax - 18) + 1) + 18;       //gets us an expantion number compatable with demon hunter
        }
        }
        int rarity = rand.nextInt(101);             //gets us a number from 0 - 100 for the rarity, to account for likelihood of showing up
        int neutral = rand.nextInt(2);              //decides if the card is going to be a class card or neutral
        
        String expantionString = "oops";
        String rarityString = "oops";           //if something fucks up, this default will let us know
        
        if (neutral == 0){          //if the card is neutral
            heroClass = "Neutral";
        }
        switch (expantion){         //get us the string of the expantion tied to the random expantion number
            case 0:
                expantionString = "Classic";
                break;
            case 1:
                expantionString = "Naxxramas";
                break;
            case 2:
                expantionString = "GoblinsVsGnomes";
                break;
            case 3:
                expantionString = "BlackrockMountain";
                break;
            case 4:
                expantionString = "GrandTournament";
                break;
            case 5:
                expantionString = "Explorers";
                break;
            case 6:
                expantionString = "OldGods";
                break;
            case 7:
                expantionString = "Karazhan";
                break;
            case 8:
                expantionString = "Gagetzan";
                break;
            case 9:
                expantionString = "UnGoro";
                break;
            case 10:
                expantionString = "FrozenThrone";
                break;
            case 11:
                expantionString = "Kobolds";
                break;
            case 12:
                expantionString = "Witchwood";
                break;
            case 13:
                expantionString = "Boomsday";
                break;
            case 14:
                expantionString = "Rumble";
                break;
            case 15:
                expantionString = "RiseOfShadows";
                break;
            case 16:
                expantionString = "Uldum";
                break;
            case 17:
                expantionString = "Dragons";
                break;
            case 18:
                expantionString = "Outland";
                break;
            case 19:
                expantionString = "Scholomance";
                break;
            case 20:
                expantionString = "Darkmoon";
                break;
            case 21:
                expantionString = "Core";
                break;
            case 22:
                expantionString = "Barrens";
                break;
        }
        switch (expantion){     //this is necessary because the early adventures don't have cards in every rarity
            case 1:     //if the expantion is naxx
                if (!"Neutral".equals(heroClass)){
                    rarityString = "Common";        //if the card isn't neutral, the card has to be common, because thats the only rarity naxx class cards are
                } else {
                    rarityString = defaultRaritySwitch(rarity, rarityString);   //if it is neutral, just point us to the normal rarity distribution
                    if ("Legendary".equals(rarityString)){
                        return rarityString;  //we only need the word legendary if it is so we can end here since we have to go to a different method to generate a legendary
                    }
                }
                break;
            case 3:         //if the card is from blackrock
                if ("Neutral".equals(heroClass)){
                    if (isBetween(rarity, 0, 79)){
                    rarityString = "Common";
                    }
                    if (isBetween(rarity, 80, 99)){
                        rarityString = "Rare";      //blackrock didn't have any epic so those are gone
                    }
                    if (rarity == 100){
                        rarityString = "Legendary";
                        return rarityString;
                    }
                } else {                //class cards only got commons or rares
                    if (isBetween(rarity, 0, 79)){
                    rarityString = "Common";
                    }
                    if (isBetween(rarity, 80, 100)){
                        rarityString = "Rare";
                    }
                }
                break;
            case 5:             //explorers neutrals didn't have any epic either
                if (!"Netural".equals(heroClass)){
                    if (isBetween(rarity, 0, 79)){
                    rarityString = "Common";
                    }
                    if (isBetween(rarity, 80, 100)){
                        rarityString = "Rare";
                    }
                } else {        //but the class cards were normal so they get the normal switch statement
                    rarityString = defaultRaritySwitch(rarity, rarityString);
                    if ("Legendary".equals(rarityString)){
                        return rarityString;
                    }
                }
                break;
            case 7:             //kharazhan only had commons and rares
                if (!"Netural".equals(heroClass)){
                    if (isBetween(rarity, 0, 79)){
                    rarityString = "Common";
                    }
                    if (isBetween(rarity, 80, 100)){
                        rarityString = "Rare";
                    }
                } else {            //class cards were normal so they get the default
                    rarityString = defaultRaritySwitch(rarity, rarityString);
                    if ("Legendary".equals(rarityString)){
                        return rarityString;
                    }
                }
                break;
            default:            //if it's not an adventure, go straight to the default rarity switch
                rarityString = defaultRaritySwitch(rarity, rarityString);
                if ("Legendary".equals(rarityString)){
                    return rarityString;
                }
        }
        
        String finalPath = heroClass + "/" + expantionString + "/" + rarityString + "/";       
        Path path = Paths.get("src/arenadraft/cards/" + finalPath);         //puts together the class, expantion, and rarity to give us a path to pick our card
        File f = path.toFile();     //converts the path variable to a file variable
        File[] files = f.listFiles();   //gives us the number of files in the path we generated
        File file = files[rand.nextInt(files.length)];  //picks a random card out of the ones at that path
        finalPath = file.toPath().toString();       //changes the card path to a string and makes that the final path
        return finalPath;
    }
    
    public String defaultRaritySwitch(int rarity, String rarityString){         //the default likelihood of getting each rarity
        if (isBetween(rarity, 0, 79)){
            rarityString = "Common";        //0-79 you get a common
        }
        if (isBetween(rarity, 80, 94)){
            rarityString = "Rare";          //80-94 you get a rare
        }
        if (isBetween(rarity, 95, 99)){
            rarityString = "Epic";          //95-99 you get an epic
        }
        if (rarity == 100){
            rarityString = "Legendary";     //100 you get a legendadry
            return rarityString;
        }
        return rarityString;
    }
    
    public String getLegendaryCard(String heroClass){       //basically just the random generation method but only gets legendary cards
        Random rand = new Random();
        int expantion = rand.nextInt(ExpantionRandMax);
        while(bans[expantion] == false){
            expantion = rand.nextInt(ExpantionRandMax);
        }
        if ("DemonHunter".equals(heroClass)){
            expantion = rand.nextInt((DHExpantionRandMax - 18) + 1) + 18;
            while(bans[expantion] == false){
                expantion = rand.nextInt((DHExpantionRandMax - 18) + 1) + 18;
            }
        }
        int neutral = rand.nextInt(2);
        
        String expantionString = "oops";
        String rarityString = "Legendary";
        String finalPath;
        
        if (neutral == 0){
            heroClass = "Neutral";
            switch (expantion){
                case 0:
                    expantionString = "Classic";
                    break;
                case 1:
                    expantionString = "Naxxramas";
                    break;
                case 2:
                    expantionString = "GoblinsVsGnomes";
                    break;
                case 3:
                    expantionString = "BlackrockMountain";
                    break;
                case 4:
                    expantionString = "GrandTournament";
                    break;
                case 5:
                    expantionString = "Explorers";
                    break;
                case 6:
                    expantionString = "OldGods";
                    break;
                case 7:
                    expantionString = "Karazhan";
                    break;
                case 8:
                    expantionString = "Gagetzan";
                    break;
                case 9:
                    expantionString = "UnGoro";
                    break;
                case 10:
                    expantionString = "FrozenThrone";
                    break;
                case 11:
                    expantionString = "Kobolds";
                    break;
                case 12:
                    expantionString = "Witchwood";
                    break;
                case 13:
                    expantionString = "Boomsday";
                    break;
                case 14:
                    expantionString = "Rumble";
                    break;
                case 15:
                    expantionString = "RiseOfShadows";
                    break;
                case 16:
                    expantionString = "Uldum";
                    break;
                case 17:
                    expantionString = "Dragons";
                    break;
                case 18:
                    expantionString = "Outland";
                    break;
                case 19:
                    expantionString = "Scholomance";
                    break;
                case 20:
                    expantionString = "Darkmoon";
                    break;
                case 21:
                    expantionString = "Core";
                    break;
                case 22:
                    expantionString = "Barrens";
                    break;
            }
        } else {
            expantion = rand.nextInt(ExpantionRandMax - 4);
            while (bans[expantion] == false){
                expantion = rand.nextInt(ExpantionRandMax - 4);
            }
            if ("DemonHunter".equals(heroClass)){
                expantion = rand.nextInt(((DHExpantionRandMax - 4) - 14) + 1) + 14;
                while (bans[expantion] == false){
                    expantion = rand.nextInt(((DHExpantionRandMax - 4) - 14) + 1) + 14;
                }
            }
            switch (expantion){
                case 0:
                    expantionString = "Classic";
                    break;
                case 1:
                    expantionString = "GoblinsVsGnomes";
                    break;
                case 2:
                    expantionString = "GrandTournament";
                    break;
                case 3:
                    expantionString = "OldGods";
                    break;
                case 4:
                    expantionString = "Gagetzan";
                    break;
                case 5:
                    expantionString = "UnGoro";
                    break;
                case 6:
                    expantionString = "FrozenThrone";
                    break;
                case 7:
                    expantionString = "Kobolds";
                    break;
                case 8:
                    expantionString = "Witchwood";
                    break;
                case 9:
                    expantionString = "Boomsday";
                    break;
                case 10:
                    expantionString = "Rumble";
                    break;
                case 11:
                    expantionString = "RiseOfShadows";
                    break;
                case 12:
                    expantionString = "Uldum";
                    break;
                case 13:
                    expantionString = "Dragons";
                    break;
                case 14:
                    expantionString = "Outland";
                    break;
                case 15:
                    expantionString = "Scholomance";
                    break;
                case 16:
                    expantionString = "Darkmoon";
                    break;
                case 17:
                    expantionString = "Core";
                    break;
                case 18:
                    expantionString = "Barrens";
                    break;
            }
        }
        
        finalPath = heroClass + "/" + expantionString + "/" + rarityString + "/";
        Path path = Paths.get("src/arenadraft/cards/" + finalPath);
        File f = path.toFile();
        File[] files = f.listFiles();
        File file = files[rand.nextInt(files.length)];
        finalPath = file.toPath().toString();
        return finalPath;
    }
    
    public int randomPortrait(){            //gives us a random hero portrait
        Path path = Paths.get("src/arenadraft/Heroes");
        File f = path.toFile();
        File[] files = f.listFiles();       //lists the number of files in the heroes file
        Random rand = new Random();
        
        return rand.nextInt(files.length);          //returns the index of one of the hero portraits in the Hereos file
    }
    
    public String startHeroImage(ImageView side, int index){        //puts the hero image on the screen
        Path path = Paths.get("src/arenadraft/Heroes");
        File f = path.toFile();
        File[] files = f.listFiles();
        File file = files[index];           //takes the index form randomPortrait() and gets the file
        
        String newString = file.toPath().toString().replace("src\\","");
        newString = newString.replace("arenadraft\\","");
        newString = newString.replace("Heroes\\","");      //trim off some bits so we can see exactly what hero we have
        
        Image heroPortrait = new Image(getClass().getResourceAsStream("Heroes/" + newString));      //sets our image to the hero portrait
        side.setImage(heroPortrait);
        
        String finalTarget = ".png";        //trim off some more
        newString = newString.replace(finalTarget,"");
        return newString;           //return a string that is the name of this portrait's hero
    }
    
    public boolean isBetween(int x, int lower, int upper){      //we use this when deciding the rarity
        return lower <= x && x <= upper;
    }
    
    public void toggleClassic(){            //all of these toggle which expantions are enabled and disabled on and off
        if (bans[0] == false){
            bans[0] = true;
        } else {
            bans[0] = false;
        }
    }
    public void toggleNax(){
        if (bans[1] == false){
            bans[1] = true;
        } else {
            bans[1] = false;
        }
    }
    public void toggleGoblins(){
        if (bans[2] == false){
            bans[2] = true;
        } else {
            bans[2] = false;
        }
    }
    public void toggleBlackrock(){
        if (bans[3] == false){
            bans[3] = true;
        } else {
            bans[3] = false;
        }
    }
    public void toggleTournament(){
        if (bans[4] == false){
            bans[4] = true;
        } else {
            bans[4] = false;
        }
    }
    public void toggleExplorers(){
        if (bans[5] == false){
            bans[5] = true;
        } else {
            bans[5] = false;
        }
    }
    public void toggleOldGods(){
        if (bans[6] == false){
            bans[6] = true;
        } else {
            bans[6] = false;
        }
    }
    public void toggleKarazhan(){
        if (bans[7] == false){
            bans[7] = true;
        } else {
            bans[7] = false;
        }
    }
    public void toggleGadgetzan(){
        if (bans[8] == false){
            bans[8] = true;
        } else {
            bans[8] = false;
        }
    }
    public void toggleUngoro(){
        if (bans[9] == false){
            bans[9] = true;
        } else {
            bans[9] = false;
        }
    }
    public void toggleFrozenThrone(){
        if (bans[10] == false){
            bans[10] = true;
        } else {
            bans[10] = false;
        }
    }
    public void toggleKobolds(){
        if (bans[11] == false){
            bans[11] = true;
        } else {
            bans[11] = false;
        }
    }
    public void toggleWitchwood(){
        if (bans[12] == false){
            bans[12] = true;
        } else {
            bans[12] = false;
        }
    }
    public void toggleBoomsday(){
        if (bans[13] == false){
            bans[13] = true;
        } else {
            bans[13] = false;
        }
    }
    public void toggleRumble(){
        if (bans[14] == false){
            bans[14] = true;
        } else {
            bans[14] = false;
        }
    }
    public void toggleShadows(){
        if (bans[15] == false){
            bans[15] = true;
        } else {
            bans[15] = false;
        }
    }
    public void toggleUldum(){
        if (bans[16] == false){
            bans[16] = true;
        } else {
            bans[16] = false;
        }
    }
    public void toggleDragons(){
        if (bans[17] == false){
            bans[17] = true;
        } else {
            bans[17] = false;
        }
    }
    public void toggleOutland(){
        if (bans[18] == false){
            bans[18] = true;
        } else {
            bans[18] = false;
        }
    }
    public void toggleScholomance(){
        if (bans[19] == false){
            bans[19] = true;
        } else {
            bans[19] = false;
        }
    }
    public void toggleDarkmoon(){
        if (bans[20] == false){
            bans[20] = true;
        } else {
            bans[20] = false;
        }
    }
    public void toggleCore(){
        if (bans[21] == false){
            bans[21] = true;
        } else {
            bans[21] = false;
        }
    }
    public void toggleBarrens(){
        if (bans[22] == false){
            bans[22] = true;
        } else {
            bans[22] = false;
        }
    }
    
    public void hoversound() throws UnsupportedAudioFileException, IOException, MalformedURLException, LineUnavailableException{        //plays a sound when hovering over a card
        File file = new File("src/OtherArtOrSounds/Hub_MouseOver.wav");
        playSound(file);
    }
    
    public void playSound(File file) throws MalformedURLException, UnsupportedAudioFileException, IOException, LineUnavailableException{        //plays a sound, any sound
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(file.toURI().toURL());
        Clip clip = AudioSystem.getClip();
        clip.open(audioIn);
        clip.start();
    }
}
